package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditItemFragment : Fragment() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var quantityInput: TextInputEditText
    private lateinit var categoryDropdown: MaterialAutoCompleteTextView
    private lateinit var serialNumberInput: TextInputEditText
    private lateinit var statusDropdown: MaterialAutoCompleteTextView
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var updateButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var repository: ItemRepository

    private var itemId: Int = -1
    private lateinit var currentItem: Item

    private val categories = listOf(
        "Projectors",
        "Cables",
        "Strips",
        "Electronics",
        "Sensors",
        "Microcontrollers",
        "Resistors",
        "Capacitors"
    )

    private val statuses = listOf(
        "Available",
        "In Use",
        "Damaged",
        "Maintenance",
        "Archived"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        repository = ItemRepository(database.itemDAO())

        // Get item ID from arguments
        itemId = arguments?.getInt("item_id", -1) ?: -1

        // Initialize views
        nameInput = view.findViewById(R.id.et_item_name)
        quantityInput = view.findViewById(R.id.et_item_quantity)
        categoryDropdown = view.findViewById(R.id.et_item_category)
        serialNumberInput = view.findViewById(R.id.et_item_serial_number)
        statusDropdown = view. findViewById(R.id.et_item_status)
        descriptionInput = view.findViewById(R.id.et_item_description)
        updateButton = view.findViewById(R.id.btn_add_item)
        cancelButton = view.findViewById(R.id.btn_cancel)

        // Change button text to "Update"
        updateButton.text = "Update Item"

        // Change title to "Edit Item"
        view.findViewById<android.widget.TextView>(R.id.add_item_title).text = "Edit Item"
        view.findViewById<android.widget.TextView>(R.id.add_item_subtitle).text = "Update the details below"

        // Setup dropdowns
        setupCategoryDropdown()
        setupStatusDropdown()

        // Load item data
        loadItemData()

        // Update button click listener
        updateButton.setOnClickListener {
            updateItem()
        }

        // Cancel button click listener
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        categoryDropdown.setAdapter(adapter)

        categoryDropdown.setOnClickListener {
            categoryDropdown.requestFocus()
            categoryDropdown.showDropDown()
        }
    }

    private fun setupStatusDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statuses
        )
        statusDropdown.setAdapter(adapter)

        statusDropdown.setOnClickListener {
            statusDropdown.requestFocus()
            statusDropdown.showDropDown()
        }
    }

    private fun loadItemData() {
        if (itemId == -1) {
            android.widget.Toast.makeText(
                requireContext(),
                "Error: Item not found",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            repository.getAllItems().collectLatest { items ->
                currentItem = items.find { it. id == itemId } ?: return@collectLatest

                // Populate fields with current item data
                nameInput.setText(currentItem.name)
                quantityInput.setText(currentItem.quantity.toString())
                categoryDropdown.setText(currentItem.category, false)
                serialNumberInput.setText(currentItem.serialNumber)
                statusDropdown.setText(currentItem.status, false)
                descriptionInput.setText(currentItem.description)
            }
        }
    }

    private fun updateItem() {
        val name = nameInput.text.toString().trim()
        val quantityStr = quantityInput.text.toString().trim()
        val category = categoryDropdown.text.toString().trim()
        val serialNumber = serialNumberInput.text.toString().trim()
        val status = statusDropdown.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            nameInput.error = "Item name is required"
            return
        }

        if (quantityStr.isEmpty()) {
            quantityInput.error = "Quantity is required"
            return
        }

        if (category. isEmpty()) {
            android.widget.Toast.makeText(
                requireContext(),
                "Please select a category",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (status. isEmpty()) {
            android.widget.Toast.makeText(
                requireContext(),
                "Please select a status",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 0

        // Create updated item
        val updatedItem = currentItem.copy(
            name = name,
            quantity = quantity,
            category = category,
            serialNumber = serialNumber,
            status = status,
            description = description
        )

        // Update in database
        lifecycleScope. launch {
            repository.updateItem(updatedItem)
            android.widget.Toast.makeText(
                requireContext(),
                "Item updated successfully! ",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        }
    }
}