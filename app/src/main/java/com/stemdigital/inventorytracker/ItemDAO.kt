package com.stemdigital.inventorytracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item:  Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): Item?

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE status = :status ORDER BY name ASC")
    fun getItemsByStatus(status: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR serialNumber LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchItems(query: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE quantity < 5 ORDER BY quantity ASC")
    fun getLowStockItems(): Flow<List<Item>>

    // NEW: Update available quantity when borrowing
    @Query("UPDATE items SET availableQuantity = availableQuantity - :quantity WHERE id = :itemId")
    suspend fun decreaseAvailableQuantity(itemId:  Int, quantity: Int)

    // NEW: Update available quantity when returning
    @Query("UPDATE items SET availableQuantity = availableQuantity + :quantity WHERE id = :itemId")
    suspend fun increaseAvailableQuantity(itemId: Int, quantity: Int)

    // NEW: Get items by category (for filtering Projectors and Power Strips)
    @Query("SELECT * FROM items WHERE category IN (:categories) AND availableQuantity > 0 ORDER BY name ASC")
    fun getAvailableItemsByCategories(categories: List<String>): Flow<List<Item>>
}