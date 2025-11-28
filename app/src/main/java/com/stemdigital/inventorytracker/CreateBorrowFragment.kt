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
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateBorrowFragment : Fragment() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var departmentDropdown: MaterialAutoCompleteTextView
    private lateinit var classroomInput: TextInputEditText
    private lateinit var itemsDropdown: MaterialAutoCompleteTextView
    private lateinit var quantityInput: TextInputEditText
    private lateinit var notesInput: TextInputEditText
    private lateinit var createButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var borrowRepository: BorrowRepository
    private lateinit var itemRepository: ItemRepository

    private val departments = listOf(
        "Computer Science",
        "ITE",
        "Engineering",
        "Biology",
        "Physics",
        "Chemistry",
        "Robotics",
        "Electronics",
        "General Studies"
    )

    private var availableItems: List<Item> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_borrow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        borrowRepository = BorrowRepository(database.borrowListDAO())
        itemRepository = ItemRepository(database.itemDAO())

        // Initialize views
        nameInput = view.findViewById(R.id.et_borrower_name)
        phoneInput = view.findViewById(R.id.et_borrower_phone)
        departmentDropdown = view.findViewById(R.id.et_borrower_department)
        classroomInput = view.findViewById(R.id.et_borrower_classroom)
        itemsDropdown = view.findViewById(R.id.et_borrow_item)
        quantityInput = view.findViewById(R.id.et_borrow_quantity)
        notesInput = view.findViewById(R.id.et_borrow_notes)
        createButton = view.findViewById(R.id.btn_create_borrow_list)
        cancelButton = view.findViewById(R.id.btn_cancel_borrow)

        // Setup dropdowns
        setupDepartmentDropdown()
        setupItemsDropdown()

        // Load available items
        loadAvailableItems()

        // Create button click listener
        createButton.setOnClickListener {
            createBorrowList()
        }

        // Cancel button click listener
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupDepartmentDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            departments
        )
        departmentDropdown.setAdapter(adapter)

        departmentDropdown.setOnClickListener {
            departmentDropdown.requestFocus()
            departmentDropdown.showDropDown()
        }
    }

    private fun setupItemsDropdown() {
        itemsDropdown.setOnClickListener {
            itemsDropdown.requestFocus()
            itemsDropdown.showDropDown()
        }
    }

    private fun loadAvailableItems() {
        lifecycleScope.launch {
            itemRepository.getAllItems().collectLatest { items ->
                availableItems = items. filter { it.quantity > 0 }
                val itemNames = availableItems.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    itemNames
                )
                itemsDropdown.setAdapter(adapter)
            }
        }
    }

    private fun createBorrowList() {
        val borrowerName = nameInput.text.toString().trim()
        val phoneNumber = phoneInput.text.toString().trim()
        val department = departmentDropdown.text.toString().trim()
        val classroomNumber = classroomInput.text.toString().trim()
        val selectedItemName = itemsDropdown.text.toString().trim()
        val quantityStr = quantityInput.text.toString().trim()
        val notes = notesInput.text.toString(). trim()

        // Validation
        if (borrowerName.isEmpty()) {
            nameInput.error = "Borrower name is required"
            return
        }

        if (phoneNumber.isEmpty()) {
            phoneInput.error = "Phone number is required"
            return
        }

        if (department.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a department", Toast.LENGTH_SHORT).show()
            return
        }

        if (classroomNumber.isEmpty()) {
            classroomInput.error = "Classroom number is required"
            return
        }

        if (selectedItemName.isEmpty()) {
            Toast.makeText(requireContext(), "Please select an item", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantityStr.isEmpty()) {
            quantityInput.error = "Quantity is required"
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 0
        if (quantity <= 0) {
            quantityInput.error = "Quantity must be greater than 0"
            return
        }

        // Find the selected item
        val selectedItem = availableItems.find { it.name == selectedItemName }
        if (selectedItem == null) {
            Toast.makeText(requireContext(), "Invalid item selected", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantity > selectedItem.quantity) {
            Toast.makeText(requireContext(), "Not enough items available", Toast.LENGTH_SHORT).show()
            return
        }

        // Create borrow list
        val borrowList = BorrowList(
            borrowerName = borrowerName,
            phoneNumber = phoneNumber,
            department = department,
            classroomNumber = classroomNumber,
            status = "Pending",
            notes = notes
        )

        // Save to database
        lifecycleScope.launch {
            val borrowListId = borrowRepository.insertBorrowList(borrowList).toInt()

            // Create borrow list item
            val borrowListItem = BorrowListItem(
                borrowListId = borrowListId,
                itemId = selectedItem.id,
                itemName = selectedItem.name,
                quantityBorrowed = quantity
            )
            borrowRepository.insertBorrowListItem(borrowListItem)

            Toast.makeText(
                requireContext(),
                "Borrow list created successfully! ",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
        }
    }
}