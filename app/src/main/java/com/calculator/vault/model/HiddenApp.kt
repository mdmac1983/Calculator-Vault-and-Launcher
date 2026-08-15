package com.calculator.vault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_apps")
data class HiddenApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val folderId: Long? = null, // For grouping into folders
    val sortOrder: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
