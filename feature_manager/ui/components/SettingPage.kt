package com.example.academiaui.feature_manager.ui.components

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_manager.data.viewmodel.UserDataStoreViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.academiaui.core.util.showToast
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import com.example.academiaui.feature_manager.util.SubjectMapper


@Preview
@Composable
fun SettingPage(
    managerViewModel: ManagerViewModel = viewModel(),
    userDataStoreViewModel: UserDataStoreViewModel = viewModel()
) {
    val username by userDataStoreViewModel.username.collectAsState()
    val avatarUri by userDataStoreViewModel.avatarUri.collectAsState()
    val selectedFields by userDataStoreViewModel.selectedFields.collectAsState()
    val availableFields = userDataStoreViewModel.categorizedAvailableFields

    val isManageMode = managerViewModel.isManageMode.value

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Persist the URI permission for future use
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            userDataStoreViewModel.updateAvatar(it.toString())
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            showToast(context, "申请照片权限失败")
        }
    }

    Log.i("Subject", selectedFields.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // 允许滚动，以防内容过多
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable {
                    if(isManageMode) {
                        // Check for READ_EXTERNAL_STORAGE permission (or READ_MEDIA_IMAGES for Android 13+)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
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

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .border(1.dp, Color.LightGray, ShapeDefaults.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "用户名", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = username,
                onValueChange = { userDataStoreViewModel.updateUsername(it) },
                enabled = isManageMode,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Right
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .border(1.dp, Color.LightGray, ShapeDefaults.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "兴趣领域", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(5.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                if(selectedFields.isEmpty()) {
                    "暂无"
                } else {
                    selectedFields.joinToString("；") {
                        SubjectMapper.toChineseName(it)
                    }
                },
                modifier = Modifier.padding(5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Right
                )
            )
        }
        if(isManageMode) {
            FlowRow (modifier = Modifier.padding(20.dp)
                .wrapContentHeight()) {
                availableFields.forEach { (broaderCategoryName, interestsList) ->
                    Text(
                        text = broaderCategoryName, // 直接使用 broaderCategoryName 作为标题
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // interestsList 是 List<Pair<String, String>>，其中 first 是英文，second 是中文
                        interestsList.forEach { (englishField, chineseName) ->
//                        Log.i("Subject", englishField + " is " +selectedFields.contains(englishField))
                            FilterChip(
                                selected = selectedFields.contains(englishField), // 使用英文名判断选中状态
                                onClick = { userDataStoreViewModel.toggleField(englishField) }, // 传递英文名
                                label = { Text(chineseName) }, // 显示中文名
                                leadingIcon = if (selectedFields.contains(englishField)) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}