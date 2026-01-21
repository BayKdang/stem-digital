package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val serialNumber: String,
    val quantity: Int,
    val availableQuantity: Int,
    val status: String,
    val location: String,
    val dateAdded: Long,
    val lastUpdated:  Long,
    val notes: String = "",
    val imageUri: String = "",
    val currentBorrowId: String = ""
)