package com.example.academiaui.feature_db.repositories


import com.example.academiaui.feature_db.daos.RecordDao
import com.example.academiaui.feature_db.entities.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalTime


class RecordRepository(private val recordDao: RecordDao) {

    suspend fun getAllRecords(): Flow<List<Record>> =
        withContext(Dispatchers.IO) {
            recordDao.getAllRecords()
        }

    suspend fun insertRecord(record: Record) =
        withContext(Dispatchers.IO) {
            recordDao.insert(record)
        }

    suspend fun deleteRecord(url: String) =
        withContext(Dispatchers.IO) {
            recordDao.delete(url)
        }

    suspend fun updateRecord(url: String) {
        val currentTime = LocalTime.now()
        recordDao.update(url, currentTime)
    }
}