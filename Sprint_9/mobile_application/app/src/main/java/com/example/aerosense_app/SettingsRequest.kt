package com.example.aerosense_app

data class SettingsRequest (

    val notificationFrequency: String,
    val vibration: Boolean,
    val sound: Boolean
)


