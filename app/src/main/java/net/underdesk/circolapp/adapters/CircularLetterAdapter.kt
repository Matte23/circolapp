/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
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
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.underdesk.circolapp.AlarmBroadcastReceiver
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AndroidCircularRepository
import net.underdesk.circolapp.data.AndroidDatabase
import net.underdesk.circolapp.databinding.ItemCircularBinding
import net.underdesk.circolapp.fragments.NewReminderFragment
import net.underdesk.circolapp.server.AndroidServerApi
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.data.CircularRepository
import net.underdesk.circolapp.utils.DownloadableFile
import net.underdesk.circolapp.utils.FileUtils

class CircularLetterAdapter(
    private var circulars: List<Circular>,
    private val mainActivity: MainActivity,
    private val adapterScope: CoroutineScope
) :
    RecyclerView.Adapter<CircularLetterAdapter.CircularLetterViewHolder>() {
    private lateinit var context: Context
    private lateinit var circularRepository: CircularRepository
    private var idsAreHumanReadable = true
    private val adapterCallback: AdapterCallback = mainActivity
    private var collapsedItems = -1

    init {
        setHasStableIds(true)
    }

    inner class CircularLetterViewHolder(binding: ItemCircularBinding) : RecyclerView.ViewHolder(binding.root) {
        var card: CardView = binding.circularCard
        var progressBar: ProgressBar = binding.circularProgressBar
        var title: TextView = binding.circularTitleTextview
        var number: TextView = binding.circularNumberTextview
        var date: TextView = binding.circularDateTextview
        var collapseButton: ImageButton = binding.circularCollapseButton
        var viewButton: ImageButton = binding.circularViewButton
        var shareButton: ImageButton = binding.circularShareButton
        var downloadButton: ImageButton = binding.circularDownloadButton
        var favouriteButton: ImageButton = binding.circularFavouriteButton
        var reminderButton: ImageButton = binding.circularReminderButton
        var attachmentsList: RecyclerView = binding.circularsAttachmentsList

        val loading = MutableLiveData(false)
        var observer: MutableList<Observer<in Boolean>> = mutableListOf()

        init {
            attachmentsList.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularLetterViewHolder {
        val binding = ItemCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        circularRepository = AndroidCircularRepository.getInstance(context)
        idsAreHumanReadable = AndroidServerApi.getInstance(context).idsAreHumanReadable()

        return CircularLetterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CircularLetterViewHolder, position: Int) {
        if (idsAreHumanReadable) {
            holder.number.text =
                context.getString(R.string.notification_title_id, circulars[position].id)
            holder.date.text = circulars[position].date
        } else {
            holder.number.text =
                context.getString(R.string.notification_title_date, circulars[position].date)
            holder.date.text = ""
        }
        holder.title.text = circulars[position].name

        val observer = Observer<Boolean> {
            if (it) {
                holder.progressBar.visibility = View.VISIBLE
                holder.viewButton.isEnabled = false
                holder.downloadButton.isEnabled = false
                holder.shareButton.isEnabled = false
            } else {
                holder.progressBar.visibility = View.GONE
                holder.viewButton.isEnabled = true
                holder.downloadButton.isEnabled = true
                holder.shareButton.isEnabled = true
            }
        }

        holder.loading.observe(mainActivity, observer)
        holder.observer.add(observer)

        if (circulars[position].read) {
            holder.number.typeface = Typeface.DEFAULT
            holder.date.typeface = Typeface.DEFAULT
        } else {
            holder.number.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            holder.date.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }

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
            holder.shareButton.visibility = View.GONE
            holder.downloadButton.visibility = View.GONE
            holder.favouriteButton.visibility = View.GONE
            holder.reminderButton.visibility = View.GONE

            holder.attachmentsList.visibility = View.GONE
            holder.attachmentsList.adapter = null
        } else {
            FileUtils.preloadFiles(
                circulars[position].realUrl,
                circulars[position].attachmentsUrls,
                mainActivity.customTabsSession
            )

            holder.collapseButton.setImageDrawable(
                getDrawable(
                    context,
                    R.drawable.baseline_expand_less_24
                )
            )
            holder.collapseButton.contentDescription = context.getString(R.string.image_collapse)

            holder.viewButton.visibility = View.VISIBLE
            holder.shareButton.visibility = View.VISIBLE
            holder.downloadButton.visibility = View.VISIBLE
            holder.favouriteButton.visibility = View.VISIBLE
            holder.reminderButton.visibility = View.VISIBLE

            if (circulars[position].attachmentsNames.isNotEmpty()) {
                holder.attachmentsList.visibility = View.VISIBLE
                holder.attachmentsList.adapter = AttachmentAdapter(
                    circulars[position],
                    mainActivity,
                    adapterScope,
                    holder
                )
            } else {
                holder.attachmentsList.adapter = null
            }
        }

        holder.viewButton.setOnClickListener {
            if (!circulars[position].read) {
                adapterScope.launch {
                    AndroidDatabase.getDaoInstance(context).markRead(
                        circulars[position].id,
                        circulars[position].school,
                        true
                    )
                }
            }

            runWhenUrlIsAvailable(holder, circulars[position]) { url ->
                FileUtils.viewFile(
                    url,
                    context,
                    mainActivity.customTabsSession
                )
            }
        }

        holder.shareButton.setOnClickListener {
            runWhenUrlIsAvailable(holder, circulars[position]) { url ->
                FileUtils.shareFile(
                    url,
                    context
                )
            }
        }

        holder.downloadButton.setOnClickListener {
            runWhenUrlIsAvailable(holder, circulars[position]) { url ->
                val file = DownloadableFile(circulars[position].name, url)
                FileUtils.downloadFile(file, adapterCallback, context)
            }
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

    override fun onViewRecycled(holder: CircularLetterViewHolder) {
        holder.observer.forEach { holder.loading.removeObserver(it) }
        holder.observer.clear()
        super.onViewRecycled(holder)
    }

    private fun runWhenUrlIsAvailable(
        holder: CircularLetterViewHolder,
        circular: Circular,
        code: (url: String) -> Unit
    ) {
        if (circular.realUrl == null) {
            holder.loading.postValue(true)

            adapterScope.launch {
                val realUrl =
                    circularRepository.getRealUrl(circular.url, circular.id, circular.school)
                holder.loading.postValue(false)
                code(realUrl)
            }
            return
        }

        code(circular.realUrl!!)
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
