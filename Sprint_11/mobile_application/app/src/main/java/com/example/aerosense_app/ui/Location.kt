package com.codingwithmitch.composegooglemaps.compose

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codingwithmitch.composegooglemaps.clusters.ZoneClusterManager
import com.example.aerosense_app.FirebaseViewModel
import com.example.aerosense_app.MapState
import com.example.aerosense_app.api.Repository
import com.example.aerosense_app.ui.components.NavBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.addMarker
import kotlinx.coroutines.launch

//Parts of code taken from: https://github.com/mitchtabian/Google-Maps-Compose

@Composable
fun Location(
    state: MapState,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    calculateZoneViewCenter: () -> LatLngBounds,
    navController: NavHostController,
    firebaseModel: FirebaseViewModel,
    repository: Repository
) {

    NavBar(navController)

    val locationList = remember { mutableStateListOf<LatLng>() }

    // Obtain the token from the ViewModel
    val token = firebaseModel.firebaseToken
    Log.d("Token", "Token: $token")

// Check if the token is not null
    if (!token.isNullOrBlank()) {
        // Use the token to make the API call
        repository.fetchLocationData(token,
            onSuccess = { locationData ->
                if (locationData != null) {
                    // Assuming LocationData has 'latitude' and 'longitude' properties
                    locationList.clear()
                    locationList.addAll(locationData.map { LatLng(it.latitude, it.longitude) })

                    Log.d("Check location data", "Location data: $locationData")
                }
                Log.d("Check Location Data Outside", "Location data: $locationData")
            },
            onError = { errorMessage ->
                Log.d("Check Location Data", "Error: $errorMessage")
            }
        )
    } else {
        Log.d("LocationError", "Error: Firebase token is null or blank")
    }

    // Set properties using MapProperties which you can use to recompose the map
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = state.lastKnownLocation != null,
    )
    val cameraPositionState = rememberCameraPositionState()
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 65.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            MapEffect(state.clusterItems) { map ->
                if (state.clusterItems.isNotEmpty()) {
                    val clusterManager = setupClusterManager(context, map)
//                    map.setOnCameraIdleListener(clusterManager)
//                    map.setOnMarkerClickListener(clusterManager)
//                    state.clusterItems.forEach { clusterItem ->
//                        map.addPolygon(clusterItem.polygonOptions)
//                    }

                    try {
                        if (locationList.isNotEmpty()) {
                            Log.d("LocationList", "LocationList: $locationList")
                            for (location in locationList) {
                                map.addMarker {
                                    position(location)
                                    title("Bad Air Quality")
                                    snippet("Bad air quality detected here")
                                }
                            }
                        } else {
                            Log.d("LocationList", "LocationList is empty")
                            // You can take additional actions here based on your requirements
                        }
                    } catch (e: Exception) {
                        Log.e("LocationList", "Error adding markers: ${e.message}")
                        // Handle the exception, log the details, and take appropriate action
                    }

                    map.setOnMapLoadedCallback {
                        if (state.clusterItems.isNotEmpty()) {
                            scope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngBounds(
                                        calculateZoneViewCenter(),
                                        0
                                    ),
                                )
                            }
                        }
                    }
                }
            }

            // NOTE: Some features of the MarkerInfoWindow don't work currently. See docs:
            // https://github.com/googlemaps/android-maps-compose#obtaining-access-to-the-raw-googlemap-experimental
            // So you can use clusters as an alternative to markers.
            MarkerInfoWindow(
                state = rememberMarkerState(position = LatLng(49.1, -122.5)),
                snippet = "Some stuff",
                onClick = {
                    // This won't work :(
                    System.out.println("Mitchs_: Cannot be clicked")
                    true
                },
                draggable = true
            )

        }
    }
    // Center camera to include all the Zones.
    LaunchedEffect(state.clusterItems) {
        if (state.clusterItems.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    calculateZoneViewCenter(),
                    0
                ),
            )
        }
    }
}

/**
 * If you want to center on a specific location.
 */
private suspend fun CameraPositionState.centerOnLocation(
    location: Location
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        LatLng(location.latitude, location.longitude),
        15f
    ),
)