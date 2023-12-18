package com.example.aerosense_app

data class SettingsRequest(

    var notificationFrequency: String,
    var vibration: Boolean,
    var sound: Boolean
)


