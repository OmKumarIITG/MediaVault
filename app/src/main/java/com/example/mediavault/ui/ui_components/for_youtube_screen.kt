package com.example.mediavault.ui.ui_components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mediavault.R
import com.example.mediavault.ui.model.YoutubeViewModel.Stream
import com.example.mediavault.ui.model.YoutubeViewModel.VideoMetadata
import com.example.mediavault.ui.model.YoutubeViewModel.YoutubeUiEvent
import com.example.mediavault.ui.model.YoutubeViewModel.YoutubeViewModel

//composable for playlist
@Composable
fun PlaylistColumn(
    videos : List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            buildAnnotatedString {
                append("Total ")
                withStyle(
                    style = SpanStyle(
                        color = Color.Red,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    )
                ){
                    append(videos.size.toString())
                }
                append(" videos found")
            },
            fontWeight = FontWeight.Black,
            modifier= Modifier.padding(vertical = 10.dp)
        )
        videos.forEachIndexed { index,videoUrl->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ){
                Text(
                    "${index+1}.   $videoUrl",
                    modifier=Modifier.padding(5.dp)
                )
            }
        }
    }
}

//composable for streams
@Composable
fun StreamList(
    viewModel : YoutubeViewModel
) {
    val state = viewModel.uiState.collectAsState()
    val streams = state.value.streams
    val videos = streams.filter {
        it.type == 1
    }
    val metadata = state.value.metadata

    val audio = streams - videos.toSet()
    LazyColumn {
        item{
            MetadataBlock(metadata = metadata)
            Spacer(modifier = Modifier.height(3.dp))
        }
        item{
            Divider(
                thickness = 2.dp,
                color = Color.Blue
            )
            Text(
                "Video Files Available",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color =  Color.White
            )
            Divider(
                thickness = 2.dp,
                color = Color.Blue
            )
            Header(true)
        }
        items(videos){video->

            val selectedStreamItag = state.value.streamToDownload.itag
            StreamRow(stream = video, viewModel = viewModel,selectedStreamItag == video.itag,true)
        }
        item{
            Divider(
                thickness = 2.dp,
                color = Color.Blue
            )
            Text(
                "Audio Files Available",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color =  Color.White
            )
            Divider(
                thickness = 2.dp,
                color = Color.Blue
            )
            Header(false)
        }
        items(audio.toList()){audio->
            val selectedStreamItag = state.value.streamToDownload.itag
            StreamRow(stream = audio, viewModel = viewModel,selectedStreamItag == audio.itag,false)
        }

    }
}

@Composable
fun StreamRow(
    stream: Stream,
    viewModel : YoutubeViewModel,
    selected : Boolean,
    isVideo : Boolean
) {
    Row (
        modifier= Modifier
            .fillMaxWidth()
    ){
        val unit = if(isVideo)"p" else "kbps"
        Text(stream.resOrBitrate.toString()+unit,
            Modifier
                .weight(0.2f)
                .padding(start = 5.dp, top = 15.dp))
        Text(stream.fileSize+" MB",
            Modifier
                .weight(0.3f)
                .padding(top = 15.dp))
        Text(stream.fps,
            Modifier
                .weight(0.1f)
                .padding(top = 15.dp))
        Text(stream.codec,
            Modifier
                .weight(0.25f)
                .padding(top = 15.dp))
        RadioButton(selected = selected , onClick = {
            viewModel.onEvent(YoutubeUiEvent.radioButtonClicked(stream))},Modifier.weight(0.15f))
    }
}

@Composable
fun Header(isVideo : Boolean = true) {
    Row (
        modifier= Modifier
            .height(15.dp)
            .background(Color.Yellow)
    ){
        Text(
            if(isVideo)"QUALITY"
            else "ABR(avg.bitrate)",
            Modifier
                .weight(0.25f)
                .padding(start = 5.dp),
            fontSize = 10.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )
        Text("SIZE",
            Modifier
                .weight(0.25f),
            fontSize = 10.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )
        Text("FPS",
            Modifier
                .weight(0.1f),
            fontSize = 10.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )
        Text("CODEC",
            Modifier
                .weight(0.2f),
            fontSize = 10.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Bold
        )
        Text(
            "",
            Modifier
                .weight(0.2f)
        )
    }
    Divider(
        thickness = 1.dp,
        color = Color.Black
    )
}

@Composable
fun MetadataBlock(
    metadata: VideoMetadata
) {
    val url = metadata.thumbnailUrl
    val title = metadata.title
    val author = metadata.author
    val views = metadata.views

    Column(
        modifier = Modifier.fillMaxHeight(0.4f)
    ){
        Text(
            title.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            modifier =Modifier.padding(bottom = 2.dp).fillMaxWidth()
        )
        Box{
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = "video_desc",
                contentScale = ContentScale.Inside, //to fill whole screen vertically and horizontally both
                error = painterResource(id = R.drawable.ic_broken_image),//in case image failed to load
                placeholder = painterResource(id = R.drawable.loading_img)//while its loaded , its shown
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text("Channel: ${author.toString()}")
            Text("Views: ${views.toString()}")
        }
    }

}
