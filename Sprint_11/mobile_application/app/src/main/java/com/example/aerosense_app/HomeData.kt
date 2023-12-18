package com.example.aerosense_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomeData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pm1: Float,
    val pm25: Float,
    val pm10: Float,
    val voc_level: Float,
    val temperature: Float,
    val humidity: Float,
    val gas_resistance: Float,
    val pollenCount: Int,
    val last_updated: String
)

//PM1 DECIMAL(10,2),
//    PM2_5 DECIMAL(10,2),
//    PM10 DECIMAL(10,2),
//    VOC DECIMAL(10,2),
//    temperature DECIMAL(5,2),
//    humidity DECIMAL(5,2),
//    gas_resistance DECIMAL(10,2),
//    pollenCount INT,