package com.stemdigital.inventorytracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines. flow.Flow

@Dao
interface BorrowListDAO {

    // Insert borrow list
    @Insert
    suspend fun insertBorrowList(borrowList: BorrowList): Long

    // Get all borrow lists
    @Query("SELECT * FROM borrow_lists ORDER BY borrowDate DESC")
    fun getAllBorrowLists(): Flow<List<BorrowList>>

    // Get recent borrow lists (last 5)
    @Query("SELECT * FROM borrow_lists ORDER BY borrowDate DESC LIMIT 5")
    fun getRecentBorrowLists(): Flow<List<BorrowList>>

    // Get borrow list by id
    @Query("SELECT * FROM borrow_lists WHERE id = :id")
    suspend fun getBorrowListById(id: Int): BorrowList?

    // Update borrow list
    @Update
    suspend fun updateBorrowList(borrowList: BorrowList)

    // Delete borrow list
    @Delete
    suspend fun deleteBorrowList(borrowList: BorrowList)

    // Get borrow lists by status
    @Query("SELECT * FROM borrow_lists WHERE status = :status ORDER BY borrowDate DESC")
    fun getBorrowListsByStatus(status: String): Flow<List<BorrowList>>

    // Insert borrow list item
    @Insert
    suspend fun insertBorrowListItem(borrowListItem: BorrowListItem)

    // Get items in a borrow list
    @Query("SELECT * FROM borrow_list_items WHERE borrowListId = :borrowListId")
    suspend fun getBorrowListItems(borrowListId: Int): List<BorrowListItem>

    // Delete borrow list items
    @Query("DELETE FROM borrow_list_items WHERE borrowListId = :borrowListId")
    suspend fun deleteBorrowListItems(borrowListId: Int)
}