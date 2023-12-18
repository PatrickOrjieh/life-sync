package com.example.aerosense_app

import Login
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codingwithmitch.composegooglemaps.compose.Location
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

//private val viewModel: MapViewModel by viewModels()

        @Composable
        fun AerosenseApp(viewModel: MapViewModel){

            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Login.name) {
                    composable("Login") { Login(navController) }
                    composable("Register") { Register(navController) }
                    composable("dataScreen") { dataScreen(navController) }
                    composable("Settings") { Settings(navController) }
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





