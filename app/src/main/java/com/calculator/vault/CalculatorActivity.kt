package com.calculator.vault

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.calculator.vault.security.PinManager

class CalculatorActivity : AppCompatActivity() {
    
    private lateinit var displayText: TextView
    private lateinit var pinManager: PinManager
    private var currentInput = ""
    private var lastResult = 0.0
    private var pendingOp = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        
        pinManager = PinManager(this)
        displayText = findViewById(R.id.displayText)
        
        // Check if PIN is set
        if (!pinManager.isPinSet()) {
            startActivity(Intent(this, PinSetupActivity::class.java))
            finish()
            return
        }
        
        setupCalculatorButtons()
    }
    
    private fun setupCalculatorButtons() {
        // Number buttons
        val numberButtons = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8", R.id.btn9 to "9"
        )
        
        // FIXED: Added explicit types (id: Int, value: String)
        numberButtons.forEach { (id: Int, value: String) ->
            findViewById<Button>(id).setOnClickListener {
                currentInput += value
                updateDisplay()
            }
        }
        
        // Operator buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { setOp("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { setOp("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOp("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOp("÷") }
        findViewById<Button>(R.id.btnDot).setOnClickListener { 
            if (!currentInput.contains(".")) {
                currentInput += "."
                updateDisplay()
            }
        }
        
        // Clear button
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            currentInput = ""
            pendingOp = ""
            lastResult = 0.0
            updateDisplay()
        }
        
        // Equals button - triggers vault unlock OR calculate
        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            checkPinAndUnlock()
        }
        
        // Scientific functions (for show)
        // FIXED: Added explicit type (id: Int)
        listOf(R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnLog, 
               R.id.btnLn, R.id.btnSqrt, R.id.btnPi, R.id.btnE,
               R.id.btnPercent, R.id.btnFactorial, R.id.btnPower,
               R.id.btnLParen, R.id.btnRParen).forEach { id: Int ->
            findViewById<Button>(id).setOnClickListener {
                Toast.makeText(this, "Scientific mode not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setOp(op: String) {
        if (currentInput.isNotEmpty()) {
            if (pendingOp.isNotEmpty()) {
                calculate()
            } else {
                lastResult = currentInput.toDoubleOrNull() ?: 0.0
            }
            pendingOp = op
            currentInput = ""
        }
    }
    
    private fun calculate() {
        val current = currentInput.toDoubleOrNull() ?: return
        lastResult = when (pendingOp) {
            "+" -> lastResult + current
            "-" -> lastResult - current
            "×" -> lastResult * current
            "÷" -> if (current != 0.0) lastResult / current else 0.0
            else -> current
        }
        currentInput = ""
        pendingOp = ""
    }
    
    private fun updateDisplay() {
        val display = if (currentInput.isEmpty()) {
            if (lastResult != 0.0) formatNumber(lastResult) else "0"
        } else {
            currentInput
        }
        displayText.text = display
    }
    
    private fun formatNumber(num: Double): String {
        return if (num == num.toLong().toDouble()) {
            num.toLong().toString()
        } else {
            num.toString()
        }
    }
    
    private fun checkPinAndUnlock() {
        // First try as PIN
        if (pinManager.validatePin(currentInput)) {
            val intent = Intent(this, LauncherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        // Otherwise calculate if valid expression
        if (pendingOp.isNotEmpty() && currentInput.isNotEmpty()) {
            calculate()
            updateDisplay()
        } else if (currentInput.isNotEmpty()) {
            // Single number - just show it
            lastResult = currentInput.toDoubleOrNull() ?: 0.0
            currentInput = ""
            updateDisplay()
        } else {
            Toast.makeText(this, "Enter PIN or expression", Toast.LENGTH_SHORT).show()
        }
    }
}
