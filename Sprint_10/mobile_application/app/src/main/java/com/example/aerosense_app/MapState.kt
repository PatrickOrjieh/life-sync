package com.example.aerosense_app

import android.location.Location
import com.codingwithmitch.composegooglemaps.clusters.ZoneClusterItem
//Parts of code taken from: https://github.com/mitchtabian/Google-Maps-Compose
data class MapState(
    val lastKnownLocation: Location?,
    val clusterItems: List<ZoneClusterItem>,
)
