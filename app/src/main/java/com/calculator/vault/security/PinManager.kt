package com.calculator.vault.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PinManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "vault_pin_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
    }
    
    fun isPinSet(): Boolean {
        return prefs.contains(KEY_PIN_HASH)
    }
    
    fun setPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt)
            .apply()
    }
    
    fun validatePin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val salt = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val inputHash = hashPin(pin, salt)
        return storedHash == inputHash
    }
    
    fun changePin(oldPin: String, newPin: String): Boolean {
        if (!validatePin(oldPin)) {
            return false
        }
        setPin(newPin)
        return true
    }
    
    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        java.security.SecureRandom().nextBytes(bytes)
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }
    
    private fun hashPin(pin: String, salt: String): String {
        val combined = pin + salt
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(combined.toByteArray())
        return android.util.Base64.encodeToString(hash, android.util.Base64.DEFAULT)
    }
}
