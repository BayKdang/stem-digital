package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview. widget.RecyclerView
import com.google.android.material. button.MaterialButton
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
        repository = ItemRepository(database.itemDAO())
        borrowRepository = BorrowRepository(database.borrowListDAO())

        // Initialize views
        val btnCreateBorrow: MaterialButton = view.findViewById(R.id.btn_create_borrow)
        val btnViewInventory: MaterialButton = view.findViewById(R.id.btn_view_inventory)
        recentBorrowRecyclerView = view.findViewById(R.id.recent_borrow_list)
        emptyStateContainer = view.findViewById(R.id.empty_state_container)
        totalItemsCount = view.findViewById(R.id.total_items_count)

        // Setup RecyclerView
        borrowListAdapter = BorrowListAdapter(emptyList())
        recentBorrowRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentBorrowRecyclerView.adapter = borrowListAdapter

        // Load total items count
        loadTotalItemsCount()

        // Load recent borrow lists
        loadRecentBorrowLists()

        // Create Borrow List button click listener
        btnCreateBorrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateBorrowFragment())
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
    }

    private fun loadTotalItemsCount() {
        lifecycleScope.launch {
            repository.getAllItems().collectLatest { items ->
                val totalCount = items.sumOf { it.quantity }
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
                    emptyStateContainer.visibility = View.GONE
                    recentBorrowRecyclerView.visibility = View.VISIBLE
                    borrowListAdapter.updateBorrowLists(borrowLists)

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
}