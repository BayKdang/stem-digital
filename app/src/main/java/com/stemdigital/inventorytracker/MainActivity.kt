package com.stemdigital.inventorytracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        // Handle bottom navigation item clicks
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item. itemId) {
                R. id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to Home Fragment
                    true
                }
                R.id.nav_inventory -> {
                    Toast.makeText(this, "Inventory", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to Inventory Fragment
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
}