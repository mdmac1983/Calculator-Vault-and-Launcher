package com.calculator.vault.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.adapter.PasswordAdapter
import com.calculator.vault.database.VaultDatabase
import com.calculator.vault.model.PasswordEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class PasswordsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PasswordAdapter
    private lateinit var categoryTabs: TabLayout
    
    private val categories = listOf("All", "Website", "Banking", "Credit Card", "Contacts")
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_passwords, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        categoryTabs = view.findViewById(R.id.categoryTabs)
        categories.forEach { categoryTabs.addTab(categoryTabs.newTab().setText(it)) }
        
        recyclerView = view.findViewById(R.id.passwordsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        adapter = PasswordAdapter(emptyList()) { entry ->
            showPasswordDetails(entry)
        }
        recyclerView.adapter = adapter
        
        view.findViewById<FloatingActionButton>(R.id.addPasswordFab).setOnClickListener {
            showAddPasswordDialog()
        }
        
        categoryTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadPasswords(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        loadPasswords(0)
    }
    
    private fun loadPasswords(categoryIndex: Int) {
        val db = VaultDatabase.getInstance(requireContext())
        val liveData = if (categoryIndex == 0) {
            db.passwordDao().getAllPasswords()
        } else {
            db.passwordDao().getPasswordsByCategory(categories[categoryIndex].uppercase())
        }
        
        liveData.observe(viewLifecycleOwner) { passwords ->
            adapter.updatePasswords(passwords)
        }
    }
    
    private fun showAddPasswordDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_password, null)
        val categorySpinner = view.findViewById<Spinner>(R.id.categorySpinner)
        
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.password_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add Password")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val entry = PasswordEntry(
                    category = categorySpinner.selectedItem.toString().uppercase(),
                    title = view.findViewById<EditText>(R.id.titleInput).text.toString(),
                    username = view.findViewById<EditText>(R.id.usernameInput).text.toString(),
                    password = view.findViewById<EditText>(R.id.passwordInput).text.toString(),
                    notes = view.findViewById<EditText>(R.id.notesInput).text.toString()
                )
                
                lifecycleScope.launch {
                    VaultDatabase.getInstance(requireContext()).passwordDao().insert(entry)
                    Toast.makeText(context, "Password saved", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPasswordDetails(entry: PasswordEntry) {
        // Show password details dialog
        AlertDialog.Builder(requireContext())
            .setTitle(entry.title)
            .setMessage("Username: ${entry.username}\nPassword: ${entry.password}")
            .setPositiveButton("OK", null)
            .show()
    }
}
