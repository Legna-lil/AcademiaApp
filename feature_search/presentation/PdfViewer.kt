package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.feature_agent.presentation.ChatBox
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel

import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.util.PdfSource
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.launch

@Composable
fun PdfViewer(
    appStateViewModel: AppStateViewModel = viewModel(),
    agentViewModel: AgentViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel(),
) {
    var coroutineScope = rememberCoroutineScope()
    var showChatDialog by agentViewModel.showChatDialog
    var isDownloaded by remember { mutableStateOf(false) }
    var isStarred by remember { mutableStateOf(false) }
    var inManager by remember { mutableStateOf(false) }

    var showUnloadDialog by remember { mutableStateOf(false) }
    var showUnstarDialog by remember { mutableStateOf(false) }

    val selectedPaper: Entry? by appStateViewModel.selectedPaper
    val selectedPaperType: String by appStateViewModel.selectedPaperType
    val selectedPaperUrl: String by appStateViewModel.selectedPaperPath

    val context = LocalContext.current

    val onUnloadConfirmDownload: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unload(selectedPaper!!)
            isDownloaded = false
            showUnloadDialog = false
        }
    }

    val onUnloadConfirmStar: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unstar(selectedPaper!!)
            isStarred = false
            showUnstarDialog = false
        }
    }

    LaunchedEffect(selectedPaper) {
        Log.i("PDF Viewer", "Open: $selectedPaperUrl")
        if(selectedPaper != null) {
            isDownloaded = managerViewModel.isDownloaded(selectedPaper!!)
            isStarred = managerViewModel.isStarred(selectedPaper!!)
        } else {
            inManager = true
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            appStateViewModel.backFromPaper()
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row() {
            IconButton(onClick = {
                agentViewModel.toggleShowDialog()
            }) {
                Icon(imageVector = Icons.Outlined.SmartToy, contentDescription = "Agent")
            }
            if (!inManager) {
                IconButton(onClick = {
                    if (isDownloaded) {
                        showUnloadDialog = true
                    } else {
                        managerViewModel.download(selectedPaper!!)
                    }
                }) {
                    if (isDownloaded) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Downloaded")
                    } else {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = "Download")
                    }
                }
                IconButton(onClick = {
                    if (isStarred) {
                        showUnstarDialog = true
                    } else {
                        managerViewModel.star(selectedPaper!!)
                        isStarred = true
                    }
                })
                {
                    if (isStarred) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = "Starred")
                    } else {
                        Icon(imageVector = Icons.Outlined.StarOutline, contentDescription = "Star")
                    }
                }
            }
        }

    }
    Box() {
        if (showChatDialog) {
            ChatBox(
                agentViewModel = agentViewModel,
                onDismiss = { agentViewModel.toggleShowDialog() }
            )
        }
        Log.i("Reader", "Read $selectedPaper: $selectedPaperType")
        PdfRendererViewCompose(
            source =
                when (selectedPaperType) {
                    "url" -> PdfSource.Remote(selectedPaperUrl)
                    "uri" -> PdfSource.LocalUri(selectedPaperUrl.toUri())
                    else -> PdfSource.LocalUri(selectedPaperUrl.toUri())
                },
            lifecycleOwner = LocalLifecycleOwner.current,
            headers = HeaderData(mapOf("Authorization" to "123456789")),

            zoomListener = object : PdfRendererView.ZoomListener {
                override fun onZoomChanged(isZoomedIn: Boolean, scale: Float) {
                    Log.i("PDF Zoom", "Zoomed in: $isZoomedIn, Scale: $scale")
                }
            }
        )
    }

    if (showUnloadDialog) {
        AlertDialog(
            onDismissRequest = { showUnloadDialog = false },
            title = { Text("删除下载") },
            text = { Text("确定要删除下载文件 \"${selectedPaper!!.title}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = onUnloadConfirmDownload,
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

    if (showUnstarDialog) {
        AlertDialog(
            onDismissRequest = { showUnstarDialog = false },
            title = { Text("取消收藏") },
            text = { Text("确定要取消收藏 \"${selectedPaper!!.title}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = onUnloadConfirmStar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnstarDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}


