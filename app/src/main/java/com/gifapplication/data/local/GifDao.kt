package com.gifapplication.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gifapplication.data.model.Gif

@Dao
interface GifDao {

    @Query("SELECT * FROM gif_table")
    fun getGifs() : LiveData<List<Gif>>

    @Query("SELECT COUNT() FROM gif_table where id = :gifId")
    fun isGifAvailable(gifId: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gif: Gif) : Long

    @Delete
    suspend fun delete(gif: Gif)

    @Query("DELETE FROM gif_table")
    suspend fun deleteAllGifs()
}