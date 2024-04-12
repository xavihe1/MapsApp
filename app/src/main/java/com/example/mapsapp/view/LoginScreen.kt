package com.example.mapsapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    var enabled by remember { mutableStateOf(true) }

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
        
        TextField(
            modifier = Modifier.padding(10.dp),
            value = myText,
            onValueChange = { myText = it },
            label = { Text(text = "Enter your username/email") }
        )

        TextField(
            value = psswrd,
            onValueChange = { psswrd = it },
            label = { Text(text = "Enter your password") }
        )
        
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = { navController.navigate(Routes.Pantalla3.route) },
            enabled = enabled,
        ) {
            Text(text = "Register")
        }
    }
}




















/*
@Composable
fun passwordField() {
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Enter your password") },
        maxLines = 1,
        singleLine = true,
        keyboardActions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisibility) { Icons.Filled.VisibilityOff }
                else { Icons.Filled.Visibility }

            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = "Password visibility")
            }
        },
        visualTransformation = if (passwordVisibility) { visualTransformation.None }
            else { PasswordVisualTransformation() }

    )
}
 */
