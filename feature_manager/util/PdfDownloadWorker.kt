package com.example.academiaui.feature_manager.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class PdfDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i("Worker", "do work")
        val url = inputData.getString("url") ?: return Result.failure()
        val title = inputData.getString("title") ?: "document"
        return try {
            // 下载并保存PDF文件
            val uri = downloadAndSavePdf(url, title)
            // 创建下载记录
            val outputData = workDataOf(
                "url" to url,
                "title" to title,
                "uri" to uri.toString()
            )
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e("DOWNLOAD", e.message.toString())
            Result.failure()
        }
    }

    private suspend fun downloadAndSavePdf(url: String, title: String): Uri {
        return withContext(Dispatchers.IO) {
            val resolver = applicationContext.contentResolver
            // 创建ContentValues定义文件属性
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                // 对于Android 10+，指定下载位置
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/AcademiaApp/DOWNLOAD")
                }
            }
            // 创建文件Uri
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("无法创建下载文件")
            // 下载文件内容
            downloadFileContent(url, resolver, uri)
            uri
        }
    }

    // 下载文件内容并写入Uri
    private fun downloadFileContent(url: String, resolver: ContentResolver, uri: Uri) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("下载失败: ${response.code}")

            response.body?.byteStream()?.use { inputStream ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                } ?: throw Exception("无法打开输出流")
            } ?: throw Exception("响应体为空")
        }
    }

    // 下载状态密封类
    sealed class DownloadStatus {
        object Idle : DownloadStatus()
        data class Success(val paperId: String) : DownloadStatus()
        data class Error(val message: String) : DownloadStatus()
    }
}


