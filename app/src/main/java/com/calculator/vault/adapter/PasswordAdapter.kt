package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.data.PasswordEntry

class PasswordAdapter(
    private val passwords: List<PasswordEntry>,
    private val onClick: (PasswordEntry) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siteName: TextView = view.findViewById(R.id.siteName)
        val username: TextView = view.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = passwords[position]
        holder.siteName.text = entry.title
        holder.username.text = entry.username
        holder.itemView.setOnClickListener { onClick(entry) }
    }

    override fun getItemCount() = passwords.size
}
