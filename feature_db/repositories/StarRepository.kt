package com.example.academiaui.feature_db.repositories

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_db.daos.StarDao
import com.example.academiaui.feature_db.entities.Star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class StarRepository(private val starDao: StarDao) {

    suspend fun getAllStars(): Flow<List<Star>> =
        withContext(Dispatchers.IO) {
            starDao.getAllStars()
        }

    suspend fun insertStar(star: Star) =
        withContext(Dispatchers.IO) {
            starDao.insert(star)
        }

    suspend fun deleteStar(url: String) =
        withContext(Dispatchers.IO) {
            starDao.delete(url)
        }

    suspend fun starExists(url: String): Boolean {
        Log.i("Query", starDao.getStar(url).toString())
        return starDao.starExists(url)
    }
}