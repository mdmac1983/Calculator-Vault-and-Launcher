package com.calculator.vault.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.calculator.vault.fragment.*

class VaultPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    
    private val fragments = listOf(
        FilesFragment(),
        PasswordsFragment(),
        PlannerFragment(),
        HiddenAppsFragment()
    )
    
    private val titles = listOf("Files", "Passwords", "Planner", "Apps")

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
    
    fun getPageTitle(position: Int): String = titles[position]
}
