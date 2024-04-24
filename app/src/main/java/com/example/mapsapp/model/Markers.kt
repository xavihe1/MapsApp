package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.android.gms.maps.model.LatLng

data class Markers(
    var userId: String?,
    var markerId: String? = null,
    var position: LatLng,
    var title: String,
    var snippet: String,
    var color: Float,
    var photo: String? = null,
){
    constructor(userId: String?, position: LatLng, title: String, snippet: String, color: Float):this(userId, null, position, title, snippet, color, null)
    constructor(): this (null, null, LatLng(0.0, 0.0), "Marcador sense nom", "Sense descripció", HUE_RED, null)
    constructor(userId: String?, latitud: Double, longitud: Double, title: String, snippet: String, color: Float, photo: String?): this(userId, null, LatLng(latitud, longitud), title, snippet, color, photo)
}
