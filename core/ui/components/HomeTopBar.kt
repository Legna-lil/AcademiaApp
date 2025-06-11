package com.example.academiaui.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.academiaui.core.data.AppState
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.feature_manager.data.viewmodel.UserDataStoreViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    appStateViewModel: AppStateViewModel = viewModel(),
    userDataStoreViewModel: UserDataStoreViewModel = viewModel()
) {
    val avatarUri by userDataStoreViewModel.avatarUri.collectAsState()
    val context = LocalContext.current

    TopAppBar(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .background(Color.LightGray),
        title = {
            Row {
                Text("Academia")
                Spacer(modifier = Modifier.weight(.1f))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                ) {
                    IconButton(
                        onClick = { appStateViewModel.setAppState(AppState.PERSON) }
                    ) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(avatarUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                    MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        })
}
