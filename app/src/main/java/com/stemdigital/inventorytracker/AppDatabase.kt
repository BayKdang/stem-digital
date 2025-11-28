package com.stemdigital.inventorytracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Item::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDAO(): ItemDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase?  = null

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class. java,
                    "inventory_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}