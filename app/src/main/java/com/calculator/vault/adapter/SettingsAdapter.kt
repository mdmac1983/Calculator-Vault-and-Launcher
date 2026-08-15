package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.fragment.SettingsFragment

class SettingsAdapter(
    private val settings: List<SettingsFragment.SettingItem>,
    private val onClick: (SettingsFragment.SettingItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingViewHolder>() {

    class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.settingIcon)
        val title: TextView = view.findViewById(R.id.settingTitle)
        val value: TextView = view.findViewById(R.id.settingValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val setting = settings[position]
        holder.title.text = setting.title
        holder.value.text = setting.value
        holder.icon.setImageResource(setting.icon)
        holder.itemView.setOnClickListener { onClick(setting) }
    }

    override fun getItemCount() = settings.size
}
