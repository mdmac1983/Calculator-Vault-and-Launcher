package com.calculator.vault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PasswordEntry::class, HiddenApp::class, CalendarEvent::class],
    version = 1
)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
    abstract fun hiddenAppDao(): HiddenAppDao
    abstract fun eventDao(): EventDao
    
    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null
        
        fun getInstance(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vault_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
