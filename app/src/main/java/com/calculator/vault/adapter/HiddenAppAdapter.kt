package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.model.HiddenApp

class HiddenAppAdapter(
    private var apps: List<HiddenApp>,
    private val onClick: (HiddenApp) -> Unit,
    private val onLongClick: (HiddenApp) -> Unit
) : RecyclerView.Adapter<HiddenAppAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.hiddenAppIcon)
        val name: TextView = view.findViewById(R.id.hiddenAppName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hidden_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.name.text = app.appName
        // Load app icon from package
        try {
            val pm = holder.itemView.context.packageManager
            val info = pm.getApplicationInfo(app.packageName, 0)
            holder.icon.setImageDrawable(info.loadIcon(pm))
        } catch (e: Exception) {
            holder.icon.setImageResource(R.drawable.ic_app)
        }
        holder.itemView.setOnClickListener { onClick(app) }
        holder.itemView.setOnLongClickListener {
            onLongClick(app)
            true
        }
    }

    override fun getItemCount() = apps.size

    fun updateApps(newApps: List<HiddenApp>) {
        apps = newApps
        notifyDataSetChanged()
    }
}
