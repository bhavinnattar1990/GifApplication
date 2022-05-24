package com.gifapplication.data.remote

import com.gifapplication.data.model.GifResponse
import com.gifapplication.utils.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GifAPI {

    @GET("/v1/gifs/trending")
    suspend fun getTrendingGifs(
        @Query("offset") pageNumber: Int = 0,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<GifResponse>

    @GET("/v1/gifs/search")
    suspend fun searchGifs(
        @Query("offset") pageNumber: Int = 0,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("q") query: String
    ): Response<GifResponse>

}