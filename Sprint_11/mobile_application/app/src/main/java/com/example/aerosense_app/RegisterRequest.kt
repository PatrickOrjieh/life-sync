package com.example.aerosense_app
data class RegisterRequest(
        val firebaseToken: String,
        val modelNumber: String,
        val fcmToken: String
)
