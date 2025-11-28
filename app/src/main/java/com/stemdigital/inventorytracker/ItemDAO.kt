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
}