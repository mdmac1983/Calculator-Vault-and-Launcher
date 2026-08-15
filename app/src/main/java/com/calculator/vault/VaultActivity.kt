package com.calculator.vault

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.calculator.vault.adapter.VaultPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class VaultActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)
        
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val adapter = VaultPagerAdapter(this)
        viewPager.adapter = adapter
        
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Files"
                1 -> "Passwords"
                2 -> "Planner"
                3 -> "Hidden"
                4 -> "Settings"
                else -> ""
            }
            tab.setIcon(when (position) {
                0 -> R.drawable.ic_files
                1 -> R.drawable.ic_passwords
                2 -> R.drawable.ic_planner
                3 -> R.drawable.ic_hidden
                4 -> R.drawable.ic_settings
                else -> null
            })
        }.attach()
    }
}
