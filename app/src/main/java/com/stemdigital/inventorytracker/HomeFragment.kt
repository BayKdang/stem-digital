package com.stemdigital.inventorytracker

import android.os. Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com. google.android.material.button. MaterialButton

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize buttons
        val btnAddItem: MaterialButton = view.findViewById(R.id.btn_add_item)
        val btnViewInventory: MaterialButton = view.findViewById(R.id.btn_view_inventory)

        // Add Item button click listener
        btnAddItem. setOnClickListener {
            Toast.makeText(requireContext(), "Add Item clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Add Item page
        }

        // View Inventory button click listener
        btnViewInventory.setOnClickListener {
            Toast.makeText(requireContext(), "View Inventory clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Inventory page
        }
    }
}