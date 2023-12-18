package com.example.aerosense_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aerosense_app.HomeData

@Database(entities = [HomeData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeDataDao(): HomeDataDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE HomeData ADD COLUMN pm1 REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE HomeData ADD COLUMN gas_resistance REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE HomeData ADD COLUMN pollenCount INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
