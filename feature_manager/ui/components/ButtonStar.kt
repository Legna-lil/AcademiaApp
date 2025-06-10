package com.example.academiaui.feature_manager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.util.ConfirmationDialog
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.launch

@Composable
fun ButtonStar(
    paper: Entry,
    managerViewModel: ManagerViewModel = viewModel(),

) {
    val coroutineScope = rememberCoroutineScope()
    // 收藏状态
    var isStarred by remember { mutableStateOf(false) }
    var showUnstarDialog by remember { mutableStateOf(false) }
    // 从数据库加载初始收藏状态
    LaunchedEffect(paper) {
        isStarred = managerViewModel.isStarred(paper)
    }
    // 按钮点击处理
    val onButtonClick: () -> Unit = {
        if (isStarred) {
            // 已收藏状态 - 显示取消收藏对话框
            showUnstarDialog = true
        } else {
            // 未收藏状态 - 直接收藏
            coroutineScope.launch {
                managerViewModel.star(paper)
                isStarred = true
            }
        }
    }
    // 取消收藏确认处理
    val onUnstarConfirm: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unstar(paper)
            isStarred = false
            showUnstarDialog = false
        }
    }
    // 按钮UI
    FilledTonalButton(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isStarred) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (isStarred) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 根据状态显示不同的图标
            if (isStarred) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "已收藏",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("已收藏")
            } else {
                Icon(
                    Icons.Outlined.StarOutline,
                    contentDescription = "收藏",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text("收藏")
            }
        }
    }

    ConfirmationDialog(
        showDialog = showUnstarDialog,
        onDismissRequest = { showUnstarDialog = false }, // 点击外部关闭
        title = "取消收藏",
        text = "确定要取消收藏 \"${paper.title}\" 吗？",
        confirmButtonText = "确定",
        onConfirm = onUnstarConfirm, // 确认取消收藏的逻辑
        dismissButtonText = "取消",
        onDismiss = { showUnstarDialog = false }, // 取消对话框
        confirmButtonColors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error // 将确认按钮设为红色
        )
    )
}
