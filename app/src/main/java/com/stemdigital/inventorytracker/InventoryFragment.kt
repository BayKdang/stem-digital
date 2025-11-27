package com.stemdigital.inventorytracker

import android.os. Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class InventoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: TextInputEditText
    private lateinit var filterDropdown: MaterialAutoCompleteTextView
    private lateinit var emptyStateContainer: View

    // Category list
    private val categories = listOf(
        "All Categories",
        "Projectors",
        "Cables (DP)",
        "Cables (HDMI)",
        "Strips",
        "Electronics",
        "Sensors",
        "Microcontrollers",
        "Resistors",
        "Capacitors"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view. findViewById(R.id.inventory_recycler_view)
        searchInput = view.findViewById(R. id.search_input)
        filterDropdown = view.findViewById(R.id.filter_dropdown)
        emptyStateContainer = view.findViewById(R.id.empty_state_container)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup filter dropdown
        setupFilterDropdown()

        // TODO: Load items from database and set adapter
        // For now, show empty state
        showEmptyState()

        // Search functionality
        searchInput.addTextChangedListener(object : android. text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: Filter items based on search text
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Filter dropdown listener
        // Filter dropdown listener
        filterDropdown. setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = categories[position]
            // TODO: Filter items by selected category

            // Dismiss dropdown and clear focus
            filterDropdown.dismissDropDown()
            filterDropdown.clearFocus()
        }
    }

    private fun setupFilterDropdown() {
        // Create adapter for dropdown
        val adapter = ArrayAdapter(
            requireContext(),
            android.R. layout.simple_dropdown_item_1line,
            categories
        )

        // Set adapter to dropdown
        filterDropdown.setAdapter(adapter)

        // Set dropdown height to show only ~5 items (half screen)
        filterDropdown.setDropDownHeight(800)

        // Show dropdown when clicked
        filterDropdown.setOnClickListener {
            filterDropdown.requestFocus()
            filterDropdown.showDropDown()
        }

        // Set default selection
        filterDropdown. setText(categories[0], false)
    }

    private fun showEmptyState() {
        emptyStateContainer.visibility = View. VISIBLE
        recyclerView. visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyStateContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}