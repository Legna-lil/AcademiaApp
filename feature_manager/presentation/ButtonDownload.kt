package com.example.academiaui.feature_manager.presentation

import android.util.Log
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

// 组件 ButtonDownload.kt
@Composable
fun ButtonDownload(
    paper: Entry,
    managerViewModel: ManagerViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val downloads by managerViewModel.downloads.collectAsState()
    val paperUrl = remember(paper.id) { convertArxivUrl(paper.id) }
    val isDownloaded = remember(downloads) { downloads.any { it.url == paperUrl } }
    var showUnloadDialog by remember { mutableStateOf(false) }
    val operationsInProgress by managerViewModel.operationsInProgress.collectAsState()
    val isProcessing = operationsInProgress.contains(paperUrl)

    val onButtonClick: () -> Unit = {
        if (!isProcessing) {
            if (isDownloaded) {
                showUnloadDialog = true
            } else {
                managerViewModel.download(paper)
            }
        }
    }

    val onUnloadConfirm: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unload(paper)
            showUnloadDialog = false
        }
    }

    FilledTonalButton(
        onClick = onButtonClick,
        enabled = !isProcessing,
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
            } else {
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
    }

    if (showUnloadDialog) {
        AlertDialog(
            onDismissRequest = { showUnloadDialog = false },
            title = { Text("删除下载") },
            text = { Text("确定要删除下载文件 \"${paper.title}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = onUnloadConfirm,
                    enabled = !isProcessing,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("确定")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnloadDialog = false },
                    enabled = !isProcessing
                ) {
                    Text("取消")
                }
            }
        )
    }
}