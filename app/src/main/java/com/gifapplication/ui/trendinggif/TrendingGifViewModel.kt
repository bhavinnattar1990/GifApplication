package com.gifapplication.ui.trendinggif

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifapplication.data.model.Gif
import com.gifapplication.data.model.GifResponse
import com.gifapplication.repository.GifRepository
import com.gifapplication.utils.NetworkUtil.Companion.hasInternetConnection
import com.gifapplication.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TrendingGifViewModel @Inject constructor(
    val gifRepository: GifRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val trendingGif: MutableLiveData<Resource<GifResponse>> = MutableLiveData()
    var trendingGifPage = 0
    var trendingGifResponse: GifResponse? = null

    val searchGif: MutableLiveData<Resource<GifResponse>> = MutableLiveData()
    var searchGifResponse: GifResponse? = null
    var searchGifPage = 0

    private val gifEventChannel = Channel<GifEvent>()
    val gifEvent = gifEventChannel.receiveAsFlow()

    sealed class GifEvent{
        data class ShowSavedMessage(val message: String): GifEvent()
    }
    init {
        getTrendingGifs()
    }

    fun getTrendingGifs() = viewModelScope.launch {
        safeTrendingGifCall(trendingGifPage)
    }

    private suspend fun safeTrendingGifCall(page: Int){
        trendingGif.postValue(Resource.Loading())
        try{
            if(hasInternetConnection(context)){
                val response = gifRepository.getTrendingGifs(page)
                trendingGif.postValue(handleTrendingGifResponse(response))
            }
            else{
                trendingGif.postValue(Resource.Error("No Internet Connection"))
            }
        }
        catch (ex : Exception){
            when(ex){
                is IOException -> trendingGif.postValue(Resource.Error("Network Failure"))
                else -> trendingGif.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleTrendingGifResponse(response: Response<GifResponse>): Resource<GifResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                trendingGifPage++
                if (trendingGifResponse == null)
                    trendingGifResponse = resultResponse
                else {
                    val oldGif = trendingGifResponse?.data
                    val newGif = resultResponse.data
                    oldGif?.addAll(newGif)
                }
                return Resource.Success(trendingGifResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchGif(searchQuery: String) = viewModelScope.launch {
        safeSearchNewCall(searchQuery)
    }

    private suspend fun safeSearchNewCall(searchQuery: String){
        searchGif.postValue(Resource.Loading())
        try{
            if(hasInternetConnection(context)){
                val response = gifRepository.searchGifs(searchQuery, searchGifPage)
                searchGif.postValue(handleSearchGifResponse(response))
            }
            else
                searchGif.postValue(Resource.Error("No Internet Connection"))
        }
        catch (ex: Exception){
            when(ex){
                is IOException -> searchGif.postValue(Resource.Error("Network Failure"))
                else -> searchGif.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSearchGifResponse(response: Response<GifResponse>): Resource<GifResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchGifPage++
                if (searchGifResponse == null)
                    searchGifResponse = resultResponse
                else {
                    val oldGif = searchGifResponse?.data
                    val newGif = resultResponse.data
                    oldGif?.addAll(newGif)
                }
                return Resource.Success(searchGifResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveGif(gif: Gif) {
        viewModelScope.launch {
            gifRepository.insertGif(gif)
            gifEventChannel.send(GifEvent.ShowSavedMessage("Gif Saved."))
        }
    }
}