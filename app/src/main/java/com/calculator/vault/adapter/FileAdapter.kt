package com.calculator.vault.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.model.VaultFile

class FileAdapter(
    private var files: List<VaultFile>,
    private val onClick: (VaultFile) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.fileIcon)
        val name: TextView = view.findViewById(R.id.fileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.name.text = file.name
        holder.icon.setImageResource(getFileIcon(file.fileType))
        holder.itemView.setOnClickListener { onClick(file) }
    }

    override fun getItemCount() = files.size

    fun updateFiles(newFiles: List<VaultFile>) {
        files = newFiles
        notifyDataSetChanged()
    }

    private fun getFileIcon(type: String): Int {
        return when (type) {
            "IMAGE" -> R.drawable.ic_image
            "VIDEO" -> R.drawable.ic_video
            "AUDIO" -> R.drawable.ic_audio
            "DOCUMENT" -> R.drawable.ic_document
            else -> R.drawable.ic_file
        }
    }
}
