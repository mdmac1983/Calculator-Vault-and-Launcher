package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R

class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onClick: (SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    data class SettingItem(val icon: Int, val title: String)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.settingIcon)
        val title: TextView = view.findViewById(R.id.settingTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_settings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.icon)
        holder.title.text = item.title
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
