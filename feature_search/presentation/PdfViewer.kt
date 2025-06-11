package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.core.util.convertArxivUrl
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
    val downloads by managerViewModel.downloads.collectAsState()
    var isStarred by remember { mutableStateOf(false) }
    var inManager by remember { mutableStateOf(false) }
    var pdfLoadError: String? by remember { mutableStateOf(null) }

    var showUnloadDialog by remember { mutableStateOf(false) }
    var showUnstarDialog by remember { mutableStateOf(false) }

    val selectedPaper: Entry? by appStateViewModel.selectedPaper
    val selectedPaperType: String by appStateViewModel.selectedPaperType
    // 传入的可能是处理过的Url或Uri
    val selectedPaperUrl: String by appStateViewModel.selectedPaperPath

    val pdfStatusCallBack = remember {
        object : PdfRendererView.StatusCallBack {
            override fun onPdfRenderSuccess() {
                Log.i("PDF_STATUS", "PDF 渲染成功")
                pdfLoadError = null
            }

            override fun onError(error: Throwable) {
                Log.e("PDF_ERROR", "PdfRendererViewCompose 内部错误: ${error.message}", error)
                // 当 PdfRendererViewCompose 内部发生错误时，更新错误状态
                pdfLoadError = error.message ?: "PDF 渲染或加载时发生未知错误"
            }

            override fun onPdfRenderStart() {
                Log.i("PDF_STATUS", "PDF 开始渲染")
            }
        }
    }

    val operationsInProgress by managerViewModel.operationsInProgress.collectAsState()

    val paperUrl = remember(selectedPaper) {
        if (selectedPaper != null) convertArxivUrl(selectedPaper!!.id) else ""  // 若selectedPaper == null，应该触发inManager = true
    }
    val isProcessing = operationsInProgress.contains(paperUrl)

    val isDownloaded = remember(downloads) {
        downloads.any { it.url == paperUrl }
    }

    val onUnloadConfirmDownload: () -> Unit = {
        coroutineScope.launch {
            managerViewModel.unload(selectedPaper!!)
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
                IconButton(
                    onClick = {
                        if (!isProcessing) {
                            if (isDownloaded) {
                                showUnloadDialog = true
                            } else {
                                managerViewModel.download(selectedPaper!!)
                            }
                        }
                    },
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        if (isDownloaded) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Downloaded")
                        } else {
                            Icon(imageVector = Icons.Filled.Download, contentDescription = "Download")
                        }
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showChatDialog) {
            ChatBox(
                agentViewModel = agentViewModel,
                onDismiss = { agentViewModel.toggleShowDialog() }
            )
        }
        Log.i("Reader", "Read $selectedPaper: $selectedPaperType")
        if (pdfLoadError == null) {
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
                },
                statusCallBack = pdfStatusCallBack
            )
        } else {
            Text("显示 PDF 时发生错误")
        }
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


