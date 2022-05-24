package com.gifapplication.di

import android.app.Application
import androidx.room.Room
import com.gifapplication.data.local.GifDao
import com.gifapplication.data.local.GifDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: GifDatabase.Callback): GifDatabase{
        return Room.databaseBuilder(application, GifDatabase::class.java, "gif_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideGifDao(db: GifDatabase): GifDao{
        return db.getGifDao()
    }
}