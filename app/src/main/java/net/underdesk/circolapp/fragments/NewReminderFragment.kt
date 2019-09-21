/*
 * Circolapp
 * Copyright (C) 2019  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.underdesk.circolapp.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.AlarmManagerCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_reminder.*
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import java.util.*


class NewReminderFragment : DialogFragment() {

    companion object {
        private const val CIRCULAR = "circular"

        fun create(circular: Circular): NewReminderFragment {
            val dialog = NewReminderFragment()
            dialog.arguments = Bundle().apply {
                putParcelable(CIRCULAR, circular)
            }
            return dialog
        }
    }

    private var dateNotChosen = true
    var circular: Circular? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        circular = arguments?.getParcelable(CIRCULAR)
        return inflater.inflate(R.layout.dialog_reminder, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog_time_picker.setIs24HourView(true)

        dialog_ok_button.setOnClickListener { next() }
        dialog_cancel_button.setOnClickListener { dismiss() }
    }

    fun next() {
        if (dateNotChosen) {
            dialog_date_picker.visibility = View.GONE
            dialog_time_picker.visibility = View.VISIBLE
            dialog_ok_button.text = getString(R.string.dialog_ok)
            dateNotChosen = false
        } else {
            val calendar = Calendar.getInstance()
            val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog_time_picker.hour
            } else {
                dialog_time_picker.currentHour
            }

            val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog_time_picker.minute
            } else {
                dialog_time_picker.currentMinute
            }

            calendar.set(
                dialog_date_picker.year,
                dialog_date_picker.month,
                dialog_date_picker.dayOfMonth,
                hour,
                minute
            )

            object : Thread() {
                override fun run() {
                    context?.let { context ->
                        circular?.let { circular ->
                            AppDatabase.getInstance(context).circularDao()
                                .update(circular.apply { reminder = true })

                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                circular.id.toInt(),
                                Intent(context, AlarmBroadcastReceiver::class.java)
                                    .putExtra(AlarmBroadcastReceiver.CIRCULAR_ID, circular.id),
                                0
                            )

                            AlarmManagerCompat.setExactAndAllowWhileIdle(
                                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                    dismiss()
                }
            }.start()
        }
    }
}