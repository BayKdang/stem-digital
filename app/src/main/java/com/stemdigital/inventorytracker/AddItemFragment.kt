package com.stemdigital.inventorytracker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var quantityInput: TextInputEditText
    private lateinit var categoryDropdown: MaterialAutoCompleteTextView
    private lateinit var serialNumberInput: TextInputEditText
    private lateinit var statusDropdown: MaterialAutoCompleteTextView
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var repository: ItemRepository

    // Image components
    private lateinit var selectedImageButton: MaterialButton
    private lateinit var itemImageView: ImageView
    private var selectedBitmap: Bitmap?  = null

    private val categories = listOf(
        "Projector",
        "Power Strip",
        "Cable",
        "Pointer",
        "Extension Cord",
        "Accessory",
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

    // Gallery picker
    private val pickImageFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES. P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            }
            itemImageView.setImageBitmap(selectedBitmap)
            selectedImageButton.text = "Change Image"
        }
    }

    // Camera picker
    private val pickImageFromCamera = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            itemImageView.setImageBitmap(selectedBitmap)
            selectedImageButton.text = "Change Image"
        }
    }

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
        nameInput = view.findViewById(R.id.et_item_name)
        quantityInput = view.findViewById(R.id.et_item_quantity)
        categoryDropdown = view.findViewById(R.id.et_item_category)
        serialNumberInput = view.findViewById(R.id.et_item_serial_number)
        statusDropdown = view.findViewById(R.id.et_item_status)
        descriptionInput = view.findViewById(R.id.et_item_description)
        addButton = view.findViewById(R.id.btn_add_item)
        cancelButton = view.findViewById(R.id.btn_cancel)

        // Image components
        itemImageView = view.findViewById(R.id.item_image_view)
        selectedImageButton = view.findViewById(R.id.btn_select_image)

        // Setup dropdowns
        setupCategoryDropdown()
        setupStatusDropdown()

        // Setup image picker
        setupImagePicker()

        // Add button click listener
        addButton.setOnClickListener {
            addItem()
        }

        // Cancel button click listener
        cancelButton.setOnClickListener {
            clearInputs()
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupImagePicker() {
        selectedImageButton.setOnClickListener {
            // Show dialog with camera and gallery options
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                    when (which) {
                        0 -> pickImageFromCamera.launch(null)
                        1 -> pickImageFromGallery.launch("image/*")
                    }
                }
                .show()
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
        statusDropdown.setText("Available", false)

        statusDropdown.setOnClickListener {
            statusDropdown.requestFocus()
            statusDropdown.showDropDown()
        }
    }

    private fun addItem() {
        val name = nameInput.text.toString().trim()
        val quantityStr = quantityInput.text.toString().trim()
        val category = categoryDropdown.text.toString().trim()
        val serialNumber = serialNumberInput.text.toString().trim()
        val status = statusDropdown.text.toString().trim()
        val notes = descriptionInput.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            nameInput.error = "Item name is required"
            return
        }

        if (quantityStr.isEmpty()) {
            quantityInput.error = "Quantity is required"
            return
        }

        if (category.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Please select a category", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        if (status.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Please select a status", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 0

        if (quantity <= 0) {
            quantityInput.error = "Quantity must be greater than 0"
            return
        }

        // Create new item with all required fields
        val newItem = Item(
            name = name,
            category = category,
            serialNumber = serialNumber,
            quantity = quantity,
            availableQuantity = quantity,
            status = status,
            location = "",
            dateAdded = System.currentTimeMillis(),
            lastUpdated = System.currentTimeMillis(),
            notes = notes,
            imageUri = "",  // Will be updated after image is saved
            currentBorrowId = ""  // NEW:  Add this field
        )

        // Add to database
        lifecycleScope.launch {
            val insertedItemId = repository.insertItem(newItem).toInt()

            // Save image if selected
            if (selectedBitmap != null) {
                val imagePath = ImageUtils.saveBitmapToInternalStorage(requireContext(), selectedBitmap!!, insertedItemId)
                if (! imagePath.isNullOrEmpty()) {
                    val updatedItem = newItem.copy(id = insertedItemId, imageUri = imagePath)
                    repository.updateItem(updatedItem)
                }
            }

            android.widget.Toast.makeText(requireContext(), "Item added successfully!", android.widget.Toast.LENGTH_SHORT).show()
            clearInputs()
            parentFragmentManager.popBackStack()
        }
    }

    private fun clearInputs() {
        nameInput.text?. clear()
        quantityInput.text?.clear()
        categoryDropdown.text?.clear()
        serialNumberInput.text?.clear()
        statusDropdown.setText("Available", false)
        descriptionInput.text?.clear()
        itemImageView.setImageResource(R.drawable.ic_placeholder_image)
        selectedImageButton.text = "Select Image"
        selectedBitmap = null
    }
}