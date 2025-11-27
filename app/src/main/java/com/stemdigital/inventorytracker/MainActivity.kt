package com.stemdigital.inventorytracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Set default selection to Home
        bottomNavigation.selectedItemId = R.id.nav_home
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Handle bottom navigation item clicks
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item. itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_inventory -> {
                    loadFragment(InventoryFragment())
                    true
                }
                R.id.nav_add -> {
                    Toast.makeText(this, "Add Item", Toast.LENGTH_SHORT).show()
                    // TODO: Open Add Item Dialog/Activity
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to Settings Fragment
                    true
                }
                R.id.nav_info -> {
                    Toast. makeText(this, "Info", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to Info Fragment
                    true
                }
                else -> false
            }
        }
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
