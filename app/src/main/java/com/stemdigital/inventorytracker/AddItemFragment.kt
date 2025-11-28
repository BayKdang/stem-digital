package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com. google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {

    private lateinit var etItemName: TextInputEditText
    private lateinit var etItemQuantity: TextInputEditText
    private lateinit var etItemCategory: MaterialAutoCompleteTextView
    private lateinit var etItemDescription: TextInputEditText
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnAddItem: MaterialButton
    private lateinit var repository: ItemRepository

    // Category list
    private val categories = listOf(
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

        // Initialize views
        etItemName = view.findViewById(R.id.et_item_name)
        etItemQuantity = view.findViewById(R.id.et_item_quantity)
        etItemCategory = view.findViewById(R.id.et_item_category)
        etItemDescription = view.findViewById(R.id.et_item_description)
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnAddItem = view.findViewById(R.id.btn_add_item)

        // Setup category dropdown
        setupCategoryDropdown()

        // Cancel button listener
        btnCancel.setOnClickListener {
            clearAllFields()
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            // TODO: Navigate back to inventory fragment
        }

        // Add Item button listener
        btnAddItem.setOnClickListener {
            validateAndAddItem()
        }
    }

    private fun setupCategoryDropdown() {
        // Create adapter for dropdown
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )

        // Set adapter to dropdown
        etItemCategory.setAdapter(adapter)

        // Set dropdown height
        etItemCategory.setDropDownHeight(800)

        // Show dropdown when clicked
        etItemCategory.setOnClickListener {
            etItemCategory.requestFocus()
            etItemCategory.showDropDown()
        }
    }

    private fun validateAndAddItem() {
        val itemName = etItemName.text?.toString()?.trim() ?: ""
        val itemQuantity = etItemQuantity.text?.toString()?.trim() ?: ""
        val itemCategory = etItemCategory.text?.toString()?.trim() ?: ""
        val itemDescription = etItemDescription.text?.toString()?.trim() ?: ""

        // Validation
        when {
            itemName.isEmpty() -> {
                etItemName.error = "Item name is required"
                Toast.makeText(requireContext(), "Please enter item name", Toast.LENGTH_SHORT).show()
            }
            itemQuantity.isEmpty() -> {
                etItemQuantity.error = "Quantity is required"
                Toast.makeText(requireContext(), "Please enter quantity", Toast.LENGTH_SHORT).show()
            }
            itemCategory.isEmpty() -> {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // All validations passed - Save to database
                val quantity = itemQuantity.toIntOrNull() ?: 0
                val newItem = Item(
                    name = itemName,
                    quantity = quantity,
                    category = itemCategory,
                    description = itemDescription
                )

                // Save to database using coroutine
                lifecycleScope.launch {
                    repository.insertItem(newItem)
                    Toast.makeText(
                        requireContext(),
                        "Item added: $itemName (Qty: $quantity)",
                        Toast.LENGTH_SHORT
                    ). show()
                    clearAllFields()
                    // TODO: Navigate back to inventory fragment
                }
            }
        }
    }

    private fun clearAllFields() {
        etItemName.text?.clear()
        etItemQuantity.text?.clear()
        etItemCategory.text?.clear()
        etItemDescription.text?.clear()
    }
}