package com.calculator.vault.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SIZE = 12
    }
    
    fun encryptFile(inputFile: File, outputFile: File) {
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val keySpec = SecretKeySpec(masterKey.toString().toByteArray().copyOf(32), ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        
        val inputData = inputFile.readBytes()
        val encryptedData = cipher.doFinal(inputData)
        
        // Write IV + encrypted data
        outputFile.outputStream().use { output ->
            output.write(iv)
            output.write(encryptedData)
        }
    }
    
    fun decryptFile(encryptedFile: File, outputFile: File) {
        val fileData = encryptedFile.readBytes()
        val iv = fileData.copyOfRange(0, IV_SIZE)
        val encryptedData = fileData.copyOfRange(IV_SIZE, fileData.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val keySpec = SecretKeySpec(masterKey.toString().toByteArray().copyOf(32), ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        
        val decryptedData = cipher.doFinal(encryptedData)
        outputFile.writeBytes(decryptedData)
    }
    
    fun encryptData(data: ByteArray): ByteArray {
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val keySpec = SecretKeySpec(masterKey.toString().toByteArray().copyOf(32), ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        
        val encrypted = cipher.doFinal(data)
        return iv + encrypted
    }
    
    fun decryptData(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, IV_SIZE)
        val data = encryptedData.copyOfRange(IV_SIZE, encryptedData.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val keySpec = SecretKeySpec(masterKey.toString().toByteArray().copyOf(32), ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        
        return cipher.doFinal(data)
    }
}
