package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mapsapp.viewModel.MapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.livedata.observeAsState


data class MarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String
)

val REQUEST_LOCATION_PERMISSION = 123

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MapScreen(navigationController: NavHostController, myViewModel: MapsViewModel) {
    val context = LocalContext.current
    val fusedLocationProviderClient: FusedLocationProviderClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState =
        rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f) }
    val bottomSheetVisible by myViewModel.bottomSheetVisible.observeAsState(false)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            searchLocationPermissionGranted(fusedLocationProviderClient, deviceLatLng, cameraPositionState, context)
        } else {
            Log.d("Permission", "Permission denied")
        }
    }
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                searchLocationPermissionGranted(fusedLocationProviderClient, deviceLatLng, cameraPositionState, context)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    MyDrawer(myViewModel = myViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ContingutGoogleMaps(
                deviceLocation = deviceLatLng,
                cameraPositionState = cameraPositionState,
                myViewModel = myViewModel,
                bottomSheetVisible = bottomSheetVisible,
                navigationController = navigationController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContingutGoogleMaps(
    deviceLocation: MutableState<LatLng>,
    cameraPositionState: CameraPositionState,
    myViewModel: MapsViewModel,
    bottomSheetVisible: Boolean,
    navigationController: NavHostController
) {
    val itb = LatLng(41.4534265, 2.1837151)

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            myViewModel.setBottomSheetVisible(true)
        }
    ) {
        Marker(
            state = MarkerState(position = itb),
            title = "ITB",
            snippet = "Marker at ITB"
        )
    }

    if (bottomSheetVisible) {
        MyBottomSheet(
            cameraPositionState = cameraPositionState,
            myViewModel = myViewModel
        ) { myViewModel.setBottomSheetVisible(false) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun MyBottomSheet(
    cameraPositionState: CameraPositionState,
    myViewModel: MapsViewModel,
    onClose: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(false)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("NAME", style = TextStyle(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("DESCRIPTION", style = TextStyle(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val currentPosition = cameraPositionState.position.target
                myViewModel.addMarker(
                    position = LatLng(currentPosition.latitude, currentPosition.longitude),
                    name = name.value,
                    description = description.value
                )
                onClose()
            }) {
                Text("ADD MARKER")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Handle navigate to camera screen */ },
            ) {
                Text("CAMERA")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClose) {
                Text("CLOSE")
            }
        }
    }
}
private fun searchLocationPermissionGranted(
    fusedLocationProviderClient: FusedLocationProviderClient,
    deviceLocation: MutableState<LatLng>,
    cameraPositionState: CameraPositionState,
    context: Context
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                deviceLocation.value = LatLng(location.latitude, location.longitude)
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(deviceLocation.value, 18f)
            } else {
                Log.e("Location", "Last location is null")
            }
        }
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }
}

















/*
@Composable
fun MapScreen(navigationController: NavHostController) {
    // Estado para almacenar la lista de marcadores
    val markers = remember { mutableStateListOf<MarkerInfo>() }

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
*/