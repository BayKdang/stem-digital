package com.stemdigital.inventorytracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Item::class, BorrowList::class, BorrowListItem::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDAO(): ItemDAO
    abstract fun borrowListDAO(): BorrowListDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add serialNumber column
                database.execSQL(
                    "ALTER TABLE items ADD COLUMN serialNumber TEXT NOT NULL DEFAULT ''"
                )
                // Add status column
                database.execSQL(
                    "ALTER TABLE items ADD COLUMN status TEXT NOT NULL DEFAULT 'Available'"
                )
            }
        }

        // Migration from version 2 to version 3
        // Migration from version 2 to version 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create borrow_lists table with new schema
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS borrow_lists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                borrowerName TEXT NOT NULL,
                phoneNumber TEXT NOT NULL,
                department TEXT NOT NULL,
                classroomNumber TEXT NOT NULL,
                borrowDate INTEGER NOT NULL,
                returnDate INTEGER,
                status TEXT NOT NULL DEFAULT 'Pending',
                notes TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
                )

                // Create borrow_list_items table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS borrow_list_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                borrowListId INTEGER NOT NULL,
                itemId INTEGER NOT NULL,
                itemName TEXT NOT NULL,
                quantityBorrowed INTEGER NOT NULL,
                FOREIGN KEY(borrowListId) REFERENCES borrow_lists(id) ON DELETE CASCADE,
                FOREIGN KEY(itemId) REFERENCES items(id) ON DELETE RESTRICT
            )
            """.trimIndent()
                )

                // Create index for borrowListId
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS idx_borrow_list_items_borrowListId ON borrow_list_items(borrowListId)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class. java,
                    "inventory_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}