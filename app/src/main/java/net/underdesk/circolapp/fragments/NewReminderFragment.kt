/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AndroidDatabase
import net.underdesk.circolapp.databinding.DialogReminderBinding
import net.underdesk.circolapp.shared.data.Circular
import java.util.*

class NewReminderFragment : DialogFragment() {

    companion object {
        fun create(circular: Circular): NewReminderFragment {
            val dialog = NewReminderFragment()
            dialog.circular = circular
            return dialog
        }
    }

    private var _binding: DialogReminderBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var dateNotChosen = true
    lateinit var circular: Circular

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogTimePicker.setIs24HourView(true)

        binding.dialogOkButton.setOnClickListener { next() }
        binding.dialogBackButton.setOnClickListener { back() }
        binding.dialogCancelButton.setOnClickListener { dismiss() }
    }

    fun next() {
        if (dateNotChosen) {
            binding.dialogDatePicker.visibility = View.GONE
            binding.dialogTimePicker.visibility = View.VISIBLE
            binding.dialogBackButton.visibility = View.VISIBLE
            binding.dialogOkButton.text = getString(R.string.dialog_ok)
            dateNotChosen = false
        } else {
            val calendar = Calendar.getInstance()
            val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.dialogTimePicker.hour
            } else {
                binding.dialogTimePicker.currentHour
            }

            val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.dialogTimePicker.minute
            } else {
                binding.dialogTimePicker.currentMinute
            }

            calendar.set(
                binding.dialogDatePicker.year,
                binding.dialogDatePicker.month,
                binding.dialogDatePicker.dayOfMonth,
                hour,
                minute
            )

            lifecycleScope.launch {
                context?.let { context ->
                    circular.let { circular ->
                        AndroidDatabase.getDaoInstance(context)
                            .update(circular.id, circular.school, circular.favourite, true)

                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            circular.id.toInt(),
                            Intent(context, AlarmBroadcastReceiver::class.java)
                                .putExtra(AlarmBroadcastReceiver.CIRCULAR_ID, circular.id)
                                .putExtra(AlarmBroadcastReceiver.SCHOOL_ID, circular.school),
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
        }
    }

    private fun back() {
        binding.dialogDatePicker.visibility = View.VISIBLE
        binding.dialogTimePicker.visibility = View.GONE
        binding.dialogBackButton.visibility = View.GONE
        binding.dialogOkButton.text = getString(R.string.dialog_next)
        dateNotChosen = true
    }
}
