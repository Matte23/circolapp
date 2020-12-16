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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.underdesk.circolapp.databinding.ItemAttachmentBinding
import net.underdesk.circolapp.utils.DownloadableFile
import net.underdesk.circolapp.utils.FileUtils

class AttachmentAdapter(
    private val attachmentsNames: List<String>,
    private val attachmentsUrls: List<String>,
    private val adapterCallback: CircularLetterAdapter.AdapterCallback
) :
    RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {
    private lateinit var context: Context

    inner class AttachmentViewHolder(binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root) {
        var title: TextView = binding.attachmentTitleTextview
        var viewButton: ImageButton = binding.attachmentViewButton
        var downloadButton: ImageButton = binding.attachmentDownloadButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context

        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.title.text = attachmentsNames[position]

        holder.viewButton.setOnClickListener {
            FileUtils.viewFile(attachmentsUrls[position], context)
        }

        holder.downloadButton.setOnClickListener {
            val file = DownloadableFile(attachmentsNames[position], attachmentsUrls[position])
            FileUtils.downloadFile(file, adapterCallback, context)
        }
    }

    override fun getItemCount() = attachmentsNames.size
}
