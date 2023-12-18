package com.example.aerosense_app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.codingwithmitch.composegooglemaps.clusters.ZoneClusterItem
import com.codingwithmitch.composegooglemaps.clusters.ZoneClusterManager
import com.codingwithmitch.composegooglemaps.clusters.calculateCameraViewPoints
import com.codingwithmitch.composegooglemaps.clusters.getCenterOfPolygon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.model.polygonOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
//Parts of code taken from: https://github.com/mitchtabian/Google-Maps-Compose
@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
            clusterItems = listOf(
                ZoneClusterItem(
                    id = "zone-1",
                    title = "Zone 1",
                    snippet = "This is Zone 1.",
                    polygonOptions = polygonOptions {
                        add(LatLng(53.98267190694426, -6.392890277320703))
                        add(LatLng(53.98206474338727, -6.392890277320703))
                        add(LatLng(53.98206474338727, -6.3902555429016275))
                        add(LatLng(53.98267190694426, -6.3902555429016275))
                        fillColor(POLYGON_FILL_COLOR)
                    }
                ),
                ZoneClusterItem(
                    id = "zone-2",
                    title = "Zone 2",
                    snippet = "This is Zone 2.",
                    polygonOptions = polygonOptions {
                        add(LatLng(54.001225765645756, -6.412810475185324))
                        add(LatLng(54.00092957923849, -6.412810475185324))
                        add(LatLng(54.00092957923849, -6.412453858091681))
                        add(LatLng(54.001225765645756, -6.412453858091681))
                        fillColor(POLYGON_FILL_COLOR)
                    }
                )
            )
        )
    )

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
        }
    }

    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }

    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        val latLngs = state.value.clusterItems.map { it.polygonOptions }
                .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
       return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
    }



    companion object {
        private val POLYGON_FILL_COLOR = Color.parseColor("#ABF44336")
    }
}