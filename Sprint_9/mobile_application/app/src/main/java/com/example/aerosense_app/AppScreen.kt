package com.example.aerosense_app

import Login
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codingwithmitch.composegooglemaps.compose.Location
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.network.RetrofitClient
import com.example.aerosense_app.ui.AsthmaProfile
import com.example.aerosense_app.ui.Register
import com.example.aerosense_app.ui.ResetPassword
import com.example.aerosense_app.ui.Settings
import com.example.aerosense_app.ui.dataScreen

enum class Screen {
    Register,
    Login,
    ResetPassword,
    DataScreen,
    Settings,
    Location,
    AsthmaProfile,
}

        @Composable
        fun AerosenseApp(viewModel: MapViewModel){
            val repository = remember {Repository(apiService = RetrofitClient.create())}
            val firebaseModel: FirebaseViewModel = viewModel()


            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Login.name) {
                    composable("Login") { Login(navController, repository, firebaseModel) }
                    composable("Register") { Register(navController, repository, firebaseModel) }
                    composable("dataScreen") { dataScreen(navController, repository, firebaseModel) }
                    composable("Settings") { Settings(navController, repository) }
                    composable("Location") { Location(
                state = viewModel.state.value,
                setupClusterManager = viewModel::setupClusterManager,
                calculateZoneViewCenter = viewModel::calculateZoneLatLngBounds,
                navController = navController
            ) }
                    composable("AsthmaProfile"){ AsthmaProfile(navController) }
                    composable("ResetPassword") { ResetPassword(navController) }
                }
            }

        }





