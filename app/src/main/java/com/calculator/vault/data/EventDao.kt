package com.calculator.vault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE startTime BETWEEN :start AND :end")
    fun getEventsForRange(start: Long, end: Long): Flow<List<CalendarEvent>>
    
    @Insert
    suspend fun insert(event: CalendarEvent)
    
    @Delete
    suspend fun delete(event: CalendarEvent)
}
