package com.calculator.vault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_files")
data class VaultFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val originalPath: String,
    val encryptedPath: String,
    val fileType: String, // "IMAGE", "VIDEO", "DOCUMENT", "AUDIO", "OTHER"
    val size: Long,
    val folderId: Long? = null,
    val isFavorite: Boolean = false,
    val importedAt: Long = System.currentTimeMillis()
)
