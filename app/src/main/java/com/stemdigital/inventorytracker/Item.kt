package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val category: String,
    val description: String = "",
    val serialNumber: String = "",
    val status: String = "Available",
    val createdAt: Long = System.currentTimeMillis()
)