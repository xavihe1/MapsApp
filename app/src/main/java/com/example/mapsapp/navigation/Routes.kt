package com.example.mapsapp.navigation

sealed class Routes(val route: String) {
    object Pantalla1: Routes("LaunchScreen")
    object Pantalla2: Routes("LoginScreen")
    object Pantalla3: Routes("MenuScreen")
    object Pantalla4: Routes("MapScreen")
    object Pantalla5: Routes("AddMarkerScreen")
    object Pantalla6: Routes("MarkerListScreen")
    object Pantalla7: Routes("TakePhotoScreen")
    object Pantalla8: Routes("GalleryScreen")
}
