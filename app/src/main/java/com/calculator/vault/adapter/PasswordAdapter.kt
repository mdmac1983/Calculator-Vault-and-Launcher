package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.model.PasswordEntry

class PasswordAdapter(
    private var passwords: List<PasswordEntry>,
    private val onClick: (PasswordEntry) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    class PasswordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.passwordIcon)
        val site: TextView = view.findViewById(R.id.passwordSite)
        val user: TextView = view.findViewById(R.id.passwordUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val entry = passwords[position]
        holder.site.text = entry.title
        holder.user.text = entry.username
        holder.icon.setImageResource(getCategoryIcon(entry.category))
        holder.itemView.setOnClickListener { onClick(entry) }
    }

    override fun getItemCount() = passwords.size

    fun updatePasswords(newPasswords: List<PasswordEntry>) {
        passwords = newPasswords
        notifyDataSetChanged()
    }

    private fun getCategoryIcon(category: String): Int {
        return when (category) {
            "WEBSITE" -> R.drawable.ic_web
            "BANKING" -> R.drawable.ic_bank
            "CREDIT_CARD" -> R.drawable.ic_card
            "CONTACT" -> R.drawable.ic_contact
            else -> R.drawable.ic_lock
        }
    }
}
