package com.example.aerosense_app
data class HomeData(
    val pm25: Float,
    val pm10: Float,
    val voc_level: Float,
    val temperature: Float,
    val humidity: Float,
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