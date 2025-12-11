package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BorrowHistoryFragment : Fragment() {

    private lateinit var borrowRepository: BorrowRepository
    private lateinit var borrowListAdapter:  BorrowListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tabLayout: TabLayout
    private var currentFilter = "All"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout. fragment_borrow_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(requireContext())
        borrowRepository = BorrowRepository(database. borrowListDAO())

        // Initialize views
        recyclerView = view.findViewById(R.id.borrow_history_recycler)
        emptyStateContainer = view.findViewById(R.id.empty_state_borrow)
        tabLayout = view.findViewById(R.id.tab_layout)

        // Setup RecyclerView
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = borrowListAdapter

        // Setup tabs
        setupTabs()

        // Load all borrow lists initially
        loadBorrowLists("All")
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Pending"))
        tabLayout.addTab(tabLayout.newTab().setText("Returned"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentFilter = tab?.text.toString()
                loadBorrowLists(currentFilter)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout. Tab?) {}
        })
    }

    private fun loadBorrowLists(filter: String) {
        lifecycleScope.launch {
            val flow = when (filter) {
                "Pending" -> borrowRepository.getBorrowListsByStatus("Pending")
                "Returned" -> borrowRepository.getBorrowListsByStatus("Returned")
                else -> borrowRepository.getAllBorrowLists()
            }

            flow.collectLatest { borrowLists ->
                if (borrowLists.isEmpty()) {
                    emptyStateContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyStateContainer.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    borrowListAdapter. updateBorrowLists(borrowLists)

                    // Load items for each borrow list
                    val itemsMap = mutableMapOf<Int, List<BorrowListItem>>()
                    borrowLists.forEach { borrowList ->
                        val items = borrowRepository.getBorrowListItems(borrowList.id)
                        itemsMap[borrowList.id] = items
                    }
                    borrowListAdapter.updateBorrowListItems(itemsMap)
                }
            }
        }
    }

    private fun viewBorrowDetails(borrowList: BorrowList) {
        val detailFragment = BorrowDetailFragment.newInstance(borrowList.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id. fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun markAsReturned(borrowList: BorrowList) {
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Returned")
            .setMessage("Mark this borrow list as returned?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val updatedBorrowList = borrowList.copy(
                        status = "Returned",
                        returnDate = System.currentTimeMillis()
                    )
                    borrowRepository.updateBorrowList(updatedBorrowList)
                    Toast.makeText(
                        requireContext(),
                        "Marked as returned successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteBorrowList(borrowList: BorrowList) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Borrow List")
            .setMessage("Are you sure you want to delete this borrow list? This action cannot be undone.")
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