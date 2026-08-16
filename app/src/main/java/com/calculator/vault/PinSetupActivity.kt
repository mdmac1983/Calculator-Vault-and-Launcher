package com.calculator.vault

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.calculator.vault.security.PinManager

class PinSetupActivity : AppCompatActivity() {
    
    private lateinit var pinManager: PinManager
    private lateinit var instructionText: TextView
    private var firstPin = ""
    private var currentPin = ""
    private var isConfirming = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_setup)
        
        pinManager = PinManager(this)
        instructionText = findViewById(R.id.instructionText)
        
        setupNumpad()
    }
    
    private fun setupNumpad() {
        val buttons = listOf(
            R.id.numpad0 to "0", R.id.numpad1 to "1", R.id.numpad2 to "2",
            R.id.numpad3 to "3", R.id.numpad4 to "4", R.id.numpad5 to "5",
            R.id.numpad6 to "6", R.id.numpad7 to "7", R.id.numpad8 to "8", R.id.numpad9 to "9"
        )
        
        buttons.forEach { (id: Int, digit: String) ->
            findViewById<Button>(id).setOnClickListener {
                if (currentPin.length < 8) {
                    currentPin += digit
                    updatePinDisplay()
                }
            }
        }
        
        findViewById<Button>(R.id.numpadClear).setOnClickListener {
            currentPin = ""
            updatePinDisplay()
        }
        
        findViewById<Button>(R.id.numpadBack).setOnClickListener {
            if (currentPin.isNotEmpty()) {
                currentPin = currentPin.dropLast(1)
                updatePinDisplay()
            }
        }
        
        findViewById<Button>(R.id.numpadConfirm).setOnClickListener {
            confirmPin()
        }
    }
    
    private fun updatePinDisplay() {
        val dots = "●".repeat(currentPin.length) + "○".repeat(8 - currentPin.length)
        findViewById<TextView>(R.id.pinDots).text = dots
    }
    
    private fun confirmPin() {
        if (currentPin.length < 4) {
            Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!isConfirming) {
            firstPin = currentPin
            currentPin = ""
            isConfirming = true
            instructionText.text = "Confirm your PIN"
            updatePinDisplay()
        } else {
            if (currentPin == firstPin) {
                pinManager.setPin(currentPin)
                Toast.makeText(this, "PIN set successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CalculatorActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "PINs don't match. Try again.", Toast.LENGTH_SHORT).show()
                currentPin = ""
                firstPin = ""
                isConfirming = false
                instructionText.text = "Create a PIN (4-8 digits)"
                updatePinDisplay()
            }
        }
    }
}
