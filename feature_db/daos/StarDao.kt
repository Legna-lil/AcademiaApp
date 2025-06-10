package com.example.academiaui.feature_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.academiaui.feature_db.entities.Star
import kotlinx.coroutines.flow.Flow

@Dao
interface StarDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(star: Star)

    @Query("DELETE FROM star WHERE url = :url")
    suspend fun delete(url: String)

    @Query("SELECT * FROM star")
    fun getAllStars(): Flow<List<Star>>

    @Query("SELECT * FROM star WHERE url = :url")
    fun getStar(url: String): Flow<Star>

    @Query("SELECT EXISTS(SELECT 1 FROM star WHERE url = :url)")
    suspend fun starExists(url: String): Boolean
}