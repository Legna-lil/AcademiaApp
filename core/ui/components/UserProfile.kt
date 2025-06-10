package com.example.academiaui.core.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.feature_manager.data.ManageState
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import com.example.academiaui.feature_manager.data.viewmodel.UserDataStoreViewModel


@Preview
@Composable
fun UserProfile(
    appStateViewModel: AppStateViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel(),
    userDataStoreViewModel: UserDataStoreViewModel = viewModel(),
) {
    val avatarUri by userDataStoreViewModel.avatarUri.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        ) {
            if (avatarUri != null) {
                AsyncImage (
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
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        // 跳转页面
        Button(onClick = {Log.i("Profile", "设置")
            appStateViewModel.manageSetting()
            managerViewModel.setManageState(ManageState.SETTING)},
            modifier = Modifier.background(Color.Transparent)
                .fillMaxWidth()
                .border(2.dp, Color.LightGray)
                .weight(1f),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // 容器透明
                contentColor = Color.Black // 按需设置内容颜色
            )
        ) {
            Text("设置", style = MaterialTheme.typography.titleLarge)
        }
        // 跳转页面
        Button(onClick = {Log.i("Profile", "收藏夹")
            appStateViewModel.manageSetting()
            managerViewModel.setManageState(ManageState.STAR)},
            modifier = Modifier.background(Color.Transparent)
                .fillMaxWidth()
                .border(2.dp, Color.LightGray)
                .weight(1f),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // 容器透明
                contentColor = Color.Black // 按需设置内容颜色
            )
        ) {
            Text("收藏夹", style = MaterialTheme.typography.titleLarge)
        }

        Button(onClick = { Log.i("Profile", "浏览记录")
            appStateViewModel.manageSetting()
            managerViewModel.setManageState(ManageState.RECORD) },
            modifier = Modifier.background(Color.Transparent)
                .fillMaxWidth()
                .border(2.dp, Color.LightGray)
                .weight(1f),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // 容器透明
                contentColor = Color.Black // 按需设置内容颜色
            )
        ) {
            Text("浏览记录", style = MaterialTheme.typography.titleLarge)
        }
        // 跳转页面
        Button(onClick = {Log.i("Profile", "本地下载")
            appStateViewModel.manageSetting()
            managerViewModel.setManageState(ManageState.DOWNLOAD) },
            modifier = Modifier.background(Color.Transparent)
                .fillMaxWidth()
                .border(2.dp, Color.LightGray)
                .weight(1f),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // 容器透明
                contentColor = Color.Black // 按需设置内容颜色
            )
        ) {
            Text("本地下载", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.weight(3f))
    }
}

