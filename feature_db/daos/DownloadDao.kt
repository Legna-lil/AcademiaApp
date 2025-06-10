package com.example.academiaui.feature_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.academiaui.feature_db.entities.Download
import kotlinx.coroutines.flow.Flow


@Dao
interface DownloadDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(download: Download)

    @Query("DELETE FROM download WHERE url = :url")
    suspend fun delete(url: String)

    @Query("SELECT * FROM download")
    fun getAllDownloads(): Flow<List<Download>>

    @Query("SELECT * FROM download WHERE url = :url")
    fun getDownload(url: String): Flow<Download>

    @Query("SELECT EXISTS(SELECT 1 FROM download WHERE url = :url)")
    suspend fun downloadExists(url: String): Boolean
}