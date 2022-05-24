package com.gifapplication.repository

import com.gifapplication.data.local.GifDao
import com.gifapplication.data.model.Gif
import com.gifapplication.data.model.GifResponse
import com.gifapplication.data.remote.GifAPI
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GifRepository @Inject constructor(
    private val gifApi: GifAPI,
    private val gifDao: GifDao
) {

    suspend fun getTrendingGifs(pageNumber: Int): Response<GifResponse> {
        return gifApi.getTrendingGifs(pageNumber = pageNumber)
    }

    suspend fun searchGifs(queryString : String, pageNumber: Int): Response<GifResponse> {
        return gifApi.searchGifs(query = queryString, pageNumber = pageNumber)
    }

    fun getAllGifs() = gifDao.getGifs()

    suspend fun insertGif(gif: Gif) = gifDao.insert(gif)

    fun isGifAvailable(gif: Gif) = gifDao.isGifAvailable(gif.id)

    suspend fun deleteGif(gif: Gif) = gifDao.delete(gif)

    suspend fun deleteAllGif() = gifDao.deleteAllGifs()
}