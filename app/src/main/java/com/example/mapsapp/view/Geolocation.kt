package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import com.example.mapsapp.navigation.MainActivity
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewModel.MapsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@Composable
fun Geolocation(navController: NavController, myViewModel: MapsViewModel) {
    val context = LocalContext.current
    val isLocationPermissionGranted by myViewModel.permissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.showPermissionRationale.observeAsState(false)
    val permissionDenied by myViewModel.permissionDenied.observeAsState(false)

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                myViewModel.setPermissionGranted(true)
            } else {
                myViewModel.setPermissionRationale(
                    shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                if (!shouldShowPermissionRationale) {
                    Log.i("MapScreen", "Cannot ask permission.")
                    myViewModel.setPermissionDenied(true)
                }
            }
        }
    )
    Log.i("Boolean", "$isLocationPermissionGranted")
    if (!isLocationPermissionGranted){
        SideEffect {
            locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    } else {
        var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        val fusedLocationProviderClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }
        var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
        val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
        locationResult.addOnCompleteListener(context as MainActivity) { task ->
            if (task.isSuccessful) {
                lastKnownLocation = task.result
                deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                myViewModel.cambiarLocalitzacio(deviceLatLng)
            } else {
                Log.e("Error", "Exception %s", task.exception)
            }
        }
        navController.navigate(Routes.Pantalla4.route)
    }
    if (permissionDenied)
        PermissionDeclinedScreen()
}