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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.data.AndroidCircularRepository
import net.underdesk.circolapp.databinding.ItemAttachmentBinding
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.data.CircularRepository
import net.underdesk.circolapp.utils.DownloadableFile
import net.underdesk.circolapp.utils.FileUtils

class AttachmentAdapter(
    private val circular: Circular,
    private val mainActivity: MainActivity,
    private val adapterScope: CoroutineScope,
    private val circularHolder: CircularLetterAdapter.CircularLetterViewHolder
) :
    RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {
    private val adapterCallback: CircularLetterAdapter.AdapterCallback = mainActivity
    private lateinit var circularRepository: CircularRepository
    private lateinit var context: Context

    inner class AttachmentViewHolder(binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root) {
        var title: TextView = binding.attachmentTitleTextview
        var viewButton: ImageButton = binding.attachmentViewButton
        var shareButton: ImageButton = binding.attachmentShareButton
        var downloadButton: ImageButton = binding.attachmentDownloadButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        circularRepository = AndroidCircularRepository.getInstance(context)

        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.title.text = circular.attachmentsNames[position]

        val observer = Observer<Boolean> {
            if (it) {
                holder.viewButton.isEnabled = false
                holder.downloadButton.isEnabled = false
                holder.shareButton.isEnabled = false
            } else {
                holder.viewButton.isEnabled = true
                holder.downloadButton.isEnabled = true
                holder.shareButton.isEnabled = true
            }
        }

        circularHolder.loading.observe(mainActivity, observer)
        circularHolder.observer.add(observer)

        holder.viewButton.setOnClickListener {
            runWhenUrlIsAvailable(position) { url ->
                FileUtils.viewFile(url, context, mainActivity.customTabsSession)
            }
        }

        holder.shareButton.setOnClickListener {
            runWhenUrlIsAvailable(position) { url -> FileUtils.shareFile(url, context) }
        }

        holder.downloadButton.setOnClickListener {
            runWhenUrlIsAvailable(position) { url ->
                val file = DownloadableFile(circular.attachmentsNames[position], url)
                FileUtils.downloadFile(file, adapterCallback, context)
            }
        }
    }

    private fun runWhenUrlIsAvailable(position: Int, code: (url: String) -> Unit) {
        if (circular.realAttachmentsUrls.size != circular.attachmentsUrls.size || circular.realAttachmentsUrls[position] == "") {
            circularHolder.loading.postValue(true)

            adapterScope.launch {
                val realUrls = circularRepository.getRealUrlForAttachment(
                    position,
                    circular.attachmentsUrls,
                    circular.realAttachmentsUrls,
                    circular.id,
                    circular.school
                )
                circularHolder.loading.postValue(false)

                if (circular.realAttachmentsUrls.size != circular.attachmentsUrls.size) {
                    circular.realAttachmentsUrls.clear()
                    repeat(circular.attachmentsUrls.size) { circular.realAttachmentsUrls.add("") }
                }

                circular.realAttachmentsUrls[position] = realUrls[position]
                code(realUrls[position])
            }
            return
        }

        code(circular.realAttachmentsUrls[position])
    }

    override fun getItemCount() = circular.attachmentsNames.size
}
