package com.calculator.vault.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.data.PasswordEntry
import com.calculator.vault.data.VaultDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class PasswordsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PasswordAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_passwords, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.passwordsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        view.findViewById<FloatingActionButton>(R.id.fabAddPassword).setOnClickListener {
            showAddPasswordDialog()
        }
        
        loadPasswords()
    }
    
    private fun loadPasswords() {
        lifecycleScope.launch {
            val passwords = VaultDatabase.getInstance(requireContext())
                .passwordDao()
                .getAllPasswords()
            
            adapter = PasswordAdapter(passwords) { entry ->
                showPasswordOptions(entry)
            }
            recyclerView.adapter = adapter
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
            .setView(view as android.view.View)
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
                    loadPasswords()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPasswordOptions(entry: PasswordEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle(entry.title)
            .setItems(arrayOf("Copy Password", "Delete")) { _, which ->
                when (which) {
                    0 -> {
                        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("password", entry.password)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Password copied", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        lifecycleScope.launch {
                            VaultDatabase.getInstance(requireContext()).passwordDao().delete(entry)
                            loadPasswords()
                        }
                    }
                }
            }
            .show()
    }
}
