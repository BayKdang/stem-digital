package com.stemdigital.inventorytracker

import kotlinx.coroutines.flow.Flow

class BorrowRepository(
    private val borrowListDAO: BorrowListDAO
) {

    // Insert a new borrow list
    suspend fun insertBorrowList(borrowList: BorrowList): Long {
        return borrowListDAO.insertBorrowList(borrowList)
    }

    // Insert a borrow list item
    suspend fun insertBorrowListItem(borrowListItem: BorrowListItem): Long {
        return borrowListDAO.insertBorrowListItem(borrowListItem)
    }

    // Update a borrow list
    suspend fun updateBorrowList(borrowList: BorrowList) {
        borrowListDAO.updateBorrowList(borrowList)
    }

    // Delete a borrow list
    suspend fun deleteBorrowList(borrowList: BorrowList) {
        borrowListDAO.deleteBorrowList(borrowList)
    }

    // Get all borrow lists
    fun getAllBorrowLists(): Flow<List<BorrowList>> {
        return borrowListDAO.getAllBorrowLists()
    }

    // Get recent borrow lists
    fun getRecentBorrowLists(): Flow<List<BorrowList>> {
        return borrowListDAO.getRecentBorrowLists()
    }

    // Get borrow lists by status
    fun getBorrowListsByStatus(status: String): Flow<List<BorrowList>> {
        return borrowListDAO.getBorrowListsByStatus(status)
    }

    // Get a single borrow list by ID
    suspend fun getBorrowListById(id: Int): BorrowList? {
        return borrowListDAO.getBorrowListById(id)
    }

    // Get items for a borrow list
    suspend fun getBorrowListItems(borrowListId: Int): List<BorrowListItem> {
        return borrowListDAO.getBorrowListItems(borrowListId)
    }

    // Delete a borrow list item
    suspend fun deleteBorrowListItem(borrowListItem: BorrowListItem) {
        borrowListDAO.deleteBorrowListItem(borrowListItem)
    }

    // NEW: Create borrow list with inventory update
    suspend fun createBorrowListWithInventoryUpdate(
        borrowList: BorrowList,
        items: List<BorrowListItem>,
        itemRepository: ItemRepository
    ): Long {
        // Insert the borrow list
        val borrowListId = insertBorrowList(borrowList)

        // Insert each item and update inventory
        items.forEach { borrowItem ->
            val itemWithId = borrowItem.copy(borrowListId = borrowListId. toInt())
            insertBorrowListItem(itemWithId)

            // Decrease available quantity in inventory
            itemRepository.decreaseAvailableQuantity(borrowItem.itemId, borrowItem.quantityBorrowed)
        }

        return borrowListId
    }

    // NEW: Return borrow list and update inventory
    suspend fun returnBorrowListWithInventoryUpdate(
        borrowList: BorrowList,
        itemRepository: ItemRepository
    ) {
        // Get all items in this borrow list
        val borrowedItems = getBorrowListItems(borrowList.id)

        // Update borrow list status
        val updatedBorrowList = borrowList.copy(
            status = "Returned",
            returnDate = System.currentTimeMillis()
        )
        updateBorrowList(updatedBorrowList)

        // Increase available quantity for each item
        borrowedItems.forEach { borrowItem ->
            itemRepository.increaseAvailableQuantity(borrowItem.itemId, borrowItem.quantityBorrowed)
        }
    }
}