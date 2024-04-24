package com.example.mapsapp.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.mapsapp.viewModel.MapsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.mapsapp.model.Markers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType


@Composable
fun MapScreen(myViewModel: MapsViewModel, navController: NavController) {
    val currentLocation: LatLng by myViewModel.localitzacioSeleccionada.observeAsState(LatLng(0.0, 0.0))
    val selectedMarker by myViewModel.markers.observeAsState(null)
    val mapType by myViewModel.tipusMapa.observeAsState(MapType.NORMAL)

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 6f)
        }
    LaunchedEffect(currentLocation) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }
    val showBottomSheet by myViewModel.showBottomSheet.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                myViewModel.cambiarLocalitzacio(it)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
            },
            onMapLongClick = {
                myViewModel.cambiarLocalitzacio(it)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
                myViewModel.modifyShowBottomSheet(true)
            },
            properties = MapProperties(
                mapType = mapType,
                isMyLocationEnabled = true,)
        ) {
            val myMarkers by myViewModel.llistaMarcadors.observeAsState()
            myViewModel.getMarkers()
            myMarkers!!.forEach {
                Marker(
                    state = MarkerState(position = it.position),
                    title = it.title,
                    snippet = it.snippet,
                    icon = BitmapDescriptorFactory.defaultMarker(it.color),
                    onInfoWindowLongClick = { marker ->
                        myViewModel.esborrarMarcadors(it)
                        if (it.photo != null && it.photo != "null") myViewModel.esborrarImatge(it.photo!!)
                    }
                )
            }
        }
    }
    if (showBottomSheet) {
        myViewModel.selectMarker(
            Markers(null, null, currentLocation,
                "", "", HUE_RED, null)
        )
        AddMarkerScreen(
            navController,
            myViewModel,
            selectedMarker!!
        )
    }
}



