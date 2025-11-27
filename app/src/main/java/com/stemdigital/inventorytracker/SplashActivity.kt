package com.stemdigital.inventorytracker

import android. content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the action bar for a clean splash screen
        supportActionBar?.hide()

        // Wait 3 seconds (3000 milliseconds), then go to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class. java))
            finish() // Close the splash screen so user can't go back to it
        }, 3000) // 3000ms = 3 seconds
    }
}