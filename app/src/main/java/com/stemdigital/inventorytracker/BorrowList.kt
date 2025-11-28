package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borrow_lists")
data class BorrowList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val borrowerName: String,
    val phoneNumber: String,
    val department: String,
    val classroomNumber: String,
    val borrowDate: Long = System.currentTimeMillis(),
    val returnDate: Long?  = null,
    val status: String = "Pending", // Pending, Returned, Overdue
    val notes: String = ""
)