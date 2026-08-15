package com.calculator.vault.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.adapter.HiddenAppAdapter
import com.calculator.vault.database.VaultDatabase
import com.calculator.vault.model.HiddenApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HiddenAppsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HiddenAppAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hidden_apps, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.hiddenAppsRecycler)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        
        adapter = HiddenAppAdapter(emptyList(), { app ->
            launchApp(app.packageName)
        }, { app ->
            removeFromHidden(app)
        })
        recyclerView.adapter = adapter
        
        view.findViewById<FloatingActionButton>(R.id.addHiddenFab).setOnClickListener {
            showAppPicker()
        }
        
        loadHiddenApps()
    }
    
    private fun loadHiddenApps() {
        VaultDatabase.getInstance(requireContext()).hiddenAppDao()
            .getAllHiddenApps()
            .observe(viewLifecycleOwner) { apps ->
                adapter.updateApps(apps)
            }
    }
    
    private fun launchApp(packageName: String) {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(context, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun removeFromHidden(app: HiddenApp) {
        lifecycleScope.launch {
            VaultDatabase.getInstance(requireContext()).hiddenAppDao().delete(app)
            Toast.makeText(context, "Removed from hidden", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showAppPicker() {
        val pm = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = pm.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != requireContext().packageName }
            .sortedBy { it.loadLabel(pm).toString() }
        
        val appNames = apps.map { it.loadLabel(pm).toString() }.toTypedArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add to Hidden Apps")
            .setItems(appNames) { _, which ->
                val app = apps[which]
                val hiddenApp = HiddenApp(
                    packageName = app.activityInfo.packageName,
                    appName = app.loadLabel(pm).toString()
                )
                lifecycleScope.launch {
                    VaultDatabase.getInstance(requireContext()).hiddenAppDao().insert(hiddenApp)
                }
            }
            .show()
    }
}
