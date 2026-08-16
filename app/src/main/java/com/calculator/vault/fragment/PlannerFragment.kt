package com.calculator.vault.ui

import com.calculator.vault.data.*
import com.calculator.vault.adapter.*
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.R
import com.calculator.vault.data.CalendarEvent
import com.calculator.vault.data.VaultDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Calendar

class PlannerFragment : Fragment() {
    
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val selectedDate = Calendar.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planner, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.eventsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedDate.set(year, month, day)
            loadEvents()
        }
        
        view.findViewById<FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            showAddEventDialog()
        }
        
        loadEvents()
    }
    
    private fun loadEvents() {
        lifecycleScope.launch {
            val startOfDay = selectedDate.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            
            val endOfDay = selectedDate.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis
            
            val events = VaultDatabase.getInstance(requireContext())
                .eventDao()
                .getEventsForRange(startOfDay, endOfDay)
            
            adapter = EventAdapter(events)
            recyclerView.adapter = adapter
        }
    }
    
    private fun showAddEventDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val titleInput = view.findViewById<EditText>(R.id.eventTitleInput)
        val descInput = view.findViewById<EditText>(R.id.eventDescInput)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add Event")
            .setView(view as android.view.View)
            .setPositiveButton("Save") { _, _ ->
                val event = CalendarEvent(
                    title = titleInput.text.toString(),
                    description = descInput.text.toString(),
                    startTime = selectedDate.timeInMillis,
                    endTime = selectedDate.timeInMillis + 3600000
                )
                
                lifecycleScope.launch {
                    VaultDatabase.getInstance(requireContext()).eventDao().insert(event)
                    Toast.makeText(context, "Event added", Toast.LENGTH_SHORT).show()
                    loadEvents()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
