package com.stemdigital.inventorytracker

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDAO: ItemDAO) {

    fun getAllItems(): Flow<List<Item>> = itemDAO.getAllItems()

    fun getItemsByCategory(category: String): Flow<List<Item>> =
        itemDAO.getItemsByCategory(category)

    suspend fun insertItem(item: Item) {
        itemDAO.insertItem(item)
    }

    suspend fun updateItem(item: Item) {
        itemDAO.updateItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDAO. deleteItem(item)
    }

    suspend fun deleteAllItems() {
        itemDAO.deleteAllItems()
    }

    fun getItemCount(): Flow<Int> = itemDAO.getItemCount()
}