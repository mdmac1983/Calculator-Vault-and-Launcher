package com.calculator.vault.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.calculator.vault.model.PasswordEntry

@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM passwords ORDER BY category, title")
    fun getAllPasswords(): LiveData<List<PasswordEntry>>
    
    @Query("SELECT * FROM passwords WHERE category = :category ORDER BY title")
    fun getPasswordsByCategory(category: String): LiveData<List<PasswordEntry>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordEntry?
    
    @Insert
    suspend fun insert(password: PasswordEntry): Long
    
    @Update
    suspend fun update(password: PasswordEntry)
    
    @Delete
    suspend fun delete(password: PasswordEntry)
    
    @Query("SELECT * FROM passwords WHERE title LIKE '%' || :query || '%'")
    fun searchPasswords(query: String): LiveData<List<PasswordEntry>>
}
