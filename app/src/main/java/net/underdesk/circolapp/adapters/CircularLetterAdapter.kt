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

package net.underdesk.circolapp.adapters

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_circular.view.*
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.fragments.NewReminderFragment


class CircularLetterAdapter(private val circulars: List<Circular>) :
    RecyclerView.Adapter<CircularLetterAdapter.CircularLetterViewHolder>() {
    private lateinit var context: Context
    private var collapsedItems = -1

    inner class CircularLetterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        if (collapsedItems != position) {
            holder.collapseButton.setImageDrawable(context.getDrawable(R.drawable.baseline_expand_more_24))

            holder.viewButton.visibility = View.GONE
            holder.downloadButton.visibility = View.GONE
            holder.favouriteButton.visibility = View.GONE
            holder.reminderButton.visibility = View.GONE

            holder.attachmentsList.visibility = View.GONE
            holder.attachmentsList.adapter = null
        } else {
            holder.collapseButton.setImageDrawable(context.getDrawable(R.drawable.baseline_expand_less_24))

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
            }
        }

        holder.viewButton.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW)
            viewIntent.setDataAndType(Uri.parse(circulars[position].url), "application/pdf")
            context.startActivity(viewIntent)
        }

        holder.downloadButton.setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(circulars[position].url))
            request.setTitle(circulars[position].name)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Circolapp/" + circulars[position].id + ".pdf"
            )

            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
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
    }

    override fun getItemCount() = circulars.size
}