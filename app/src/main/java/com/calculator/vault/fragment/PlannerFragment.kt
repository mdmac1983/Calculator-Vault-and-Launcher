package com.calculator.vault.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.adapter.EventAdapter
import com.calculator.vault.database.VaultDatabase
import com.calculator.vault.model.CalendarEvent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.util.*

class PlannerFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var viewTabs: TabLayout
    
    private var selectedDate = Calendar.getInstance()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_planner, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewTabs = view.findViewById(R.id.viewTabs)
        listOf("Day", "Week", "Month").forEach {
            viewTabs.addTab(viewTabs.newTab().setText(it))
        }
        
        recyclerView = view.findViewById(R.id.eventsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        adapter = EventAdapter(emptyList())
        recyclerView.adapter = adapter
        
        view.findViewById<FloatingActionButton>(R.id.addEventFab).setOnClickListener {
            showAddEventDialog()
        }
        
        loadEvents()
    }
    
    private fun loadEvents() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        val start = cal.timeInMillis
        
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis
        
        VaultDatabase.getInstance(requireContext()).eventDao()
            .getEventsBetween(start, end)
            .observe(viewLifecycleOwner) { events ->
                adapter.updateEvents(events)
            }
    }
    
    private fun showAddEventDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val titleInput = view.findViewById<EditText>(R.id.eventTitleInput)
        val descInput = view.findViewById<EditText>(R.id.eventDescInput)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add Event")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val event = CalendarEvent(
                    title = titleInput.text.toString(),
                    description = descInput.text.toString(),
                    startTime = selectedDate.timeInMillis,
                    endTime = selectedDate.timeInMillis + 3600000 // 1 hour
                )
                
                lifecycleScope.launch {
                    VaultDatabase.getInstance(requireContext()).eventDao().insert(event)
                    Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
