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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AndroidDatabase
import net.underdesk.circolapp.databinding.ItemCircularBinding
import net.underdesk.circolapp.fragments.NewReminderFragment
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.utils.DownloadableFile
import net.underdesk.circolapp.utils.FileUtils

class CircularLetterAdapter(
    private var circulars: List<Circular>,
    private val adapterCallback: AdapterCallback,
    private val adapterScope: CoroutineScope
) :
    RecyclerView.Adapter<CircularLetterAdapter.CircularLetterViewHolder>() {
    private lateinit var context: Context
    private var collapsedItems = -1

    init {
        setHasStableIds(true)
    }

    inner class CircularLetterViewHolder(binding: ItemCircularBinding) : RecyclerView.ViewHolder(binding.root) {
        var card: CardView = binding.circularCard
        var title: TextView = binding.circularTitleTextview
        var number: TextView = binding.circularNumberTextview
        var date: TextView = binding.circularDateTextview
        var collapseButton: ImageButton = binding.circularCollapseButton
        var viewButton: ImageButton = binding.circularViewButton
        var downloadButton: ImageButton = binding.circularDownloadButton
        var favouriteButton: ImageButton = binding.circularFavouriteButton
        var reminderButton: ImageButton = binding.circularReminderButton
        var attachmentsList: RecyclerView = binding.circularsAttachmentsList

        init {
            attachmentsList.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularLetterViewHolder {
        val binding = ItemCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context

        return CircularLetterViewHolder(binding)
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
            holder.collapseButton.setImageDrawable(
                getDrawable(
                    context,
                    R.drawable.baseline_expand_more_24
                )
            )
            holder.collapseButton.contentDescription = context.getString(R.string.image_expand)

            holder.viewButton.visibility = View.GONE
            holder.downloadButton.visibility = View.GONE
            holder.favouriteButton.visibility = View.GONE
            holder.reminderButton.visibility = View.GONE

            holder.attachmentsList.visibility = View.GONE
            holder.attachmentsList.adapter = null
        } else {
            holder.collapseButton.setImageDrawable(
                getDrawable(
                    context,
                    R.drawable.baseline_expand_less_24
                )
            )
            holder.collapseButton.contentDescription = context.getString(R.string.image_collapse)

            holder.viewButton.visibility = View.VISIBLE
            holder.downloadButton.visibility = View.VISIBLE
            holder.favouriteButton.visibility = View.VISIBLE
            holder.reminderButton.visibility = View.VISIBLE

            if (circulars[position].attachmentsNames.isNotEmpty()) {
                holder.attachmentsList.visibility = View.VISIBLE
                holder.attachmentsList.adapter = AttachmentAdapter(
                    circulars[position].attachmentsNames,
                    circulars[position].attachmentsUrls,
                    adapterCallback
                )
            } else {
                holder.attachmentsList.adapter = null
            }
        }

        holder.viewButton.setOnClickListener {
            FileUtils.viewFile(circulars[position].url, context)
        }

        holder.downloadButton.setOnClickListener {
            val file = DownloadableFile(circulars[position].name, circulars[position].url)
            FileUtils.downloadFile(file, adapterCallback, context)
        }

        holder.favouriteButton.setOnClickListener {
            adapterScope.launch {
                AndroidDatabase.getDaoInstance(context).update(
                    circulars[position].id,
                    circulars[position].school,
                    !circulars[position].favourite,
                    circulars[position].reminder
                )
            }
        }

        holder.reminderButton.setOnClickListener {
            if (circulars[position].reminder) {

                adapterScope.launch {
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        circulars[position].id.toInt(),
                        Intent(context, AlarmBroadcastReceiver::class.java),
                        0
                    )

                    pendingIntent.cancel()

                    AndroidDatabase.getDaoInstance(context)
                        .update(
                            circulars[position].id,
                            circulars[position].school,
                            circulars[position].favourite,
                            false
                        )
                }
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
        var fileToDownload: DownloadableFile?
        fun downloadFile()
    }
}
