package com.stemdigital.inventorytracker

import kotlinx.coroutines.flow.Flow

class BorrowRepository(private val borrowListDAO: BorrowListDAO) {

    fun getAllBorrowLists(): Flow<List<BorrowList>> = borrowListDAO.getAllBorrowLists()

    fun getRecentBorrowLists(): Flow<List<BorrowList>> = borrowListDAO.getRecentBorrowLists()

    suspend fun getBorrowListById(id: Int): BorrowList? = borrowListDAO.getBorrowListById(id)

    suspend fun insertBorrowList(borrowList: BorrowList): Long = borrowListDAO.insertBorrowList(borrowList)

    suspend fun updateBorrowList(borrowList: BorrowList) = borrowListDAO.updateBorrowList(borrowList)

    suspend fun deleteBorrowList(borrowList: BorrowList) = borrowListDAO.deleteBorrowList(borrowList)

    fun getBorrowListsByStatus(status: String): Flow<List<BorrowList>> = borrowListDAO.getBorrowListsByStatus(status)

    suspend fun insertBorrowListItem(borrowListItem: BorrowListItem) = borrowListDAO.insertBorrowListItem(borrowListItem)

    suspend fun getBorrowListItems(borrowListId: Int): List<BorrowListItem> = borrowListDAO.getBorrowListItems(borrowListId)

    suspend fun deleteBorrowListItems(borrowListId: Int) = borrowListDAO.deleteBorrowListItems(borrowListId)
}