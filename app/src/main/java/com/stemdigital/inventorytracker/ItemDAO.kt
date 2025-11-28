package com.stemdigital.inventorytracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {

    // Insert a new item
    @Insert
    suspend fun insertItem(item: Item)

    // Get all items
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<Item>>

    // Get items by category
    @Query("SELECT * FROM items WHERE category = :category ORDER BY createdAt DESC")
    fun getItemsByCategory(category: String): Flow<List<Item>>

    // Update an item
    @Update
    suspend fun updateItem(item: Item)

    // Delete an item
    @Delete
    suspend fun deleteItem(item: Item)

    // Delete all items
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

    // Get item count
    @Query("SELECT COUNT(*) FROM items")
    fun getItemCount(): Flow<Int>

    // Get item by id
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): Item?

    // Get items by status
    @Query("SELECT * FROM items WHERE status = :status ORDER BY createdAt DESC")
    fun getItemsByStatus(status: String): Flow<List<Item>>

    // Search items by serial number
    @Query("SELECT * FROM items WHERE serialNumber LIKE :serialNumber")
    fun searchBySerialNumber(serialNumber: String): Flow<List<Item>>
}