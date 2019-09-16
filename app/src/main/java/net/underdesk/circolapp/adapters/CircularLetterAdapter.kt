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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_circular.view.*
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.Circular


class CircularLetterAdapter(private val circulars: List<Circular>) :
    RecyclerView.Adapter<CircularLetterAdapter.CircularLetterViewHolder>() {
    private lateinit var context: Context

    inner class CircularLetterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.circular_title_textview
        var number: TextView = view.circular_number_textview
        var date: TextView = view.circular_date_textview
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
    }

    override fun getItemCount() = circulars.size
}