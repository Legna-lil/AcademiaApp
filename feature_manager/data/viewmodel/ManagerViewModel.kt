package com.example.academiaui.feature_manager.data.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academiaui.core.util.convertArxivUrl
import com.example.academiaui.feature_db.entities.Download
import com.example.academiaui.feature_db.entities.Record
import com.example.academiaui.feature_db.entities.Star
import com.example.academiaui.feature_db.repositories.DownloadRepository
import com.example.academiaui.feature_db.repositories.RecordRepository
import com.example.academiaui.feature_db.repositories.StarRepository
import com.example.academiaui.feature_manager.data.ManageState
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateSetOf
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.academiaui.core.util.modifyTitle
import com.example.academiaui.feature_db.entities.ListItem
import com.example.academiaui.feature_manager.util.PdfDownloadWorker
import com.example.academiaui.feature_manager.util.PdfDownloadWorker.DownloadStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import java.time.LocalTime
import javax.inject.Inject
import kotlin.collections.joinToString

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.academiaui.core.util.showToast
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val downloadRepository: DownloadRepository,
    private val starRepository: StarRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> get() = _records

    private val _downloads = MutableStateFlow<List<Download>>(emptyList())
    val downloads: StateFlow<List<Download>> get() = _downloads

    private val _stars = MutableStateFlow<List<Star>>(emptyList())
    val stars: StateFlow<List<Star>> get() = _stars

    private val _manageState = mutableStateOf(ManageState.RECORD)
    val manageState: State<ManageState> get() = _manageState

    // 下载状态
    private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatus: StateFlow<DownloadStatus> get() = _downloadStatus

    // 添加当前操作状态（包含paperId和操作类型）
    private val _currentOperation = MutableStateFlow<Pair<String, OperationType>?>(null)
    val currentOperation = _currentOperation.asStateFlow()

    enum class OperationType { DOWNLOAD, UNLOAD }

    // 是否处于管理模式
    val isManageMode = mutableStateOf(false)
    // 选中条目
    val selectedUrls = mutableStateSetOf<String>()

    private val workManager = WorkManager.getInstance(context)

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                // 并行加载所有数据
                launch { loadRecords() }
                launch { loadDownloads() }
                launch { loadStars() }

            } catch (e: Exception) {
                Log.e("Database", e.message.toString())
            }
        }
    }

    private suspend fun loadRecords() {
        recordRepository.getAllRecords().collect {
            _records.value = it
        }
    }

    private suspend fun loadDownloads() {
        downloadRepository.getAllDownloads().collect {
            _downloads.value = it
        }
    }

    private suspend fun loadStars() {
        starRepository.getAllStars().collect {
            _stars.value = it
        }
    }

    fun setManageState(manageState: ManageState) {
        _manageState.value = manageState
    }

    // 管理模式：编辑/取消
    fun toggleManageMode() {
        isManageMode.value = !isManageMode.value
        if (!isManageMode.value) selectedUrls.clear()
    }

    // 选择列表
    fun toggleSelection(url: String) {
        if (selectedUrls.contains(url)) {
            selectedUrls.remove(url)
            Log.i("Manager", "Remove selection $url")
        } else {
            selectedUrls.add(url)
            Log.i("Manager", "Add selection $url")
        }
    }

    fun removeSelection() {
        selectedUrls.clear()
        Log.i("Manager", "Selection cleared")
    }

    // 浏览记录操作
    fun record(paper: Entry) {
        viewModelScope.launch {
            val record = Record(
                convertArxivUrl(paper.id),
                modifyTitle(paper.title),
                paper.author.joinToString { author -> author.name },
                LocalTime.now()
            )
            recordRepository.insertRecord(record)
            Log.i("RECORD", "Successfully inserted")
        }
    }

    // 重载函数——ListItem
    fun record(item: ListItem) {
        viewModelScope.launch {
            val record = Record(
                item.url,
                item.title,
                item.author,
                LocalTime.now()
            )
            recordRepository.insertRecord(record)
            Log.i("RECORD", "Successfully inserted")
        }
    }

    fun removeRecord(){
        viewModelScope.launch {
            for(selectedUrl in selectedUrls) {
                recordRepository.deleteRecord(selectedUrl)
            }
            selectedUrls.clear()
            toggleManageMode()
            Log.i("Manager", "删除成功")
        }
    }

    // 下载操作
    fun download(paper: Entry) {
        _currentOperation.value = paper.id to OperationType.DOWNLOAD
        val url = convertArxivUrl(paper.id)
        val title = modifyTitle(paper.title)
        val author = paper.author.joinToString { author -> author.name }
        val updatedTime = paper.updated
        // 检查是否已下载
        Log.i("DOWNLOAD", "Start to download")
        viewModelScope.launch {
            if (downloadRepository.downloadExists(url)) {
                _downloadStatus.value = DownloadStatus.Error("该论文已下载")
                return@launch
            }
            // 启动下载任务
            startDownload(url, title, author, updatedTime)
        }
    }

    // 启动下载任务
    private fun startDownload(url: String, title: String, author: String, updatedTime: Date) {
        val updatedTimeMillis = updatedTime.time

        // 创建工作请求
        val inputData = workDataOf(
            "url" to url,
            "title" to title,
            "author" to author,
            "update" to updatedTimeMillis
        )
        Log.i("DOWNLOAD", "getData")
        Log.i("Network", NetworkUtils.isNetworkAvailable(context).toString())
        val request = OneTimeWorkRequestBuilder<PdfDownloadWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresDeviceIdle(false)
                    .build()
            )
            .addTag("pdf_download")
            .build()
        Log.i("DOWNLOAD","work")
        // 监听工作状态
        workManager.getWorkInfoByIdLiveData(request.id).observeForever { info ->
            when (info?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    viewModelScope.launch {
                        val uri = info.outputData.getString("uri")
                        if(uri != null) {
                            val download = Download(
                                url = url,
                                title = title,
                                author = author,
                                uri = uri,
                                updatedTime = Date(updatedTimeMillis),
                                downloadTime = LocalTime.now()
                            )
                            downloadRepository.insertDownload(download)
                            _downloadStatus.value = DownloadStatus.Success(url)
                            Log.i("DOWNLOAD", "Successfully DOWNLOAD")
                        }
                    }
                    showToast(context, "下载成功！")
                    _currentOperation.value = null
                }
                WorkInfo.State.FAILED -> {
                    _downloadStatus.value = DownloadStatus.Error("下载失败")
                    showToast(context, "下载失败！")
                    _currentOperation.value = null
                }
                else -> {}
            }
        }
        workManager.enqueue(request)
        Log.i("DOWNLOAD", "Enqueue")
    }

    fun unload(paper: Entry) {
        _currentOperation.value = paper.id to OperationType.UNLOAD
        viewModelScope.launch {
            val download = downloadRepository.getDownload(convertArxivUrl(paper.id)).firstOrNull()
            if (download != null && download.uri?.isNotEmpty() == true) {
                try {
                    val uri = Uri.parse(download.uri)
                    context.contentResolver.delete(uri, null, null)
                    downloadRepository.deleteDownload(download!!.url)
                    Log.i("DOWNLOAD", "Successfully delete download")
                } catch (e: Exception) {
                    Log.e("DOWNLOAD", "文件删除异常: ${e.message}")
                }
            }
        }
    }

    fun removeDownload() {
        viewModelScope.launch {
            for (selectedUrl in selectedUrls) {
                // 1. 从数据库获取下载记录
                val download = downloadRepository.getDownload(selectedUrl).firstOrNull()
                // 2. 通过 Uri 删除文件
                if (download != null && download.uri?.isNotEmpty() == true) {
                    try {
                        val uri = Uri.parse(download.uri)
                        context.contentResolver.delete(uri, null, null)
                    } catch (e: Exception) {
                        Log.e("DOWNLOAD", "文件删除异常: ${e.message}")
                        showToast(context, "删除下载文件失败！")
                    }
                }
                // 3. 删除数据库记录
                downloadRepository.deleteDownload(selectedUrl)
            }
            // 4. 清除选择并退出管理模式
            selectedUrls.clear()
            toggleManageMode()
            Log.i("Manager", "下载项删除成功")
            showToast(context, "删除下载文件成功！")
        }
        Log.i("Manager", "删除成功")
    }

    suspend fun isDownloaded(paper: Entry): Boolean {
        val url = convertArxivUrl(paper.id)
        return downloadRepository.downloadExists(url)
    }

    // 收藏操作
    fun star(paper: Entry) {
        viewModelScope.launch {
            val star = Star(
                convertArxivUrl(paper.id),
                modifyTitle(paper.title),
                paper.author.joinToString { author -> author.name },
                LocalTime.now()
            )
            try {
                starRepository.insertStar(star)
                Log.i("STAR", "Successfully add to STAR")
                showToast(context, "已收藏")
            } catch (e: Exception) {
                Log.e("STAR", "出现异常" + e.message.toString())
            }
        }
    }

    fun unstar(paper: Entry) {
        viewModelScope.launch {
            val url = convertArxivUrl(paper.id)
            try {
                starRepository.deleteStar(url)
                Log.i("STAR", "Successfully delete star")
                showToast(context, "取消收藏")
            } catch (e: Exception) {
                Log.e("STAR", "出现异常" + e.message.toString())
            }
        }
    }

    fun removeStar() {
        viewModelScope.launch {
            try {
                for(selectedUrl in selectedUrls) {
                    starRepository.deleteStar(selectedUrl)
                }
                selectedUrls.clear()
                toggleManageMode()
                Log.i("Manager", "删除成功")
                showToast(context, "移除收藏成功")
            } catch (e: Exception) {
                Log.e("STAR", "删除出现异常" + e.message.toString())
            }
        }
    }

    suspend fun isStarred(paper: Entry): Boolean {
        val url = convertArxivUrl(paper.id)
        return starRepository.starExists(url)
    }
}

/* 网络连通判断器，只在调试时使用 */
object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) // 某些情况下蓝牙也算网络
        } else {
            // 对于 Android M (API 23) 以下的版本
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}