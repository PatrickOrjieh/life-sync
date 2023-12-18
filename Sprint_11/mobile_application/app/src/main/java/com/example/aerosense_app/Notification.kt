package com.example.aerosense_app

import java.time.LocalDateTime

data class Notification(
    val time: String,
    val header: String,
    val message: String
)