package com.example.mediavault.ui.model.YoutubeViewModel

sealed class YoutubeUiEvent {
    data class showStreamsButtonClicked(val url:String) : YoutubeUiEvent()
    data class radioButtonClicked(val stream : Stream) : YoutubeUiEvent()
    data object downloadButtonClicked : YoutubeUiEvent()
}