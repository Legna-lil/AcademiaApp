package com.example.academiaui.feature_db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.academiaui.feature_db.entities.Record
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime


@Dao
interface RecordDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(record: Record)

    @Query("DELETE FROM record WHERE url = :url")
    suspend fun delete(url: String)

    @Query("SELECT * FROM record")
    fun getAllRecords(): Flow<List<Record>>

    @Query("UPDATE record SET viewedTime = :viewedTime WHERE url = :url")
    suspend fun update(url: String, viewedTime: LocalTime)
}