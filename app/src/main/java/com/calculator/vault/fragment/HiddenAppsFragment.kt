package com.calculator.vault.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.data.HiddenApp
import com.calculator.vault.data.VaultDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HiddenAppsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HiddenAppsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hidden_apps, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.hiddenAppsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        view.findViewById<FloatingActionButton>(R.id.fabAddApp).setOnClickListener {
            showAppPicker()
        }
        
        loadHiddenApps()
    }
    
    private fun loadHiddenApps() {
        lifecycleScope.launch {
            val apps = VaultDatabase.getInstance(requireContext())
                .hiddenAppDao()
                .getAllHiddenApps()
            
            adapter = HiddenAppsAdapter(apps) { app ->
                unhideApp(app)
            }
            recyclerView.adapter = adapter
        }
    }
    
    private fun showAppPicker() {
        val pm = requireContext().packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .sortedBy { it.loadLabel(pm).toString() }
        
        val appNames = apps.map { it.loadLabel(pm).toString() }.toTypedArray()
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Select App to Hide")
            .setItems(appNames) { _, which ->
                val selected = apps[which]
                hideApp(selected)
            }
            .show()
    }
    
    private fun hideApp(appInfo: ApplicationInfo) {
        val hiddenApp = HiddenApp(
            packageName = appInfo.packageName,
            appName = appInfo.loadLabel(requireContext().packageManager).toString()
        )
        
        lifecycleScope.launch {
            VaultDatabase.getInstance(requireContext()).hiddenAppDao().insert(hiddenApp)
            Toast.makeText(context, "App hidden", Toast.LENGTH_SHORT).show()
            loadHiddenApps()
        }
    }
    
    private fun unhideApp(app: HiddenApp) {
        lifecycleScope.launch {
            VaultDatabase.getInstance(requireContext()).hiddenAppDao().delete(app)
            Toast.makeText(context, "App unhidden", Toast.LENGTH_SHORT).show()
            loadHiddenApps()
        }
    }
}
