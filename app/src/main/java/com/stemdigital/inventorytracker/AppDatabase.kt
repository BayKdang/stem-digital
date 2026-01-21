package com.stemdigital.inventorytracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Item::class, BorrowList::class, BorrowListItem::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDAO(): ItemDAO
    abstract fun borrowListDAO(): BorrowListDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE items ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
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

                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS idx_borrow_list_items_borrowListId ON borrow_list_items(borrowListId)"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE items ADD COLUMN availableQuantity INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE items SET availableQuantity = quantity")

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS borrow_lists_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        borrowerName TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        department TEXT NOT NULL,
                        classroomNumber TEXT NOT NULL,
                        borrowDate INTEGER NOT NULL,
                        returnDate INTEGER,
                        status TEXT NOT NULL DEFAULT 'Not Returned',
                        notes TEXT NOT NULL DEFAULT ''
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO borrow_lists_new (id, borrowerName, phoneNumber, department, classroomNumber, borrowDate, returnDate, status, notes)
                    SELECT id, borrowerName, phoneNumber, department, classroomNumber, borrowDate, returnDate, 
                           CASE WHEN status = 'Pending' THEN 'Not Returned' ELSE status END, notes
                    FROM borrow_lists
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE borrow_lists")
                database.execSQL("ALTER TABLE borrow_lists_new RENAME TO borrow_lists")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE items ADD COLUMN imageUri TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE items ADD COLUMN currentBorrowId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE borrow_lists ADD COLUMN borrowId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE borrow_list_items ADD COLUMN borrowId TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}