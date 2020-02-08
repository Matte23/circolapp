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

package net.underdesk.circolapp.adapters

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_circular.view.*
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.fragments.NewReminderFragment


class CircularLetterAdapter(
    private var circulars: List<Circular>,
    private val adapterCallback: AdapterCallback
) :
    RecyclerView.Adapter<CircularLetterAdapter.CircularLetterViewHolder>() {
    private lateinit var context: Context
    private var collapsedItems = -1

    init {
        setHasStableIds(true)
    }

    inner class CircularLetterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var card: CardView = view.circular_card
        var title: TextView = view.circular_title_textview
        var number: TextView = view.circular_number_textview
        var date: TextView = view.circular_date_textview
        var collapseButton: ImageButton = view.circular_collapse_button
        var viewButton: ImageButton = view.circular_view_button
        var downloadButton: ImageButton = view.circular_download_button
        var favouriteButton: ImageButton = view.circular_favourite_button
        var reminderButton: ImageButton = view.circular_reminder_button
        var attachmentsList: RecyclerView = view.circulars_attachments_list

        init {
            attachmentsList.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularLetterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circular, parent, false)
        context = parent.context

        return CircularLetterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircularLetterViewHolder, position: Int) {
        holder.number.text = context.getString(R.string.notification_title, circulars[position].id)
        holder.title.text = circulars[position].name
        holder.date.text = circulars[position].date

        holder.favouriteButton.setImageResource(
            if (circulars[position].favourite) {
                holder.favouriteButton.contentDescription =
                    context.getString(R.string.image_remove_favourite)
                R.drawable.baseline_star_24
            } else {
                holder.favouriteButton.contentDescription =
                    context.getString(R.string.image_add_favourite)
                R.drawable.baseline_star_border_24
            }
        )

        holder.reminderButton.setImageResource(
            if (circulars[position].reminder) {
                holder.reminderButton.contentDescription =
                    context.getString(R.string.image_remove_reminder)
                R.drawable.baseline_notifications_active_24
            } else {
                holder.reminderButton.contentDescription =
                    context.getString(R.string.image_add_reminder)
                R.drawable.baseline_notifications_none_24
            }
        )

        if (collapsedItems != position) {
            holder.collapseButton.setImageDrawable(context.getDrawable(R.drawable.baseline_expand_more_24))
            holder.collapseButton.contentDescription = context.getString(R.string.image_expand)

            holder.viewButton.visibility = View.GONE
            holder.downloadButton.visibility = View.GONE
            holder.favouriteButton.visibility = View.GONE
            holder.reminderButton.visibility = View.GONE

            holder.attachmentsList.visibility = View.GONE
            holder.attachmentsList.adapter = null
        } else {
            holder.collapseButton.setImageDrawable(context.getDrawable(R.drawable.baseline_expand_less_24))
            holder.collapseButton.contentDescription = context.getString(R.string.image_collapse)

            holder.viewButton.visibility = View.VISIBLE
            holder.downloadButton.visibility = View.VISIBLE
            holder.favouriteButton.visibility = View.VISIBLE
            holder.reminderButton.visibility = View.VISIBLE

            if (circulars[position].attachmentsNames.isNotEmpty()) {
                holder.attachmentsList.visibility = View.VISIBLE
                holder.attachmentsList.adapter = AttachmentAdapter(
                    circulars[position].attachmentsNames,
                    circulars[position].attachmentsUrls
                )
            } else {
                holder.attachmentsList.adapter = null
            }
        }

        holder.viewButton.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW)
            viewIntent.setDataAndType(Uri.parse(circulars[position].url), "application/pdf")
            if (viewIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(viewIntent)
            } else {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle(R.string.dialog_install_pdf_reader_title)
                    setMessage(R.string.dialog_install_pdf_reader_content)
                    setPositiveButton(
                        R.string.dialog_ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                }

                builder.create().show()
            }
        }

        holder.downloadButton.setOnClickListener {
            adapterCallback.circularToDownload = circulars[position]

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                val builder = AlertDialog.Builder(context)

                builder.apply {
                    setMessage(context.getString(R.string.dialog_message_permission_write))
                    setTitle(context.getString(R.string.dialog_title_permission_required))
                    setPositiveButton(
                        context.getString(R.string.dialog_next)
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            adapterCallback as AppCompatActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }

                }

                builder.create().show()
            } else {
                adapterCallback.downloadCircular()
            }
        }

        holder.favouriteButton.setOnClickListener {
            object : Thread() {
                override fun run() {
                    AppDatabase.getInstance(context).circularDao()
                        .update(circulars[position].apply { favourite = !favourite })
                }
            }.start()
        }

        holder.reminderButton.setOnClickListener {
            if (circulars[position].reminder) {
                object : Thread() {
                    override fun run() {
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            circulars[position].id.toInt(),
                            Intent(context, AlarmBroadcastReceiver::class.java),
                            0
                        )

                        pendingIntent.cancel()

                        AppDatabase.getInstance(context).circularDao()
                            .update(circulars[position].apply { reminder = false })
                    }
                }.start()
            } else {
                NewReminderFragment.create(circulars[position])
                    .show((context as FragmentActivity).supportFragmentManager, "NewReminderDialog")
            }
        }

        holder.collapseButton.setOnClickListener {
            collapsedItems = if (collapsedItems == position) {
                -1
            } else {
                if (collapsedItems > -1) notifyItemChanged(collapsedItems)
                position
            }

            notifyItemChanged(position)
        }

        holder.card.setOnClickListener {
            collapsedItems = if (collapsedItems == position) {
                -1
            } else {
                if (collapsedItems > -1) notifyItemChanged(collapsedItems)
                position
            }

            notifyItemChanged(position)
        }
    }

    fun changeDataSet(newCirculars: List<Circular>) {
        if (circulars.size != newCirculars.size)
            collapsedItems = -1

        circulars = newCirculars
        notifyDataSetChanged()
    }

    override fun getItemCount() = circulars.size

    override fun getItemId(position: Int) = circulars[position].id

    interface AdapterCallback {
        var circularToDownload: Circular?
        fun downloadCircular()
    }
}