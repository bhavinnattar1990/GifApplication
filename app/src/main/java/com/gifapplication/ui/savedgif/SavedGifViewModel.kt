package com.gifapplication.ui.savedgif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifapplication.data.model.Gif
import com.gifapplication.repository.GifRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedGifViewModel @Inject constructor(
    private val gifRepository: GifRepository
) : ViewModel() {

    private val savedGifEventChannel = Channel<SavedGifEvent>()
    val savedGifEvent = savedGifEventChannel.receiveAsFlow()

    fun getAllGifs() = gifRepository.getAllGifs()

    fun onDeleteGif(gif: Gif) {
        viewModelScope.launch {
            gifRepository.deleteGif(gif)
            savedGifEventChannel.send(SavedGifEvent.ShowSavedMessage("Gif Deleted."))
        }
    }

    sealed class SavedGifEvent{
        data class ShowSavedMessage(val message: String): SavedGifEvent()
    }
}