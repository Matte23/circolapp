package net.underdesk.circolapp.adapters

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

    inner class CircularLetterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.circular_title_textview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularLetterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circular, parent, false)

        return CircularLetterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircularLetterViewHolder, position: Int) {
        holder.title.text = circulars[position].name
    }

    override fun getItemCount() = circulars.size
}