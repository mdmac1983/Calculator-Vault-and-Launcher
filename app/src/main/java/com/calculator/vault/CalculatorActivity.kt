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
    private var isEnteringPin = false
    
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
        
        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                currentInput += value
                updateDisplay()
            }
        }
        
        // Operator buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { appendOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { appendOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { appendOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { appendOperator("÷") }
        findViewById<Button>(R.id.btnDot).setOnClickListener { appendOperator(".") }
        
        // Clear button
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            currentInput = ""
            isEnteringPin = false
            updateDisplay()
        }
        
        // Equals button - triggers vault unlock
        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            checkPinAndUnlock()
        }
        
        // Scientific functions (for show)
        listOf(R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnLog, 
               R.id.btnLn, R.id.btnSqrt, R.id.btnPi, R.id.btnE,
               R.id.btnPercent, R.id.btnFactorial, R.id.btnPower,
               R.id.btnLParen, R.id.btnRParen).forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                Toast.makeText(this, "Function not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun appendOperator(op: String) {
        currentInput += op
        updateDisplay()
    }
    
    private fun updateDisplay() {
        displayText.text = if (currentInput.isEmpty()) "0" else currentInput
    }
    
    private fun checkPinAndUnlock() {
        if (pinManager.validatePin(currentInput)) {
            // PIN correct - launch vault
            val intent = Intent(this, LauncherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // PIN incorrect - show error and calculate if valid expression
            try {
                val result = evaluateExpression(currentInput)
                currentInput = result.toString()
                updateDisplay()
            } catch (e: Exception) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                currentInput = ""
                updateDisplay()
            }
        }
    }
    
    private fun evaluateExpression(expression: String): Double {
        // Simple expression evaluator for calculator functionality
        return try {
            val cleanExpr = expression.replace("×", "*").replace("÷", "/")
            val engine = javax.script.ScriptEngineManager().getEngineByName("JavaScript")
            engine.eval(cleanExpr).toString().toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
}
