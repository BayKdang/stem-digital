package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BorrowDetailFragment : Fragment() {

    private lateinit var borrowRepository: BorrowRepository
    private lateinit var borrowList: BorrowList
    private lateinit var itemsAdapter: BorrowedItemsAdapter

    private lateinit var tvBorrowerName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvClassroom: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvBorrowDate: TextView
    private lateinit var tvReturnDate: TextView
    private lateinit var tvNotes: TextView
    private lateinit var rvBorrowedItems: RecyclerView
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnMarkReturned: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnBack: MaterialButton

    companion object {
        private const val ARG_BORROW_LIST_ID = "borrow_list_id"

        fun newInstance(borrowListId: Int): BorrowDetailFragment {
            val fragment = BorrowDetailFragment()
            val args = Bundle()
            args.putInt(ARG_BORROW_LIST_ID, borrowListId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater:  LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_borrow_detail, container, false)
    }

    override fun onViewCreated(view:  View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        borrowRepository = BorrowRepository(database. borrowListDAO())

        // Initialize views
        tvBorrowerName = view.findViewById(R.id.tv_borrower_name)
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number)
        tvDepartment = view.findViewById(R.id.tv_department)
        tvClassroom = view.findViewById(R.id.tv_classroom)
        tvStatus = view.findViewById(R.id.tv_status)
        tvBorrowDate = view.findViewById(R.id.tv_borrow_date)
        tvReturnDate = view.findViewById(R.id.tv_return_date)
        tvNotes = view.findViewById(R.id.tv_notes)
        rvBorrowedItems = view.findViewById(R.id.rv_borrowed_items)
        btnEdit = view.findViewById(R.id.btn_edit_borrow)
        btnMarkReturned = view.findViewById(R.id.btn_mark_returned_detail)
        btnDelete = view.findViewById(R.id.btn_delete_borrow_detail)
        btnBack = view.findViewById(R.id.btn_back)

        // Setup RecyclerView for borrowed items
        itemsAdapter = BorrowedItemsAdapter(emptyList())
        rvBorrowedItems.layoutManager = LinearLayoutManager(requireContext())
        rvBorrowedItems.adapter = itemsAdapter

        // Load borrow list data
        val borrowListId = arguments?.getInt(ARG_BORROW_LIST_ID) ?: -1
        if (borrowListId != -1) {
            loadBorrowListDetails(borrowListId)
        }

        // Button click listeners
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnEdit. setOnClickListener {
            editBorrowList()
        }

        btnMarkReturned.setOnClickListener {
            markAsReturned()
        }

        btnDelete.setOnClickListener {
            confirmDelete()
        }
    }

    private fun loadBorrowListDetails(borrowListId: Int) {
        lifecycleScope.launch {
            borrowList = borrowRepository.getBorrowListById(borrowListId) ?: return@launch

            // Populate UI
            tvBorrowerName.text = borrowList.borrowerName
            tvPhoneNumber.text = borrowList.phoneNumber
            tvDepartment.text = borrowList.department
            tvClassroom.text = borrowList.classroomNumber
            tvStatus.text = borrowList.status
            tvBorrowDate.text = formatDate(borrowList.borrowDate)
            tvReturnDate.text = if (borrowList.returnDate != null) {
                formatDate(borrowList.returnDate!!)
            } else {
                "Not returned yet"
            }
            tvNotes.text = if (borrowList.notes.isEmpty()) "No notes" else borrowList.notes

            // Update button states
            if (borrowList.status == "Returned") {
                btnMarkReturned.isEnabled = false
                btnMarkReturned.alpha = 0.5f
                btnEdit.isEnabled = false
                btnEdit.alpha = 0.5f
            }

            // Load borrowed items
            val items = borrowRepository.getBorrowListItems(borrowListId)
            itemsAdapter.updateItems(items)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun editBorrowList() {
        // TODO: Navigate to edit fragment
        Toast.makeText(requireContext(), "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun markAsReturned() {
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Returned")
            .setMessage("Mark this borrow list as returned?  This will restore the items to inventory.")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    try {
                        // Initialize item repository
                        val database = AppDatabase.getDatabase(requireContext())
                        val itemRepository = ItemRepository(database.itemDAO())

                        // Return items and update inventory
                        borrowRepository.returnBorrowListWithInventoryUpdate(
                            borrowList = borrowList,
                            itemRepository = itemRepository
                        )
                        Toast.makeText(
                            requireContext(),
                            "Marked as returned and inventory updated!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reload the details
                        loadBorrowListDetails(borrowList.id)
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Borrow List")
            .setMessage("Are you sure you want to delete this borrow list?  This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteBorrowList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteBorrowList() {
        lifecycleScope.launch {
            try {
                borrowRepository.deleteBorrowList(borrowList)
                Toast.makeText(
                    requireContext(),
                    "Borrow list deleted successfully! ",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error deleting borrow list: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}