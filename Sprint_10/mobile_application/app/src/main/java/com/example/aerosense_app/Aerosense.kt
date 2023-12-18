package com.example.aerosense_app

import android.app.Application
import androidx.room.Room
import com.example.aerosense_app.database.AppDatabase
import com.example.aerosense_app.database.AppDatabase.Companion.MIGRATION_1_2

class Aerosense : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "aerosense-db"
        )
            .addMigrations(MIGRATION_1_2) // Only needed if you provide a migration.
            .fallbackToDestructiveMigration() // This line tells Room to recreate the database if the versions do not match.
            .build()
    }
}
