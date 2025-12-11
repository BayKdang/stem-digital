package com.stemdigital.inventorytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BorrowListAdapter(
    private var borrowLists: List<BorrowList>,
    private var borrowListItems: Map<Int, List<BorrowListItem>> = emptyMap(),
    private val onViewDetails: (BorrowList) -> Unit = {},
    private val onMarkReturned: (BorrowList) -> Unit = {},
    private val onDelete: (BorrowList) -> Unit = {}
) : RecyclerView.Adapter<BorrowListAdapter.BorrowListViewHolder>() {

    inner class BorrowListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val borrowerName: TextView = itemView.findViewById(R.id.borrower_name)
        private val borrowerPhone: TextView = itemView. findViewById(R.id.borrower_phone)
        private val borrowerDepartment: TextView = itemView.findViewById(R.id.borrower_department)
        private val borrowerClassroom: TextView = itemView.findViewById(R.id.borrower_classroom)
        private val borrowStatus: TextView = itemView.findViewById(R.id.borrow_status)
        private val borrowDate: TextView = itemView.findViewById(R.id.borrow_date)
        private val borrowedItems: TextView = itemView.findViewById(R.id.borrowed_items)
        private val btnViewDetails: MaterialButton = itemView. findViewById(R.id.btn_view_details)
        private val btnMarkReturned: MaterialButton = itemView.findViewById(R.id.btn_mark_returned)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btn_delete_borrow)

        fun bind(borrowList: BorrowList) {
            borrowerName.text = borrowList.borrowerName
            borrowerPhone.text = borrowList.phoneNumber
            borrowerDepartment.text = borrowList.department
            borrowerClassroom.text = borrowList.classroomNumber
            borrowStatus. text = borrowList.status
            borrowDate.text = formatDate(borrowList.borrowDate)

            // Get items for this borrow list
            val items = borrowListItems[borrowList. id] ?: emptyList()
            borrowedItems.text = if (items.isEmpty()) {
                "No items"
            } else {
                items.joinToString(", ") { "${it.itemName} (Qty: ${it.quantityBorrowed})" }
            }

            // Handle button visibility based on status
            if (borrowList.status == "Returned") {
                btnMarkReturned.isEnabled = false
                btnMarkReturned.alpha = 0.5f
                btnMarkReturned.text = "Returned"
            } else {
                btnMarkReturned.isEnabled = true
                btnMarkReturned.alpha = 1f
                btnMarkReturned.text = "Mark Returned"
            }

            // View Details button click listener
            btnViewDetails.setOnClickListener {
                onViewDetails(borrowList)
            }

            // Mark Returned button click listener
            btnMarkReturned.setOnClickListener {
                onMarkReturned(borrowList)
            }

            // Delete button click listener
            btnDelete.setOnClickListener {
                onDelete(borrowList)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale. getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_borrow_list, parent, false)
        return BorrowListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorrowListViewHolder, position: Int) {
        holder.bind(borrowLists[position])
    }

    override fun getItemCount(): Int = borrowLists.size

    fun updateBorrowLists(newBorrowLists: List<BorrowList>) {
        borrowLists = newBorrowLists
        notifyDataSetChanged()
    }

    fun updateBorrowListItems(newItems: Map<Int, List<BorrowListItem>>) {
        borrowListItems = newItems
        notifyDataSetChanged()
    }
}