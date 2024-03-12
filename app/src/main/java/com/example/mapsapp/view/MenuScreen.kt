package com.example.mapsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.viewModel.MapsViewModel
import kotlinx.coroutines.launch

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(myViewModel: MapsViewModel) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet {
            Text(text = "Drawer title", modifier = Modifier.padding(16.dp))
            Divider()
            NavigationDrawerItem(
                label = { Text(text = "Drawer Item 1") },
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                }
            )
        }
    }) {
        MyScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(myViewModel: MapsViewModel, state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(text = "My SuperApp") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}
*/