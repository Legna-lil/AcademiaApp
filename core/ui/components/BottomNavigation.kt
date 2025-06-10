package com.example.academiaui.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.AppState
import com.example.academiaui.core.data.viewmodel.AppStateViewModel


@Composable
fun BottomNavigation(
    appStateViewModel: AppStateViewModel = viewModel()
) {
    val currentAppState = appStateViewModel.currentAppState.value

    BottomAppBar(content= {
        Row(
            modifier = Modifier.fillMaxSize(1.0f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                appStateViewModel.setAppState(AppState.SEARCH)
            }) {
                Icon(
                    Icons.Default.Search, contentDescription = "Search",
                    tint = if(currentAppState == AppState.SEARCH) Color.Red
                    else Color.White
                )
            }
            IconButton(onClick = {
                appStateViewModel.setAppState(AppState.HOME)
            }) {
                Icon(
                    Icons.Default.Home, contentDescription = "Home",
                    tint = if(currentAppState == AppState.HOME) Color.Red
                    else Color.White
                )
            }
            IconButton(onClick = {
                appStateViewModel.setAppState(AppState.PERSON)
            }) {
                Icon(
                    Icons.Default.Person, contentDescription = "User",
                    tint = if(currentAppState == AppState.PERSON) Color.Red
                    else Color.White
                )
            }
        }
    })
}

