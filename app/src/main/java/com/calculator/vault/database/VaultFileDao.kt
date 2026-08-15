package com.calculator.vault.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.calculator.vault.model.VaultFile

@Dao
interface VaultFileDao {
    
    @Query("SELECT * FROM vault_files ORDER BY importedAt DESC")
    fun getAllFiles(): LiveData<List<VaultFile>>
    
    @Query("SELECT * FROM vault_files WHERE folderId = :folderId")
    fun getFilesInFolder(folderId: Long?): LiveData<List<VaultFile>>
    
    @Query("SELECT * FROM vault_files WHERE fileType = :type")
    fun getFilesByType(type: String): LiveData<List<VaultFile>>
    
    @Insert
    suspend fun insert(file: VaultFile): Long
    
    @Delete
    suspend fun delete(file: VaultFile)
    
    @Update
    suspend fun update(file: VaultFile)
    
    @Query("SELECT * FROM vault_files WHERE id = :id")
    suspend fun getFileById(id: Long): VaultFile?
}
