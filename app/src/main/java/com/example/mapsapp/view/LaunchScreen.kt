package com.example.mapsapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun LaunchScreen(navController: NavController) {
    var animation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (animation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )
    LaunchedEffect(key1 = true) {
        animation = true
        delay(4000)
        navController.popBackStack()
        navController.navigate(Routes.Pantalla4.route)
    }
    Splash(alphaAnim.value)
}

@Composable
fun Splash(alphaAnim: Float) {
    Column(modifier = Modifier.fillMaxSize()
        .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.maps_image),
            contentDescription = "logo", alpha = alphaAnim,
            modifier = Modifier
                .size(350.dp)
        )
    }
}