package com.calculator.vault.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.calculator.vault.model.CalendarEvent

@Dao
interface EventDao {
    
    @Query("SELECT * FROM events WHERE startTime BETWEEN :start AND :end ORDER BY startTime")
    fun getEventsBetween(start: Long, end: Long): LiveData<List<CalendarEvent>>
    
    @Query("SELECT * FROM events ORDER BY startTime")
    fun getAllEvents(): LiveData<List<CalendarEvent>>
    
    @Insert
    suspend fun insert(event: CalendarEvent): Long
    
    @Update
    suspend fun update(event: CalendarEvent)
    
    @Delete
    suspend fun delete(event: CalendarEvent)
}
