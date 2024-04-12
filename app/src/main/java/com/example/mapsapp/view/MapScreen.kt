package com.example.mapsapp.view

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsapp.navigation.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class MarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String
)


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navigationController: NavHostController) {
    val markers = remember { mutableStateListOf<LatLng>() }

    val context = LocalContext.current
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        MapScreen(navigationController)
    } else {
        Text(text = "Need Permission")
    }
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState =
        rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f) }
    val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)

    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
        } else {
            Log.e("Error", "Exception: %s", task.exception)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val initialPosition = LatLng(41.4534265, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedLatLng ->
                // Abre un diálogo para que el usuario ingrese el nombre del marcador
                // y luego agrega el marcador a la lista
                val dialog = AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Nombre del marcador") },
                    text = {
                        var markerName by remember { mutableStateOf(TextFieldValue()) }
                        TextField(
                            value = markerName,
                            onValueChange = { markerName = it },
                            label = { Text("Nombre") }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                markers.add(
                                    MarkerInfo(
                                        position = clickedLatLng,
                                        title = if (markerName.text.isEmpty()) "Marcador" else markerName.text,
                                        snippet = "Marcador en latitud ${clickedLatLng.latitude}, longitud ${clickedLatLng.longitude}"
                                    )
                                )
                            }
                        ) {
                            Text("Agregar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
                showDialog(dialog)
            },
            onMapLongClick = {
                // Algo que puedes hacer cuando se hace clic largo en el mapa
            }
        ) {
            // Renderizar los marcadores en el mapa
            markers.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title,
                    snippet = marker.snippet
                )
            }
        }
    }
}