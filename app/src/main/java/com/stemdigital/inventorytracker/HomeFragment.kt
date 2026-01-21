package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment. app.Fragment
import android.view.LayoutInflater
import android.view.View
import android. view.ViewGroup
import android. widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx. appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget. RecyclerView
import com. google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var repository: ItemRepository
    private lateinit var borrowRepository: BorrowRepository
    private lateinit var borrowListAdapter: BorrowListAdapter
    private lateinit var recentBorrowRecyclerView: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var totalItemsCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        repository = ItemRepository(database. itemDAO())
        borrowRepository = BorrowRepository(database.borrowListDAO())

        // Initialize views
        val btnCreateBorrow:  MaterialButton = view.findViewById(R.id.btn_create_borrow)
        val btnViewInventory: MaterialButton = view.findViewById(R.id.btn_view_inventory)
        recentBorrowRecyclerView = view.findViewById(R.id.recent_borrow_list)
        emptyStateContainer = view.findViewById(R.id. empty_state_container)
        totalItemsCount = view.findViewById(R.id.total_items_count)

        // Setup RecyclerView with click handlers
        borrowListAdapter = BorrowListAdapter(
            borrowLists = emptyList(),
            onViewDetails = { borrowList ->
                viewBorrowDetails(borrowList)
            },
            onMarkReturned = { borrowList ->
                markAsReturned(borrowList)
            },
            onDelete = { borrowList ->
                confirmDeleteBorrowList(borrowList)
            }
        )
        recentBorrowRecyclerView. layoutManager = LinearLayoutManager(requireContext())
        recentBorrowRecyclerView.adapter = borrowListAdapter

        // Load total items count
        loadTotalItemsCount()

        // Load recent borrow lists
        loadRecentBorrowLists()

        // Create Borrow List button click listener
        btnCreateBorrow. setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id. fragment_container, CreateBorrowFragment())
                .addToBackStack(null)
                .commit()
        }

        // View Inventory button click listener
        btnViewInventory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InventoryFragment())
                .addToBackStack(null)
                .commit()
        }

        val btnViewBorrowHistory:  MaterialButton = view.findViewById(R.id.btn_view_borrow_history)

        btnViewBorrowHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BorrowHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadTotalItemsCount() {
        lifecycleScope.launch {
            repository.getAllItems().collectLatest { items ->
                val totalCount = items.sumOf { it. quantity }
                totalItemsCount.text = totalCount.toString()
            }
        }
    }

    private fun loadRecentBorrowLists() {
        lifecycleScope.launch {
            borrowRepository.getRecentBorrowLists().collectLatest { borrowLists ->
                if (borrowLists.isEmpty()) {
                    emptyStateContainer.visibility = View.VISIBLE
                    recentBorrowRecyclerView.visibility = View.GONE
                } else {
                    emptyStateContainer. visibility = View.GONE
                    recentBorrowRecyclerView.visibility = View. VISIBLE
                    borrowListAdapter. updateBorrowLists(borrowLists)

                    // Load items for each borrow list
                    val itemsMap = mutableMapOf<Int, List<BorrowListItem>>()
                    borrowLists.forEach { borrowList ->
                        val items = borrowRepository.getBorrowListItems(borrowList. id)
                        itemsMap[borrowList.id] = items
                    }
                    borrowListAdapter.updateBorrowListItems(itemsMap)
                }
            }
        }
    }

    private fun viewBorrowDetails(borrowList: BorrowList) {
        val detailFragment = BorrowDetailFragment.newInstance(borrowList. id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun markAsReturned(borrowList: BorrowList) {
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Returned")
            .setMessage("Mark this borrow list as returned?  This will restore the items to inventory.")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    try {
                        // Return items and update inventory
                        borrowRepository.returnBorrowListWithInventoryUpdate(
                            borrowList = borrowList,
                            itemRepository = repository
                        )
                        Toast.makeText(
                            requireContext(),
                            "Marked as returned and inventory updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Error:  ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteBorrowList(borrowList: BorrowList) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Borrow List")
            .setMessage("Are you sure you want to delete this borrow list?  This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteBorrowList(borrowList)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteBorrowList(borrowList: BorrowList) {
        lifecycleScope.launch {
            try {
                borrowRepository.deleteBorrowList(borrowList)
                Toast.makeText(
                    requireContext(),
                    "Borrow list deleted successfully! ",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error deleting borrow list:  ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}