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
import com.example.mapsapp.view.GalleryScreen
import com.example.mapsapp.view.LaunchScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListScreen
import com.example.mapsapp.view.MenuScreen
import com.example.mapsapp.view.TakePhotoScreen
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
                        composable(Routes.Pantalla7.route) { TakePhotoScreen(navigationController, myViewModel) }
                        composable(Routes.Pantalla8.route) { GalleryScreen(navigationController, myViewModel) }
                    }
                }
            }
        }
    }
}