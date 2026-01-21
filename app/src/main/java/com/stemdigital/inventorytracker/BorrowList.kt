package com.stemdigital.inventorytracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borrow_lists")
data class BorrowList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val borrowId: String,
    val borrowerName: String,
    val phoneNumber: String,
    val department: String,
    val classroomNumber: String,
    val borrowDate: Long,
    val returnDate: Long?  = null,
    val status: String = "Not Returned",
    val notes: String = ""
)