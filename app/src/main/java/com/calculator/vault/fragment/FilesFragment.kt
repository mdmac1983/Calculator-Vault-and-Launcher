package com.calculator.vault.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.adapter.FileAdapter
import com.calculator.vault.database.VaultDatabase
import com.calculator.vault.model.VaultFile
import com.calculator.vault.security.EncryptionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FilesFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter
    private lateinit var encryptionManager: EncryptionManager
    
    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { importFile(it) }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_files, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        encryptionManager = EncryptionManager(requireContext())
        
        recyclerView = view.findViewById(R.id.filesRecycler)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        
        adapter = FileAdapter(emptyList()) { file ->
            // Handle file click
            Toast.makeText(context, file.name, Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
        
        view.findViewById<FloatingActionButton>(R.id.addFileFab).setOnClickListener {
            pickFile.launch("*/*")
        }
        
        loadFiles()
    }
    
    private fun loadFiles() {
        val db = VaultDatabase.getInstance(requireContext())
        db.vaultFileDao().getAllFiles().observe(viewLifecycleOwner) { files ->
            adapter.updateFiles(files)
        }
    }
    
    private fun importFile(uri: Uri) {
        lifecycleScope.launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val fileName = getFileName(uri)
                val fileSize = inputStream?.available()?.toLong() ?: 0
                
                // Create vault directory
                val vaultDir = File(requireContext().filesDir, "vault")
                vaultDir.mkdirs()
                
                val encryptedFile = File(vaultDir, UUID.randomUUID().toString())
                
                inputStream?.use { input ->
                    val data = input.readBytes()
                    val encrypted = encryptionManager.encryptData(data)
                    encryptedFile.writeBytes(encrypted)
                }
                
                // Save to database
                val vaultFile = VaultFile(
                    name = fileName,
                    originalPath = uri.toString(),
                    encryptedPath = encryptedFile.absolutePath,
                    fileType = getFileType(fileName),
                    size = fileSize
                )
                
                VaultDatabase.getInstance(requireContext()).vaultFileDao().insert(vaultFile)
                Toast.makeText(context, "File imported", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getFileName(uri: Uri): String {
        var result = "unknown"
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) result = it.getString(index)
                }
            }
        }
        return result
    }
    
    private fun getFileType(fileName: String): String {
        return when (fileName.substringAfterLast(".", "").lowercase()) {
            "jpg", "jpeg", "png", "gif", "webp" -> "IMAGE"
            "mp4", "avi", "mkv", "mov" -> "VIDEO"
            "mp3", "wav", "flac", "aac" -> "AUDIO"
            "pdf", "doc", "docx", "txt" -> "DOCUMENT"
            else -> "OTHER"
        }
    }
}
