package com.calculator.vault.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.calculator.vault.model.PasswordEntry
import com.calculator.vault.model.CalendarEvent
import com.calculator.vault.model.HiddenApp
import com.calculator.vault.model.VaultFile

@Database(
    entities = [
        PasswordEntry::class,
        CalendarEvent::class,
        HiddenApp::class,
        VaultFile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VaultDatabase : RoomDatabase() {
    
    abstract fun passwordDao(): PasswordDao
    abstract fun eventDao(): EventDao
    abstract fun hiddenAppDao(): HiddenAppDao
    abstract fun vaultFileDao(): VaultFileDao
    
    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null
        
        fun getInstance(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): VaultDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                VaultDatabase::class.java,
                "vault_database"
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
