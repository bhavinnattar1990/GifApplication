package com.gifapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gifapplication.data.model.Gif
import com.gifapplication.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Gif::class], version = 1)
@TypeConverters(Converters::class)
abstract class GifDatabase : RoomDatabase() {

    abstract fun getGifDao(): GifDao

    class Callback @Inject constructor(
        private val database: Provider<GifDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}