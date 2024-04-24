package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewModel.MapsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val myViewModel = MapsViewModel()
    var myText by remember { mutableStateOf("") }
    var psswrd by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())

    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != "" && storedUserData.value[1] != "") {
        myViewModel.modifyProcessing(true)
        myViewModel.login(storedUserData.value[0], storedUserData.value[1])
        if (myViewModel.goToNext.value == true) {
            navController.navigate(Routes.Pantalla3.route)
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
        userPrefs.saveUserData(username = "", userpass = "")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "login",
            modifier = Modifier
                .size(170.dp)
                .clip(RoundedCornerShape(25f))
                .padding(5.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            TextField(
                modifier = Modifier.weight(1f),
                value = myText,
                onValueChange = { myText = it },
                label = { Text(text = "Enter your username/email") }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            TextField(
                modifier = Modifier.weight(1f),
                value = psswrd,
                onValueChange = { psswrd = it },
                label = { Text(text = "Enter your password") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = { myViewModel.login(myText, psswrd) },
                enabled = myText.isNotEmpty() && psswrd.isNotEmpty() && !(myViewModel.showProgressBar.value ?: false),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Login")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = { navController.navigate(Routes.Pantalla10.route) },
        ) {
            Text(text = "Register")
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = { navController.navigate(Routes.Pantalla3.route) },
        ) {
            Text(text = "VIP")
        }
    }
    //Observamos el estado del goToNext para navegar a la siguiente pantalla
    val goToNext by myViewModel.goToNext.observeAsState()
    goToNext?.let {
        if (it) {
            navController.navigate(Routes.Pantalla3.route)
        }
    }
    //Observamos el estado de showProgressBar para mostrar el indicador de carga
    val showProgressBar by myViewModel.showProgressBar.observeAsState()
    showProgressBar?.let {showProgress ->
        if (showProgress) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}