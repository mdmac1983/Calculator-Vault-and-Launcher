package com.calculator.vault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "WEBSITE", "BANKING", "CREDIT_CARD", "CONTACT"
    val title: String,
    val username: String,
    val password: String, // Encrypted
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
