package com.calculator.vault.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean = false,
    val reminderMinutes: Int = 15, // Minutes before event
    val createdAt: Long = System.currentTimeMillis()
)
