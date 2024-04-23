package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.mapsapp.navigation.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.launch


data class MarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String
)

val REQUEST_LOCATION_PERMISSION = 123

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun MapScreen(navController, myViewModel: MapsViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet by myViewModel.showBottomSheet.observeAsState(initial = false)
    val marcadores by myViewModel.markers.observeAsState(emptyList())
    val texto: String by myViewModel.dropDownText.observeAsState("Mostrar Todos")
    val isLoading: Boolean by myViewModel.loadingMarkers.observeAsState(initial = false)
    myViewModel.getMarkers()

    if (!myViewModel.userLogged()) {
        myViewModel.logout(context = LocalContext.current, navController)
    }

    if (!isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colors.secondary
            )
        }
    } else {
        MenuScreen(
            navController = navController,
            myViewModel = myViewModel,
            content = {
                val permissionState =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                LaunchedEffect(Unit) {
                    permissionState.launchPermissionRequest()
                }

                if (permissionState.status.isGranted) {
                    val context = LocalContext.current
                    val fusedLocationProviderClient =
                        remember { LocationServices.getFusedLocationProviderClient(context) }
                    var lastKnownLocation by remember { mutableStateOf<ExerciseRoute.Location?>(null) }
                    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
                    val cameraPositionState =
                        rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                        }

                    val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
                    locationResult.addOnCompleteListener(context as MainActivity) { task ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            deviceLatLng =
                                LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                            myViewModel.changePosition(deviceLatLng)
                            myViewModel.modificarEditingPosition(deviceLatLng)
                        } else {
                            Log.e("Error", "Exception: %s", task.exception)
                        }
                    }
                    Box {
                        Column {
                            val categories: List<Categoria> by myViewModel.categories.observeAsState(
                                emptyList()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = texto,
                                    onValueChange = { /* No permitimos cambios directos aquí */ },
                                    enabled = false,
                                    readOnly = true,
                                    modifier = Modifier
                                        .clickable { myViewModel.modifyExpandedMapa(true) }
                                        .fillMaxWidth()
                                )

                                DropdownMenu(
                                    expanded = myViewModel.getExpandedMapa(),
                                    onDismissRequest = { myViewModel.modifyExpandedMapa(false) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Opción para mostrar todos los marcadores
                                    DropdownMenuItem(
                                        text = { Text(text = "Mostrar Todos") },
                                        onClick = {
                                            myViewModel.modifyExpandedMapa(false)
                                            myViewModel.getAllMarkers()
                                            myViewModel.modifyDropDownText("Mostrar Todos")
                                        })

                                    // Opciones para las categorías
                                    categories.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(text = categoria.name) },
                                            onClick = {
                                                myViewModel.modifyExpandedMapa(false)
                                                myViewModel.modifyDropDownText(categoria.name)
                                            })
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.Start
                            ) {
                                GoogleMap(
                                    modifier = Modifier.fillMaxHeight(),
                                    cameraPositionState = cameraPositionState,
                                    onMapLongClick = {
                                        myViewModel.changePosition(it)
                                        myViewModel.modificarEditingPosition(it)
                                        myViewModel.modifyShowBottomSheet(true)
                                    },
                                    properties = MapProperties(
                                        isMyLocationEnabled = true,
                                        isIndoorEnabled = true,
                                        isBuildingEnabled = true,
                                        isTrafficEnabled = true
                                    )
                                )
                                {
                                    if (showBottomSheet) {
                                        ModalBottomSheet(
                                            // tonalElevation =  BottomSheetDefaults.SheetPeekHeight,
                                            // modifier = Modifier.fillMaxSize(),
                                            onDismissRequest = {
                                                myViewModel.modifyShowBottomSheet(false)
                                            },
                                            sheetState = sheetState
                                        ) {
                                            AddMarkerScreen(
                                                MapsViewModel = myViewModel,
                                                navController,
                                                onCloseBottomSheet = {
                                                    resetearParametros(myViewModel)
                                                    scope.launch { sheetState.hide() }
                                                        .invokeOnCompletion {
                                                            if (!sheetState.isVisible) {
                                                                myViewModel.modifyShowBottomSheet(
                                                                    false
                                                                )
                                                            }
                                                        }
                                                },true
                                            )
                                        }
                                    }

                                    marcadores.forEach { marker ->
                                        Marker(
                                            state = MarkerState(
                                                LatLng(
                                                    marker.latitude,
                                                    marker.longitude
                                                )
                                            ), // no los muestra en la posicion D:
                                            title = marker.title,
                                            snippet = marker.snippet,
                                            icon = BitmapDescriptorFactory.defaultMarker(
                                                when (marker.category.name) {
                                                    "Favoritos" -> BitmapDescriptorFactory.HUE_CYAN
                                                    "Likes" -> BitmapDescriptorFactory.HUE_YELLOW
                                                    "Info" -> BitmapDescriptorFactory.HUE_GREEN
                                                    else -> BitmapDescriptorFactory.HUE_RED
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                myViewModel.modifyShowBottomSheet(true)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 33.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Add, contentDescription = null) // Icono
                            }
                        }
                    }
                } else {
                    PermissionDeclinedScreen()
                }
            })
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