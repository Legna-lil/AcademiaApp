package com.example.academiaui.feature_db.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import com.example.academiaui.feature_db.converter.ListConverter
import com.example.academiaui.feature_db.daos.DownloadDao
import com.example.academiaui.feature_db.daos.RecordDao
import com.example.academiaui.feature_db.daos.StarDao
import com.example.academiaui.feature_db.entities.Download
import com.example.academiaui.feature_db.entities.Record
import com.example.academiaui.feature_db.entities.Star

@Database(
    entities = [Record::class, Download::class, Star::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class UserDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun downloadDao(): DownloadDao
    abstract fun starDao(): StarDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "academia_user_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}