package com.example.mediavault.ui.model.ConverterViewModel

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.mediavault.ui.model.HomeViewModel.MetaDataReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val MetaDataReader : MetaDataReader
):ViewModel() {

    private var _uiState = MutableStateFlow(ConverterUIState())
    val uiState = _uiState.asStateFlow()

    private val externalDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    private val yourFolderName = "MediaVaultDownloads" // Replace with the desired folder name
    private val folder = File(externalDir, yourFolderName)

    fun onEvent(event : ConverterUIEvent){
        when(event){
            ConverterUIEvent.VTAUDIOExecutionStart -> {
                runFfmpeg(2)
            }
            ConverterUIEvent.VTGIFExecutionStart -> {
                runFfmpeg(1)
            }
            is ConverterUIEvent.LauncherLaunched -> {
                _uiState.value = uiState.value.copy(
                    uri = event.uri,
                    fileName = event.uri?.let { getNameFromUri(it) }
                )
            }
            is ConverterUIEvent.IsDefaultButtonSelected -> {
                _uiState.value = uiState.value.copy(
                    isDefaultSelected = !uiState.value.isDefaultSelected
                )
            }

            is ConverterUIEvent.FrameRateChanged -> {
                _uiState.value = uiState.value.copy(
                    frameRate = event.fps
                )
            }
            is ConverterUIEvent.TrimDurationChanged -> {
                _uiState.value = uiState.value.copy(
                    trimDuration = event.trim
                )
            }

            is ConverterUIEvent.StartFromChanged -> {
                _uiState.value = uiState.value.copy(
                    startTime = event.start
                )
            }
        }
    }

    private fun getRealPath(uri: Uri) : String?{
        return MetaDataReader.getRealPathFromURI(uri)
    }

    private fun getNameFromUri(uri : Uri):String?{
        return MetaDataReader.getFileNameFromURI(uri)
    }

    private fun replaceExtension(name: String, newExtension: String): String {
        val dotIndex = name.lastIndexOf('.')
        return if (dotIndex != -1) {
            name.substring(0, dotIndex + 1) + newExtension
        } else {
            "$name.$newExtension"
        }
    }

    private fun videoToGif(uri: Uri,isdefault:Boolean,startFrom:String?,trim:String?,fps:String?): Int {
        val path = getRealPath(uri)
        createFolder()
        val outputPath =
            folder.absolutePath + "/" + getNameFromUri(uri)?.let { replaceExtension(it, "gif") }
        Config.enableLogCallback { message ->
            _uiState.value = uiState.value.copy(
                callbackStatistics = uiState.value.callbackStatistics + "\n" + message.text
            )
        }
        return if(isdefault){
            FFmpeg.execute("-y -i $path -ss 0 -t 2 -loop 0 -filter_complex \"fps=10, scale=-1:360[s]; [s]split[a][b]; [a]palettegen[palette]; [b][palette]paletteuse\" $outputPath")
        }else{
            val trimUse:String = if(trim == null || trim.toString().isEmpty())"2" else trim
            val fpsUse:String = if(fps == null || fps.toString().isEmpty())"10" else fps
            val startFromUse = if(startFrom == null || startFrom.toString().isEmpty())"0" else startFrom
            FFmpeg.execute("-y -i $path -ss $startFromUse -t $trimUse -loop 0 -filter_complex \"fps=$fpsUse, scale=-1:360[s]; [s]split[a][b]; [a]palettegen[palette]; [b][palette]paletteuse\" $outputPath")
        }
    }

    private fun videoToAudio(uri:Uri):Int{
        val path = getRealPath(uri)
        createFolder()
        val outputPath =
            folder.absolutePath + "/" + getNameFromUri(uri)?.let { replaceExtension(it, "mp3") }
        Config.enableLogCallback { message ->
            _uiState.value = uiState.value.copy(
                callbackStatistics = uiState.value.callbackStatistics + "\n" + message.text
            )
        }
        return FFmpeg.execute("-y -i $path $outputPath")
    }

    private fun runFfmpeg(screenCode:Int){
        _uiState.value = uiState.value.copy(
            isExecuting = true,
            callbackStatistics = ""
        )
        viewModelScope.launch (Dispatchers.IO){
            val returnCode = async {
                uiState.value.uri?.let {
                    when(screenCode){
                        1->videoToGif(it,uiState.value.isDefaultSelected, uiState.value.startTime, uiState.value.trimDuration, uiState.value.frameRate)
                        else->videoToAudio(it)
                    }
                }
            }
            val result = returnCode.await() //we wait till execution of above command
            _uiState.value = uiState.value.copy(
                isExecuting = false,
                executionStatus = result
            )
        }
    }

    //if folder not exists create it
    private fun createFolder(){
        if(!folder.exists() || !folder.isDirectory()){
            folder.mkdir()
        }
    }
}