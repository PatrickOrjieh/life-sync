package com.example.aerosense_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aerosense_app.HomeData

@Dao
interface HomeDataDao {
    @Query("SELECT * FROM homeData")
    fun getAll(): List<HomeData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg homeData: HomeData)
}
