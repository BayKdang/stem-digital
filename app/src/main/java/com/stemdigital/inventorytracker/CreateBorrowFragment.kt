package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CreateBorrowFragment :  Fragment() {

    private lateinit var repository: ItemRepository
    private lateinit var borrowRepository: BorrowRepository

    private lateinit var etBorrowerName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etDepartment: EditText
    private lateinit var etClassroom: EditText
    private lateinit var spinnerProjector: Spinner
    private lateinit var spinnerPowerStrip: Spinner
    private lateinit var checkboxHDMI: MaterialCheckBox
    private lateinit var checkboxVGA: MaterialCheckBox
    private lateinit var checkboxPointer: MaterialCheckBox
    private lateinit var checkboxExtensionCord: MaterialCheckBox
    private lateinit var etNotes: EditText
    private lateinit var btnCreateBorrowList: MaterialButton

    private var availableProjectors = listOf<Item>()
    private var availablePowerStrips = listOf<Item>()
    private var availableAccessories = mutableMapOf<String, Item>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_borrow, container, false)
    }

    override fun onViewCreated(view:  View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        repository = ItemRepository(database.itemDAO())
        borrowRepository = BorrowRepository(database.borrowListDAO())

        // Initialize views
        etBorrowerName = view.findViewById(R.id.et_borrower_name)
        etPhoneNumber = view.findViewById(R.id.et_phone_number)
        etDepartment = view.findViewById(R.id.et_department)
        etClassroom = view.findViewById(R.id.et_classroom)
        spinnerProjector = view.findViewById(R.id.spinner_projector)
        spinnerPowerStrip = view.findViewById(R.id.spinner_power_strip)
        checkboxHDMI = view.findViewById(R.id.checkbox_hdmi)
        checkboxVGA = view.findViewById(R.id.checkbox_vga)
        checkboxPointer = view.findViewById(R.id.checkbox_pointer)
        checkboxExtensionCord = view.findViewById(R.id.checkbox_extension_cord)
        etNotes = view.findViewById(R.id.et_borrow_notes)
        btnCreateBorrowList = view.findViewById(R.id.btn_create_borrow_list)

        // Load available items
        loadAvailableItems()

        // Create borrow list button
        btnCreateBorrowList.setOnClickListener {
            createBorrowList()
        }
    }

    private fun loadAvailableItems() {
        lifecycleScope.launch {
            // Load Projectors
            val projectors = repository.getAvailableItemsByCategories(listOf("Projector"))
                .firstOrNull() ?: emptyList()
            availableProjectors = projectors.filter { it.availableQuantity > 0 }

            val projectorNames = listOf("Select Projector") + availableProjectors.map {
                "${it.name} - ${it.serialNumber}"
            }
            val projectorAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                projectorNames
            )
            projectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerProjector.adapter = projectorAdapter

            // Load Power Strips
            val powerStrips = repository.getAvailableItemsByCategories(listOf("Power Strip"))
                .firstOrNull() ?: emptyList()
            availablePowerStrips = powerStrips.filter { it. availableQuantity > 0 }

            val powerStripNames = listOf("Select Power Strip") + availablePowerStrips.map {
                "${it.name} - ${it. serialNumber}"
            }
            val powerStripAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                powerStripNames
            )
            powerStripAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPowerStrip.adapter = powerStripAdapter

            // Load Accessories (cables, pointers, etc.)
            val accessories = repository.getAvailableItemsByCategories(
                listOf("Cable", "Pointer", "Extension Cord", "Accessory")
            ).firstOrNull() ?:emptyList()

            accessories.forEach { item ->
                when {
                    item.name.contains("HDMI", ignoreCase = true) -> {
                        availableAccessories["HDMI"] = item
                        checkboxHDMI.isEnabled = item.availableQuantity > 0
                    }
                    item.name.contains("VGA", ignoreCase = true) -> {
                        availableAccessories["VGA"] = item
                        checkboxVGA.isEnabled = item.availableQuantity > 0
                    }
                    item.name.contains("Pointer", ignoreCase = true) -> {
                        availableAccessories["Pointer"] = item
                        checkboxPointer.isEnabled = item.availableQuantity > 0
                    }
                    item.name.contains("Extension", ignoreCase = true) -> {
                        availableAccessories["Extension Cord"] = item
                        checkboxExtensionCord.isEnabled = item.availableQuantity > 0
                    }
                }
            }
        }
    }

    private fun createBorrowList() {
        // Validate inputs
        val borrowerName = etBorrowerName.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val department = etDepartment.text.toString().trim()
        val classroom = etClassroom.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        if (borrowerName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter borrower name", Toast.LENGTH_SHORT).show()
            return
        }

        if (phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (department.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter department", Toast.LENGTH_SHORT).show()
            return
        }

        if (classroom.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter classroom number", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect selected items
        val selectedItems = mutableListOf<BorrowListItem>()

        // Check projector selection
        if (spinnerProjector.selectedItemPosition > 0) {
            val projector = availableProjectors[spinnerProjector.selectedItemPosition - 1]
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0, // Will be set after insert
                    itemId = projector.id,
                    itemName = "${projector.name} - ${projector.serialNumber}",
                    quantityBorrowed = 1
                )
            )
        }

        // Check power strip selection
        if (spinnerPowerStrip.selectedItemPosition > 0) {
            val powerStrip = availablePowerStrips[spinnerPowerStrip.selectedItemPosition - 1]
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0,
                    itemId = powerStrip.id,
                    itemName = "${powerStrip.name} - ${powerStrip.serialNumber}",
                    quantityBorrowed = 1
                )
            )
        }

        // Check accessory checkboxes
        if (checkboxHDMI.isChecked && availableAccessories.containsKey("HDMI")) {
            val hdmi = availableAccessories["HDMI"]!!
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0,
                    itemId = hdmi.id,
                    itemName = hdmi.name,
                    quantityBorrowed = 1
                )
            )
        }

        if (checkboxVGA.isChecked && availableAccessories.containsKey("VGA")) {
            val vga = availableAccessories["VGA"]!!
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0,
                    itemId = vga.id,
                    itemName = vga.name,
                    quantityBorrowed = 1
                )
            )
        }

        if (checkboxPointer.isChecked && availableAccessories.containsKey("Pointer")) {
            val pointer = availableAccessories["Pointer"]!!
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0,
                    itemId = pointer.id,
                    itemName = pointer.name,
                    quantityBorrowed = 1
                )
            )
        }

        if (checkboxExtensionCord.isChecked && availableAccessories.containsKey("Extension Cord")) {
            val extensionCord = availableAccessories["Extension Cord"]!!
            selectedItems.add(
                BorrowListItem(
                    borrowListId = 0,
                    itemId = extensionCord.id,
                    itemName = extensionCord.name,
                    quantityBorrowed = 1
                )
            )
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one item to borrow", Toast.LENGTH_SHORT).show()
            return
        }

        // Create borrow list
        lifecycleScope.launch {
            try {
                val borrowList = BorrowList(
                    borrowerName = borrowerName,
                    phoneNumber = phoneNumber,
                    department = department,
                    classroomNumber = classroom,
                    borrowDate = System.currentTimeMillis(),
                    status = "Not Returned",
                    notes = notes
                )

                // Create borrow list and update inventory
                borrowRepository.createBorrowListWithInventoryUpdate(
                    borrowList = borrowList,
                    items = selectedItems,
                    itemRepository = repository
                )

                Toast.makeText(
                    requireContext(),
                    "Borrow list created successfully! ",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate back to home
                parentFragmentManager.popBackStack()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error creating borrow list: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}