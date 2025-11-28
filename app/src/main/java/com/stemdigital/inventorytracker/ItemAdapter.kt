package com.stemdigital.inventorytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ItemAdapter(
    private var items: List<Item>,
    private val onEdit: (Item) -> Unit = {},
    private val onDelete: (Item) -> Unit = {}
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemCategory: TextView = itemView.findViewById(R.id.item_category)
        private val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        private val itemSerialNumber: TextView = itemView.findViewById(R.id.item_serial_number)
        private val itemStatus: TextView = itemView.findViewById(R.id.item_status)
        private val itemDescription: TextView = itemView.findViewById(R. id.item_description)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btn_delete)

        fun bind(item: Item) {
            itemName.text = item.name
            itemCategory.text = item.category
            itemQuantity.text = item.quantity.toString()
            itemSerialNumber.text = if (item.serialNumber.isEmpty()) "N/A" else item.serialNumber
            itemStatus.text = item.status
            itemDescription.text = if (item.description.isEmpty()) "No description" else item.description

            // Edit button click listener
            btnEdit.setOnClickListener {
                onEdit(item)
            }

            // Delete button click listener
            btnDelete.setOnClickListener {
                onDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}