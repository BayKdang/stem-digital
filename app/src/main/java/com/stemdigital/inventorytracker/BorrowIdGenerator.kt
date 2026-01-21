package com.stemdigital.inventorytracker

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BorrowIdGenerator {
    /**
     * Generate unique Borrow ID in format:  B-YYYY-XXXX
     * Example: B-2026-0001
     */
    fun generateUniqueBorrowId(): String {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()
        val lastFourDigits = (timestamp % 10000).toInt()
        val formattedSequence = String.format("%04d", lastFourDigits)
        return "B-$currentYear-$formattedSequence"
    }
}