package com.calculator.vault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords")
    fun getAllPasswords(): Flow<List<PasswordEntry>>
    
    @Insert
    suspend fun insert(entry: PasswordEntry)
    
    @Delete
    suspend fun delete(entry: PasswordEntry)
}
