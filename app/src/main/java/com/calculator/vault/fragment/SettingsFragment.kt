package com.calculator.vault.fragment

import com.calculator.vault.data.*
import com.calculator.vault.adapter.*
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.adapter.SettingsAdapter
import com.calculator.vault.security.PinManager

class SettingsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var pinManager: PinManager
    
    private val settings = listOf(
        SettingItem("UI Theme", "Dark", R.drawable.ic_theme),
        SettingItem("Change PIN", "", R.drawable.ic_lock),
        SettingItem("Lock Home Screen", "On", R.drawable.ic_home),
        SettingItem("Icon Pack", "Default", R.drawable.ic_palette),
        SettingItem("Vault Wallpaper", "", R.drawable.ic_image),
        SettingItem("App Name", "Calculator", R.drawable.ic_app),
        SettingItem("Set as Default Launcher", "", R.drawable.ic_launcher_settings),
        SettingItem("Backup & Restore", "", R.drawable.ic_backup)
    )
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        pinManager = PinManager(requireContext())
        
        recyclerView = view.findViewById(R.id.settingsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        val adapter = SettingsAdapter(settings) { setting ->
            when (setting.title) {
                "Change PIN" -> showChangePinDialog()
                "Set as Default Launcher" -> setDefaultLauncher()
                else -> Toast.makeText(context, "${setting.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter
    }
    
    private fun showChangePinDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_change_pin, null)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Change PIN")
            .setView(view)
            .setPositiveButton("Change") { _, _ ->
                val oldPin = view.findViewById<EditText>(R.id.oldPinInput).text.toString()
                val newPin = view.findViewById<EditText>(R.id.newPinInput).text.toString()
                
                if (pinManager.changePin(oldPin, newPin)) {
                    Toast.makeText(context, "PIN changed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Incorrect old PIN", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun setDefaultLauncher() {
        val intent = Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
        startActivity(intent)
    }
    
    data class SettingItem(val title: String, val value: String, val icon: Int)
}
