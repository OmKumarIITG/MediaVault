package com.example.mediavault.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mediavault.R
import com.example.mediavault.ui.model.YoutubeViewModel.YoutubeUiEvent
import com.example.mediavault.ui.model.YoutubeViewModel.YoutubeViewModel
import com.example.mediavault.ui.ui_components.PlaylistColumn
import com.example.mediavault.ui.ui_components.StreamList

@Composable
fun YoutubeDownloaderScreen(
    viewModel : YoutubeViewModel
) {
    var text by remember {
        mutableStateOf("")
    }
    val state = viewModel.uiState.collectAsState()
    val streams = state.value.streams
    val playlistVideos = state.value.playlistVideos
    val isFetchingStreams = state.value.isFetchingStreams
    val downloadStatus = state.value.downloadResult
    val isDownloading = state.value.isDownloading

    Column(
        modifier = Modifier.fillMaxSize().padding(top=2.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            value = text
            ,
            onValueChange = {
                text = it
            },
            label = {
                Text("Paste YouTube Url to download video")
            },
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
               viewModel.onEvent(YoutubeUiEvent.showStreamsButtonClicked(text))
            }
        ) {
            Text("Show Available Streams")
        }

        if(!isFetchingStreams && (streams.isNotEmpty() || playlistVideos.isNotEmpty())) {
            Box(modifier=Modifier.weight(0.5f)){
                Column(modifier = Modifier){
                    if (streams.isNotEmpty()) {
                        StreamList(viewModel = viewModel)
                    } else {
                        PlaylistColumn(videos = state.value.playlistVideos)
                    }
                }
                if(isDownloading){
                    Image(
                        painter = painterResource(id = R.drawable.loading_img),
                        contentDescription = "loading",
                        modifier = Modifier
                            .height(400.dp)
                            .width(400.dp)
                    )
                }
            }
            Button(
                onClick = {
                    viewModel.onEvent(YoutubeUiEvent.downloadButtonClicked)
                },
                enabled = !isDownloading
            ) {
                Text("Download Selected Streams/Videos")
            }

            if (downloadStatus != 0) {
                if (downloadStatus == 1) {
                    Text("FILE(S) SUCCESSFULLY DOWNLOADED ")
                } else {
                    Text("ERROR!! PLEASE TRY AGAIN")
                }
            }else if(isDownloading){
                Text("PLEASE WAIT FILE IS BEING DOWNLOADING")
            }
        }
        else if(isFetchingStreams){
            Image(
                painter = painterResource(id = R.drawable.loading_img),
                contentDescription = "loading",
                modifier = Modifier
                    .height(400.dp)
                    .width(400.dp)
            )
        }
    }
}
