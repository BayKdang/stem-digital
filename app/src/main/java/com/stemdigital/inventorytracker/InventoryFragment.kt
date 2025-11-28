package com.stemdigital.inventorytracker

import android.os. Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.text.TextWatcher
import android.text. Editable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview. widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com. google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: TextInputEditText
    private lateinit var filterDropdown: MaterialAutoCompleteTextView
    private lateinit var emptyStateContainer: View
    private lateinit var repository: ItemRepository
    private lateinit var itemAdapter: ItemAdapter

    // Category list
    private val categories = listOf(
        "All Categories",
        "Projectors",
        "DP",
        "HDMI",
        "Strips",
        "Electronics",
        "Sensors",
        "Microcontrollers",
        "Resistors",
        "Capacitors"
    )

    private var allItems: List<Item> = emptyList()
    private var selectedCategory = "All Categories"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        repository = ItemRepository(database.itemDAO())

        // Initialize views
        recyclerView = view.findViewById(R. id.inventory_recycler_view)
        searchInput = view.findViewById(R.id.search_input)
        filterDropdown = view.findViewById(R.id.filter_dropdown)
        emptyStateContainer = view.findViewById(R.id.empty_state_container)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(
            emptyList(),
            onEdit = { item ->
                // Navigate to EditItemFragment with item ID
                val bundle = Bundle(). apply {
                    putInt("item_id", item.id)
                }
                val editFragment = EditItemFragment(). apply {
                    arguments = bundle
                }
                // Replace current fragment with EditItemFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDelete = { item ->
                // Show confirmation dialog
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"${item.name}\"?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        // Delete item from database
                        lifecycleScope.launch {
                            repository.deleteItem(item)
                            android.widget.Toast.makeText(
                                requireContext(),
                                "Deleted: ${item.name}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )
        recyclerView.adapter = itemAdapter

        // Setup filter dropdown
        setupFilterDropdown()

        // Load items from database
        loadItems()

        // Search functionality
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Filter dropdown listener
        filterDropdown.setOnItemClickListener { parent, view, position, id ->
            selectedCategory = categories[position]
            filterItems()

            // Dismiss dropdown and clear focus
            filterDropdown.dismissDropDown()
            filterDropdown.clearFocus()
        }
    }

    private fun setupFilterDropdown() {
        // Create adapter for dropdown
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
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
        filterDropdown.setText(categories[0], false)
    }

    private fun loadItems() {
        lifecycleScope.launch {
            repository.getAllItems().collectLatest { items ->
                allItems = items
                if (allItems.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    filterItems()
                }
            }
        }
    }

    private fun filterItems() {
        var filteredItems = allItems

        // Filter by category
        if (selectedCategory != "All Categories") {
            filteredItems = filteredItems.filter { it.category == selectedCategory }
        }

        // Filter by search text
        val searchText = searchInput.text?.toString()?. trim() ?: ""
        if (searchText.isNotEmpty()) {
            filteredItems = filteredItems.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.description.contains(searchText, ignoreCase = true)
            }
        }

        // Update adapter
        itemAdapter.updateItems(filteredItems)

        // Show empty state if no items
        if (filteredItems.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    private fun showEmptyState() {
        emptyStateContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyStateContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}