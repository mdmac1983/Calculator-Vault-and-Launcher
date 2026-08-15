package com.calculator.vault.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.calculator.vault.model.HiddenApp

@Dao
interface HiddenAppDao {
    
    @Query("SELECT * FROM hidden_apps ORDER BY sortOrder")
    fun getAllHiddenApps(): LiveData<List<HiddenApp>>
    
    @Query("SELECT * FROM hidden_apps WHERE folderId = :folderId ORDER BY sortOrder")
    fun getAppsInFolder(folderId: Long): LiveData<List<HiddenApp>>
    
    @Query("SELECT * FROM hidden_apps WHERE packageName = :packageName")
    suspend fun getAppByPackage(packageName: String): HiddenApp?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: HiddenApp)
    
    @Delete
    suspend fun delete(app: HiddenApp)
    
    @Update
    suspend fun update(app: HiddenApp)
}
