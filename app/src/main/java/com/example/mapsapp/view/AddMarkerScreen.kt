package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.R
import com.example.mapsapp.model.Markers
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewModel.MapsViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddMarkerScreen(navController: NavController, myViewModel: MapsViewModel, selectedMarker: Markers, nomFitxer: String? = null) {
    val markerReady by myViewModel.marcadorOn.observeAsState(false)

    val userId by myViewModel.userId.observeAsState(null)
    var newMarkerTitle: String by remember { mutableStateOf(selectedMarker.title) }
    var newMarkerSnippet: String by remember { mutableStateOf(selectedMarker.snippet) }
    var newMarkerLat: Double by remember { mutableStateOf(selectedMarker.position.latitude) }
    var newMarkerLong: Double by remember { mutableStateOf(selectedMarker.position.longitude) }
    var newMarkerColor: Float by remember { mutableStateOf(selectedMarker.color) }
    val newMarkerPhoto by myViewModel.imatgeSeleccionada.observeAsState(null)
    val imageUrl by myViewModel.urlImatge.observeAsState(null)

    ModalBottomSheet(onDismissRequest = {
        myViewModel.selectMarker(null)
        myViewModel.hideBottomSheet()
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .size(200.dp)
                    .padding(15.dp),
                contentAlignment = Alignment.Center
            ) {
                if (newMarkerPhoto.toString() != "null") {
                    GlideImage(
                        model = newMarkerPhoto.toString(),
                        contentDescription = "Image from storage",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painterResource(id = R.drawable.maps_image),
                        "Map icon",
                        Modifier.fillMaxSize()
                    )
                }
                IconButton(onClick = {
                    myViewModel.hideBottomSheet()
                    navController.navigate(Routes.Pantalla9.route)}
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, "Take photo", tint = Color.LightGray)
                }
            }
            Text(text = nomFitxer?: "Fes una foto o selecciona una de la galeria")
            TextField(
                value = newMarkerTitle,
                onValueChange = {
                    newMarkerTitle = it
                    myViewModel.selectMarker(
                        Markers(
                            userId,
                            selectedMarker.markerId,
                            LatLng(newMarkerLat, newMarkerLong),
                            newMarkerTitle,
                            newMarkerSnippet,
                            newMarkerColor,
                            newMarkerPhoto.toString()
                        )
                    ) },
                placeholder = { Text(text = "Input marker title...") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.8f)
            )
            TextField(
                value = newMarkerSnippet,
                onValueChange = {
                    newMarkerSnippet = it
                    myViewModel.selectMarker(
                        Markers(
                            userId,
                            selectedMarker.markerId,
                            LatLng(newMarkerLat, newMarkerLong),
                            newMarkerTitle,
                            newMarkerSnippet,
                            newMarkerColor,
                            newMarkerPhoto.toString()
                        )
                    ) },
                placeholder = { Text(text = "Input marker snippet...") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.8f),
            )
            TextField(
                value = "$newMarkerLat",
                onValueChange = { newMarkerLat = it.toDouble() },
                placeholder = { Text(text = "Input marker latitude...") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.8f),
                enabled = false
            )
            TextField(
                value = "$newMarkerLong",
                onValueChange = { newMarkerLong = it.toDouble() },
                placeholder = { Text(text = "Input marker longitude...") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(0.8f),
                enabled = false
            )
            // Color radio buttons
            ColorRadioButtons(myViewModel, {newMarkerColor = it}, newMarkerColor)

            // Save marker
            Button(
                onClick = {
                    myViewModel.uploadImage(newMarkerPhoto, nomFitxer?: "${System.currentTimeMillis() }", selectedMarker.photo)
                },
                shape = RoundedCornerShape(10),
                enabled = true,
                modifier = Modifier.padding(15.dp)
            ) {
                Text ("Save Marker")
            }

            if (markerReady) {
                if (selectedMarker.markerId == null) {
                    myViewModel.guardarMarcadors(
                        Markers(
                            userId, null,
                            LatLng(newMarkerLat, newMarkerLong),
                            if (!newMarkerTitle.isBlank()) newMarkerTitle else "Marcador sense nom",
                            if (!newMarkerSnippet.isBlank()) newMarkerSnippet else "Sense descripció",
                            newMarkerColor, imageUrl.toString()
                        )
                    )
                } else {
                    myViewModel.editarMarcador(
                        Markers(
                            userId,
                            selectedMarker.markerId,
                            LatLng(newMarkerLat, newMarkerLong),
                            if (!newMarkerTitle.isBlank()) newMarkerTitle else "Marcador sense nom",
                            if (!newMarkerSnippet.isBlank()) newMarkerSnippet else "Sense descripció",
                            newMarkerColor, imageUrl.toString()
                        )
                    )
                }

                myViewModel.cambiarLocalitzacio(selectedMarker.position)
                myViewModel.selectMarker(null)
                myViewModel.selectImage(null)
                myViewModel.selectImageUrl(null)
                myViewModel.confirmMarcadorOn(false)

                myViewModel.hideBottomSheet()
                myViewModel.getAllMarkers()
                navController.navigate(Routes.Pantalla4.route)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorRadioButtons(myViewModel: MapsViewModel, SelectColor: (Float) -> Unit, color: Float?){
    val markerColors: Array<Pair<Color, Float>> = arrayOf(
        Pair(Color.Red, HUE_RED), Pair(Color.Blue, HUE_BLUE), Pair(Color.Green, HUE_GREEN),
        Pair(Color.Yellow, HUE_YELLOW), Pair(Color.Cyan, HUE_CYAN), Pair(Color.Magenta, HUE_MAGENTA)
    )
    var selectedOption by remember { mutableStateOf(markerColors[0]) }
    markerColors.forEach { if (it.second == color) selectedOption = it }
    FlowRow (horizontalArrangement = Arrangement.Center){
        markerColors.forEach { color ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(Icons.Filled.LocationOn, "Coloured Marker Icon", tint = color.first)
                RadioButton(
                    selected = color == selectedOption,
                    onClick = {
                        selectedOption = color
                        SelectColor(color.second)
                    }
                )
            }
        }
    }
}