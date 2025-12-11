package com.stemdigital.inventorytracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowListDAO {

    // Insert a new borrow list
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrowList(borrowList: BorrowList): Long

    // Insert a borrow list item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrowListItem(borrowListItem: BorrowListItem): Long

    // Update a borrow list
    @Update
    suspend fun updateBorrowList(borrowList: BorrowList)

    // Delete a borrow list
    @Delete
    suspend fun deleteBorrowList(borrowList: BorrowList)

    // Get all borrow lists
    @Query("SELECT * FROM borrow_lists ORDER BY borrowDate DESC")
    fun getAllBorrowLists(): Flow<List<BorrowList>>

    // Get recent borrow lists (last 5)
    @Query("SELECT * FROM borrow_lists ORDER BY borrowDate DESC LIMIT 5")
    fun getRecentBorrowLists(): Flow<List<BorrowList>>

    // Get borrow lists by status
    @Query("SELECT * FROM borrow_lists WHERE status = :status ORDER BY borrowDate DESC")
    fun getBorrowListsByStatus(status: String): Flow<List<BorrowList>>

    // Get a single borrow list by ID
    @Query("SELECT * FROM borrow_lists WHERE id = :id")
    suspend fun getBorrowListById(id: Int): BorrowList?

    // Get items for a specific borrow list
    @Query("SELECT * FROM borrow_list_items WHERE borrowListId = :borrowListId")
    suspend fun getBorrowListItems(borrowListId: Int): List<BorrowListItem>

    // Delete borrow list item
    @Delete
    suspend fun deleteBorrowListItem(borrowListItem: BorrowListItem)
}