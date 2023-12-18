package com.example.aerosense_app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class FirebaseViewModel : ViewModel() {
    var firebaseToken: String? by mutableStateOf(null)


}
