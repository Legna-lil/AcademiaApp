package com.example.academiaui.feature_manager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.academiaui.core.util.convertArxivUrl
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import com.example.academiaui.feature_manager.util.PdfDownloadWorker.DownloadStatus
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.launch

@Composable
fun ButtonDownload(
    paper: Entry,
    managerViewModel: ManagerViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var isDownloaded by remember { mutableStateOf(false) }
    var showUnloadDialog by remember { mutableStateOf(false) }
    val downloadStatus by managerViewModel.downloadStatus.collectAsState()
    val currentOperation by managerViewModel.currentOperation.collectAsState()

    // 检查当前论文是否正在被操作
    val isProcessing = currentOperation?.let { (id, _) ->
        id == convertArxivUrl(paper.id)
    } ?: false

    LaunchedEffect(paper) {
        isDownloaded = managerViewModel.isDownloaded(paper)
    }

    LaunchedEffect(downloadStatus, paper.id) {
        when (downloadStatus) {
            is DownloadStatus.Success -> {
                if ((downloadStatus as DownloadStatus.Success).paperId == paper.id) {
                    isDownloaded = true
                }
            }
            else -> {}
        }
    }

    val onButtonClick: () -> Unit = onButtonClick@{
        if (isProcessing) {
            return@onButtonClick
        }

        if (isDownloaded) {
            showUnloadDialog = true
        } else {
            coroutineScope.launch {
                managerViewModel.download(paper)
            }
        }
    }

    // 取消收藏确认处理
    val onUnloadConfirm: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unload(paper)
            isDownloaded = false
            showUnloadDialog = false
        }
    }

    // 按钮UI
    FilledTonalButton(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDownloaded) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (isDownloaded) {
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
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("处理中...")
            }
            // 根据状态显示不同的图标
            if (isDownloaded) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "已下载",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("已下载")
            } else {
                Icon(
                    Icons.Filled.Download,
                    contentDescription = "下载",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text("下载")
            }
        }
    }

    // 取消收藏确认对话框
    if (showUnloadDialog) {
        AlertDialog(
            onDismissRequest = { showUnloadDialog = false },
            title = { Text("删除下载") },
            text = { Text("确定要删除下载文件 \"${paper.title}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = onUnloadConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnloadDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}