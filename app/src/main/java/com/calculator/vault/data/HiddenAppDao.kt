package com.calculator.vault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppDao {
    @Query("SELECT * FROM hidden_apps")
    fun getAllHiddenApps(): Flow<List<HiddenApp>>
    
    @Insert
    suspend fun insert(app: HiddenApp)
    
    @Delete
    suspend fun delete(app: HiddenApp)
}
