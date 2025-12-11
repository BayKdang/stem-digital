package com.stemdigital.inventorytracker

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDAO: ItemDAO) {

    fun getAllItems(): Flow<List<Item>> {
        return itemDAO.getAllItems()
    }

    suspend fun getItemById(id: Int): Item? {
        return itemDAO.getItemById(id)
    }

    fun getItemsByCategory(category: String): Flow<List<Item>> {
        return itemDAO.getItemsByCategory(category)
    }

    fun getItemsByStatus(status: String): Flow<List<Item>> {
        return itemDAO.getItemsByStatus(status)
    }

    fun searchItems(query: String): Flow<List<Item>> {
        return itemDAO.searchItems(query)
    }

    fun getLowStockItems(): Flow<List<Item>> {
        return itemDAO.getLowStockItems()
    }

    suspend fun insertItem(item: Item): Long {
        return itemDAO.insertItem(item)
    }

    suspend fun updateItem(item: Item) {
        itemDAO.updateItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDAO.deleteItem(item)
    }

    // NEW:  Decrease available quantity when borrowing
    suspend fun decreaseAvailableQuantity(itemId: Int, quantity: Int) {
        itemDAO.decreaseAvailableQuantity(itemId, quantity)
    }

    // NEW: Increase available quantity when returning
    suspend fun increaseAvailableQuantity(itemId: Int, quantity: Int) {
        itemDAO.increaseAvailableQuantity(itemId, quantity)
    }

    // NEW: Get available items by categories
    fun getAvailableItemsByCategories(categories: List<String>): Flow<List<Item>> {
        return itemDAO.getAvailableItemsByCategories(categories)
    }
}