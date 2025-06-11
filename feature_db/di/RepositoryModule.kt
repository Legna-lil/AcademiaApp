package com.example.academiaui.feature_db.di

import com.example.academiaui.feature_db.daos.DownloadDao
import com.example.academiaui.feature_db.daos.RecordDao
import com.example.academiaui.feature_db.daos.StarDao
import com.example.academiaui.feature_db.repositories.DownloadRepository
import com.example.academiaui.feature_db.repositories.RecordRepository
import com.example.academiaui.feature_db.repositories.StarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRecordRepository(recordDao: RecordDao): RecordRepository {
        return RecordRepository(recordDao)
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(downloadDao: DownloadDao): DownloadRepository {
        return DownloadRepository(downloadDao)
    }

    @Provides
    @Singleton
    fun provideStarRepository(starDao: StarDao): StarRepository {
        return StarRepository(starDao)
    }
}