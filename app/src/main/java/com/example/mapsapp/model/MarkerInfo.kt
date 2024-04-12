package com.example.mapsapp.model

import com.google.android.gms.maps.model.LatLng

data class MarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String
)
