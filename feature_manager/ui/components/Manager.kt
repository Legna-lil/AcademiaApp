package com.example.academiaui.feature_manager.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.core.util.ConfirmationDialog
import com.example.academiaui.feature_db.entities.ListItem
import com.example.academiaui.feature_manager.data.ManageState
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun Manager(
    appStateViewModel: AppStateViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val records = managerViewModel.records.collectAsState()
    val downloads = managerViewModel.downloads.collectAsState()
    val stars = managerViewModel.stars.collectAsState()

    val manageState = managerViewModel.manageState.value
    val isManageMode = managerViewModel.isManageMode.value
    val selectedUrls = managerViewModel.selectedUrls

    var showDeleteDialog by remember { mutableStateOf(false) }

    // 取消收藏确认处理
    val onDeleteConfirm: () -> Unit = {
        coroutineScope.launch {
            try {
                when(manageState) {
                    ManageState.RECORD -> {
                        managerViewModel.removeRecord()
                    }
                    ManageState.DOWNLOAD -> {
                        managerViewModel.removeDownload()
                    }
                    ManageState.STAR -> {
                        managerViewModel.removeStar()
                    }
                    ManageState.SETTING -> {
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("Manager", e.message.toString())
            }
            showDeleteDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) { // 让 Box 填充整个屏幕
        Column(modifier = Modifier.fillMaxSize()) { // Column 填充 Box
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = {
                    appStateViewModel.navigateBack()
                    managerViewModel.removeSelection()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    managerViewModel.toggleManageMode()
                }) {
                    Icon(
                        imageVector = if (isManageMode) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = "管理"
                    )
                }
            }
            when(manageState) {
                ManageState.RECORD ->
                    ManageList(records.value.map {
                        it as ListItem
                    }, isManageMode,
                        onClick = {
                            appStateViewModel.selectPaperInManager(it)
                            managerViewModel.record(it)
                        })
                ManageState.DOWNLOAD ->
                    ManageList(downloads.value.map {
                        it as ListItem
                    }, isManageMode,
                        onClick = {
                            appStateViewModel.selectPaperInManager(it)
                            managerViewModel.record(it)
                        })
                ManageState.STAR ->
                    ManageList(stars.value.map {
                        it as ListItem
                    }, isManageMode,
                        onClick = {
                            appStateViewModel.selectPaperInManager(it)
                            managerViewModel.record(it)
                        })
                ManageState.SETTING -> {
                    SettingPage()
                }
            }
        }

        // 悬浮的 FAB
        if(isManageMode && manageState != ManageState.SETTING) {
            // AnimatedVisibility 可以提供进出动画
            AnimatedVisibility(
                visible = isManageMode,
                enter = slideInVertically(initialOffsetY = { it }), // 从底部滑入
                exit = slideOutVertically(targetOffsetY = { it }), // 滑出底部
                modifier = Modifier.padding(all = 16.dp)
                    .align(Alignment.BottomEnd)// 左右和底部留白
            ) {
                FloatingActionButton(
                    onClick = {
                        if(selectedUrls.size > 0)
                            showDeleteDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(), // 填充整个宽度
                    containerColor = MaterialTheme.colorScheme.error // 示例颜色
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("删除 (${selectedUrls.size})")
                    }
                }
            }
        }
    }

    ConfirmationDialog(
        showDialog = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false }, // 点击外部关闭
        title = "删除记录",
        text = "确定要删除所选文章吗？",
        confirmButtonText = "确定",
        onConfirm = onDeleteConfirm, // 确认取消收藏的逻辑
        dismissButtonText = "取消",
        onDismiss = { showDeleteDialog = false }, // 取消对话框
        confirmButtonColors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error // 将确认按钮设为红色
        )
    )
}
