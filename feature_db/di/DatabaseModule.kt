package com.example.academiaui.feature_db.di

import android.content.Context
import androidx.room.Room
import com.example.academiaui.feature_db.daos.DownloadDao
import com.example.academiaui.feature_db.daos.RecordDao
import com.example.academiaui.feature_db.daos.StarDao
import com.example.academiaui.feature_db.db.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRecordDao(database: UserDatabase): RecordDao {
        return database.recordDao()
    }

    @Provides
    fun provideDownloadDao(database: UserDatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    fun provideStarDao(database: UserDatabase): StarDao {
        return database.starDao()
    }
}