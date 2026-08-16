package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.data.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val events: List<CalendarEvent>
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.eventTitle)
        val date: TextView = view.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        holder.date.text = sdf.format(Date(event.startTime))
    }

    override fun getItemCount() = events.size
}
