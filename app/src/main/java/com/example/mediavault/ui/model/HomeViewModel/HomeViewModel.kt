package com.example.mediavault.ui.model.HomeViewModel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle : SavedStateHandle,
    //it contains map of variables that survives process death
    //so that we return to our app from recent app tab , we get our previous state instead of empty state,
    val player : Player,
    private val metaDataReader: MetaDataReader
): ViewModel(){

    private val videoUris =  savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    //from uris convert each of them to videoItem
    val videoItems = videoUris.map{uris->
        uris.map{uri->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReader.getFileNameFromURI(uri) ?: "No Name"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    //stateIn converts flow to stateFlow , passed initial value as empty list

    init{
        player.prepare()
    }

    fun addVideoUri(uri:Uri){
        savedStateHandle["videoUris"] =  videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri)) // add media item to end of playlist
    }

    fun playVideo(uri:Uri){
        player.setMediaItem(
            videoItems.value.find { it.contentUri == uri }?.mediaItem ?: return
        )
    }

}