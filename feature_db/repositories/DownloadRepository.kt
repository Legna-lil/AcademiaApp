package com.example.academiaui.feature_db.repositories

import android.util.Log
import com.example.academiaui.feature_db.daos.DownloadDao
import com.example.academiaui.feature_db.entities.Download
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class DownloadRepository(private val downloadDao: DownloadDao) {

    suspend fun getAllDownloads(): Flow<List<Download>> =
        withContext(Dispatchers.IO) {
            downloadDao.getAllDownloads()
        }

    suspend fun insertDownload(download: Download) =
        withContext(Dispatchers.IO) {
            downloadDao.insert(download)
        }

    suspend fun deleteDownload(url: String) =
        withContext(Dispatchers.IO) {
            downloadDao.delete(url)
        }

    suspend fun downloadExists(url: String): Boolean {
        Log.i("Query", downloadDao.getDownload(url).toString())
        return downloadDao.downloadExists(url)
    }

    suspend fun getDownload(url: String): Flow<Download> {
        return downloadDao.getDownload(url)
    }

}