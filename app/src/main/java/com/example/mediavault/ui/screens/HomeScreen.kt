package com.example.mediavault.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.example.mediavault.ui.model.HomeViewModel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel
) {
    val videoItems by homeViewModel.videoItems.collectAsState()
    // compose will be triggered whenever there is an update
    val selectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        //contract: what we want to do with this activity
        //result will be our chosen video
        onResult ={uri->
            uri?.let(
                homeViewModel :: addVideoUri
            )
        }
    )

    var lifecycle by remember{
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver{ _ ,event->
            lifecycle = event
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier= Modifier
            .fillMaxSize()
    ){
        AndroidView(
            factory ={context->
                PlayerView(context).also{
                    it.player = homeViewModel.player
                }
            },
            //whenever compose state changes below code executes
            update = {
                when(lifecycle){
                    Lifecycle.Event.ON_PAUSE ->{
                        it.onPause()
                        it.player?.pause()
                    }
                    Lifecycle.Event.ON_RESUME ->{
                        it.onResume()
                    }
                    else -> Unit
                }
            }
            ,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        )
        Spacer(modifier= Modifier.height(8.dp))
        Row(
          modifier=Modifier.fillMaxWidth()
        ){
            Text("Select Video : ", Modifier.padding(top =15.dp))
            IconButton(
                onClick = {
                    selectVideoLauncher.launch("video/*")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Select Video"
                )
            }
        }
        Spacer(modifier= Modifier.height(16.dp))
        LazyColumn(
            modifier= Modifier.fillMaxWidth()
        ) {
            items(videoItems){item->
                Text(
                    item.name,
                    modifier= Modifier
                        .fillMaxWidth()
                        .clickable {
                            homeViewModel.playVideo(item.contentUri)
                        }
                        .padding(16.dp)
                )
            }
        }

    }
}