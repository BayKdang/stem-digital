package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android. view.View
import android.view.ViewGroup
import android.widget. Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var btnClearCache: MaterialButton
    private lateinit var btnResetData: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        switchDarkMode = view.findViewById(R.id.switch_dark_mode)
        switchNotifications = view.findViewById(R.id.switch_notifications)
        btnClearCache = view. findViewById(R.id.btn_clear_cache)
        btnResetData = view.findViewById(R.id.btn_reset_data)

        // Dark Mode toggle listener
        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT). show()
                // TODO: Implement dark mode
            } else {
                Toast.makeText(requireContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
                // TODO: Disable dark mode
            }
        }

        // Notifications toggle listener
        switchNotifications.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show()
                // TODO: Enable notifications
            } else {
                Toast.makeText(requireContext(), "Notifications Disabled", Toast. LENGTH_SHORT).show()
                // TODO: Disable notifications
            }
        }

        // Clear Cache button listener
        btnClearCache.setOnClickListener {
            Toast.makeText(requireContext(), "Cache cleared", Toast.LENGTH_SHORT).show()
            // TODO: Implement clear cache functionality
        }

        // Reset Data button listener
        btnResetData.setOnClickListener {
            // Show confirmation dialog
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reset All Data")
                .setMessage("Are you sure you want to delete all inventory data?  This cannot be undone.")
                .setPositiveButton("Yes, Delete") { dialog, which ->
                    Toast.makeText(requireContext(), "All data reset", Toast.LENGTH_SHORT).show()
                    // TODO: Implement reset data functionality
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}