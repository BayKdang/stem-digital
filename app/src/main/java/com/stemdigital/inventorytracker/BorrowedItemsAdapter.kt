package com.stemdigital.inventorytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BorrowedItemsAdapter(
    private var items: List<BorrowListItem>
) : RecyclerView.Adapter<BorrowedItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName:  TextView = itemView.findViewById(R.id.tv_item_name)
        private val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)

        fun bind(item: BorrowListItem) {
            itemName.text = item.itemName
            itemQuantity.text = "Quantity: ${item.quantityBorrowed}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_borrowed_items_adapter, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder:  ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<BorrowListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}