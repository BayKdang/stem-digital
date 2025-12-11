package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val serialNumber: String,  // Empty string for batch items like cables
    val quantity: Int,
    val availableQuantity: Int,  // NEW: Track available (not borrowed) quantity
    val status: String,  // "Available", "Borrowed", "Maintenance", "Damaged"
    val location: String,
    val dateAdded: Long,
    val lastUpdated: Long,
    val notes: String = ""
)