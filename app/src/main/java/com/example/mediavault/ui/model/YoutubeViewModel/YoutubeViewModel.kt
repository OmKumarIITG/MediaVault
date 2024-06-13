package com.example.mediavault.ui.model.YoutubeViewModel

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val python : Python
) : ViewModel(){

    private var _uiState = MutableStateFlow(YoutubeUIState())
    var uiState = _uiState.asStateFlow()

    fun onEvent(event: YoutubeUiEvent){
        when(event){
            is YoutubeUiEvent.showStreamsButtonClicked ->{
                // when clicked on show streams button reset download status
                _uiState.value = uiState.value.copy(
                    downloadResult = 0
                )
                _uiState.value = _uiState.value.copy(
                    isFetchingStreams = true,
                    url = event.url
                )
                viewModelScope.launch (Dispatchers.IO){
                    //if its not a playlist run showStreams function
                    if(!isPlaylist((event.url))){
                        //clear any playlist stored
                        _uiState.value = uiState.value.copy(
                            playlistVideos = emptyList()
                        )
                        val list = async {
                            showStreams(event.url)
                        }
                        _uiState.value = _uiState.value.copy(
                            streams = list.await().toSet(),
                            isFetchingStreams = false
                        )
                    }else{
                        //clear any stream stored
                        _uiState.value = uiState.value.copy(
                            streams = emptySet()
                        )
                        val videoList = async{
                            showVideosPlaylist(event.url)
                        }
                        _uiState.value = uiState.value.copy(
                            playlistVideos = videoList.await(),
                            isFetchingStreams = false
                        )
                    }

                }
            }

            is YoutubeUiEvent.radioButtonClicked -> {
                _uiState.value  = uiState.value.copy(
                    streamToDownload = event.stream
                )
            }

            YoutubeUiEvent.downloadButtonClicked -> {
                // when again click on download button reset download status
                _uiState.value = uiState.value.copy(
                    downloadResult = 0,
                    isDownloading = true
                )
                viewModelScope.launch(Dispatchers.IO) {
                    val result =
                        if(isPlaylist(_uiState.value.url)){
                            async{
                                downloadPlaylist(_uiState.value.url)
                            }
                        }else{
                            async {
                                downloadStream(_uiState.value.url,_uiState.value.streamToDownload)
                        }
                    }
                    if(result.await()){
                        _uiState.value = uiState.value.copy(
                            downloadResult = 1,
                            isDownloading = false
                        )
                    }else{
                        _uiState.value = uiState.value.copy(
                            downloadResult = -1,
                            isDownloading = false
                        )
                    }
                }
            }
        }
    }

    private fun showStreams(url: String): List<Stream> {
        return try {
                val module = python.getModule("script")
                val showStreams = module["showStreamsVideo"]
                val result = showStreams?.call(url)
                // Process the result if it's not null
                result?.let {
                    val streamList: List<PyObject> = it.asList().filterNotNull()
                    streamList.map { pyObject ->
                        val streamDetails: List<PyObject> = pyObject.asList()
                        val type = streamDetails[0].toInt()
                        val itag = streamDetails[1].toInt()
                        val resOrBitrate = streamDetails[2].toInt()
                        val fileSize = streamDetails[3].toString()
                        val fps = streamDetails[4].toString()
                        val codec = streamDetails[5].toString()
                        Stream(type, itag, resOrBitrate,fileSize,fps,codec)
                    }
                } ?: emptyList()
            } catch (e: Exception) {
                Log.d("debugg", "exception : $e")
                emptyList()
            }
    }

    private fun downloadStream(url:String, streamToDownload : Stream):Boolean{
        val externalDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val yourFolderName = "MediaVaultDownloads" // Replace with the desired folder name
        val folder = File(externalDir, yourFolderName)
        return try {
            val module = python.getModule("script")
            val downloadStream = module["downloadStreams"]
            val itag = streamToDownload.itag
            val result = downloadStream?.call(url,itag,folder.toString())
            result?.toBoolean() ?: false
        } catch (e: Exception) {
            Log.d("debugg", "exception : $e")
            false
        }
    }

    fun isPlaylist(url:String):Boolean{
        return "list"  in url
    }

    private fun showVideosPlaylist(url : String):List<String>{
        return try {
            val module = python.getModule("script")
            val showVideosPlayList = module["showVideosPlayList"]
            val result = showVideosPlayList?.call(url)?.asList()?.mapNotNull { it.toString() } ?: emptyList()
            result
        } catch (e: Exception) {
            Log.d("debugg", "exception : $e")
            emptyList()
        }
    }

    private fun downloadPlaylist(url:String) : Boolean{
        val externalDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val yourFolderName = "MediaVaultDownloads" // Replace with the desired folder name
        val folder = File(externalDir, yourFolderName)
        return try {
            val module = python.getModule("script")
            val downloadStream = module["downloadPlaylist"]
            val result = downloadStream?.call(url,folder.toString())
            result?.toBoolean() ?: false
        } catch (e: Exception) {
            Log.d("debugg", "exception : $e")
            false
        }
    }
}