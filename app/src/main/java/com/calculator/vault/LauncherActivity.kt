package com.calculator.vault

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.calculator.vault.model.AppInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LauncherActivity : AppCompatActivity() {
    
    private lateinit var appGrid: GridLayout
    private lateinit var vaultFab: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        
        appGrid = findViewById(R.id.appGrid)
        vaultFab = findViewById(R.id.vaultFab)
        
        loadInstalledApps()
        setupVaultButton()
        updateTime()
    }
    
    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = pm.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                AppInfo(
                    name = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.activityInfo.loadIcon(pm)
                )
            }
            .sortedBy { it.name }
            .filter { it.packageName != packageName } // Exclude self
        
        displayApps(apps)
    }
    
    private fun displayApps(apps: List<AppInfo>) {
        appGrid.removeAllViews()
        
        apps.forEach { app ->
            val appView = layoutInflater.inflate(R.layout.item_app_icon, appGrid, false)
            
            appView.findViewById<ImageView>(R.id.appIcon).setImageDrawable(app.icon)
            appView.findViewById<TextView>(R.id.appName).text = app.name
            
            appView.setOnClickListener {
                launchApp(app.packageName)
            }
            
            appView.setOnLongClickListener {
                // TODO: Add to hidden apps
                Toast.makeText(this, "Added to hidden apps", Toast.LENGTH_SHORT).show()
                true
            }
            
            appGrid.addView(appView)
        }
    }
    
    private fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupVaultButton() {
        vaultFab.setOnClickListener {
            startActivity(Intent(this, VaultActivity::class.java))
        }
    }
    
    private fun updateTime() {
        // Update time display
        val timeText = findViewById<TextView>(R.id.timeText)
        val dateText = findViewById<TextView>(R.id.dateText)
        
        val timeFormat = java.text.SimpleDateFormat("h:mm", java.util.Locale.getDefault())
        val dateFormat = java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault())
        
        timeText.text = timeFormat.format(java.util.Date())
        dateText.text = dateFormat.format(java.util.Date())
    }
    
    override fun onResume() {
        super.onResume()
        updateTime()
    }
    
    // Prevent back button from exiting launcher
    override fun onBackPressed() {
        // Do nothing - stay in launcher
    }
}
