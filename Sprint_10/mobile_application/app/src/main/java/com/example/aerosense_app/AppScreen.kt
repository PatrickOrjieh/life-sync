package com.example.aerosense_app

import Login
import SplashScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codingwithmitch.composegooglemaps.compose.Location
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.network.RetrofitClient
import com.example.aerosense_app.ui.AsthmaProfile
import com.example.aerosense_app.ui.EducationPage
import com.example.aerosense_app.ui.History
import com.example.aerosense_app.ui.Register
import com.example.aerosense_app.ui.ResetPassword
import com.example.aerosense_app.ui.Settings
import com.example.aerosense_app.ui.dataScreen
import com.example.aerosense_app.ui.EducationPage

enum class Screen {
                  SplashScreen,
    Register,
    Login,
    ResetPassword,
    DataScreen,
    Settings,
    Location,
    AsthmaProfile,
    History,
    EducationPage
}

        @SuppressLint("ComposableDestinationInComposeScope")
        @Composable
        fun AerosenseApp(viewModel: MapViewModel){
            val appContext = LocalContext.current.applicationContext as Aerosense
            val homeDataDao = appContext.db.homeDataDao()

            val repository = remember {
                Repository(apiService = RetrofitClient.create(), homeDataDao = homeDataDao)
            }
            val firebaseModel: FirebaseViewModel = viewModel()


            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.SplashScreen.name) {
                    composable("SplashScreen") { SplashScreen(navController) }
                    composable("Login") { Login(navController, repository, firebaseModel) }
                    composable("Register") { Register(navController, repository, firebaseModel) }
                    composable("dataScreen") { dataScreen(navController, repository, firebaseModel) }
                    composable("Settings") { Settings(navController, repository) }
                    composable("EducationPage") { EducationPage(navController) }
                    composable("Location") { Location(
                state = viewModel.state.value,
                setupClusterManager = viewModel::setupClusterManager,
                calculateZoneViewCenter = viewModel::calculateZoneLatLngBounds,
                navController = navController
            ) }
                    composable("AsthmaProfile"){ AsthmaProfile(navController) }
                    composable("ResetPassword") { ResetPassword(navController) }
                    composable("History") { History(navController) }
            }

        }
    }





