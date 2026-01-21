package com.stemdigital.inventorytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BorrowedItemsAdapter(
    private var items: List<BorrowListItem>,
    private val itemMap: Map<Int, Item> = emptyMap()  // Map of itemId to Item for image loading
) : RecyclerView.Adapter<BorrowedItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        private val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        private val borrowId: TextView = itemView.findViewById(R.id.tv_borrow_id)

        fun bind(item: BorrowListItem) {
            itemName.text = item.itemName
            itemQuantity.text = "Quantity: ${item.quantityBorrowed}"
            borrowId.text = item.borrowId

            // Load item image
            val itemData = itemMap[item.itemId]
            if (itemData != null && itemData.imageUri. isNotEmpty()) {
                val bitmap = ImageUtils.loadBitmapFromPath(itemData.imageUri)
                if (bitmap != null) {
                    itemImage.setImageBitmap(bitmap)
                } else {
                    itemImage.setImageResource(R.drawable.ic_placeholder_image)
                }
            } else {
                itemImage.setImageResource(R.drawable.ic_placeholder_image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R. layout.fragment_borrowed_items_adapter, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<BorrowListItem>, newItemMap: Map<Int, Item> = emptyMap()) {
        items = newItems
        notifyDataSetChanged()
    }
}