package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "borrow_list_items",
    foreignKeys = [
        ForeignKey(
            entity = BorrowList::class,
            parentColumns = ["id"],
            childColumns = ["borrowListId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey. RESTRICT
        )
    ]
)
data class BorrowListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val borrowListId: Int,
    val itemId: Int,
    val itemName: String,
    val quantityBorrowed: Int
)