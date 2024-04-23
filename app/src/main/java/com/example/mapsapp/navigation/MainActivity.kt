package com.example.mapsapp.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.AddMarkerScreen
import com.example.mapsapp.view.CameraScreen
import com.example.mapsapp.view.GalleryScreen
import com.example.mapsapp.view.LaunchScreen
import com.example.mapsapp.view.LoginScreen
import com.example.mapsapp.view.MapScreen
import com.example.mapsapp.view.MarkerListScreen
import com.example.mapsapp.view.MenuScreen
import com.example.mapsapp.view.RegisterScreen
import com.example.mapsapp.view.TakePhotoScreen
import com.example.mapsapp.viewModel.MapsViewModel

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
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.Pantalla1.route
                    ) {
                        composable(Routes.Pantalla1.route) { LaunchScreen(navigationController) }
                        composable(Routes.Pantalla2.route) { LoginScreen(navigationController) }
                        composable(Routes.Pantalla3.route) { MenuScreen(myViewModel, navigationController) }
                        composable(Routes.Pantalla4.route) { MapScreen(navigationController, myViewModel) }
                        composable(Routes.Pantalla5.route) { AddMarkerScreen(navigationController) }
                        composable(Routes.Pantalla6.route) { MarkerListScreen(navigationController) }
                        composable(Routes.Pantalla7.route) { TakePhotoScreen(navigationController, myViewModel) }
                        composable(Routes.Pantalla8.route) { GalleryScreen(navigationController, myViewModel) }
                        composable(Routes.Pantalla9.route) { CameraScreen(navigationController, myViewModel) }
                        composable(Routes.Pantalla10.route) { RegisterScreen(navigationController) }
                    }
                }
            }
        }
    }
}