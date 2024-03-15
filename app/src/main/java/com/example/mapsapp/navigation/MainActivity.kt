package com.example.mapsapp.navigation

import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.AddMarkerScreen
import com.example.mapsapp.view.LaunchScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListScreen
import com.example.mapsapp.view.MenuScreen
//import com.example.mapsapp.view.MenuScreen
import com.example.mapsapp.viewModel.MapsViewModel
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myViewModel by viewModels<MapsViewModel>()
        setContent {
            MapsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //MenuScreen(myViewModel = MapsViewModel())

                    val navigationController = rememberNavController()
                    val mapsViewModel by viewModels<MapsViewModel>()
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.Pantalla1.route
                    ) {
                        composable(Routes.Pantalla1.route) { LaunchScreen(navigationController) }
                        composable(Routes.Pantalla2.route) { LoginScreen(navigationController) }
                        composable(Routes.Pantalla3.route) { MenuScreen(myViewModel, navigationController) }
                        composable(Routes.Pantalla4.route) { MapScreen(navigationController) }
                        composable(Routes.Pantalla5.route) { AddMarkerScreen(navigationController) }
                        composable(Routes.Pantalla6.route) { MarkerListScreen(navigationController) }
                    }
                }
            }
        }
    }
}


/*

val permissionState = 
    rememberPermissionState(permission = Manifest.permission.ACCES_FINE_LOCATION)
LaunchedEffect(Unit) {
    permissionState.launchPermissionRequest()
}
if (permissionState.status.isGranted) {
    ShowMap(myViewModel)
} else {
    Text(text = "Need permission")
}



val context = LocalContext.current
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



val permissionState =
    rememberPermissionState(permission = Manifest.permission.CAMERA)
LaunchedEffect(Unit) {
    permissionState.launchPermissionRequest()
}
if (permissionState.status.isGranted) {
    CameraScreen()
} else {
    Text("Need permission")
}



val context = LocalContext.current
val controller = remember {
    LifecycleCameraController(context).apply {
        CameraController.IMAGE_CAPTURE
    }
}



Box(modifier = Modifier.fillMaxSize()) {
    CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
    IconButton(
        onClick = {
            controller.cameraSelector =
                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
        },
        modifier = Modifier.offset(16.dp, 16.dp)
    ) {
        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch Camera")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = {
            takePhoto(context, controller) { photo ->
                //Do something with the photo
            }
        }) {
            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take Photo")
        }
    }
}
 */