package com.example.mediavault.ui.model.YoutubeViewModel

data class YoutubeUIState(
    val url : String = "",
    val isFetchingStreams : Boolean = false,
    //for single video
    val streams : Set<Stream> = emptySet(),
    val streamToDownload : Stream = Stream(),
    //for playlist
    val playlistVideos : List<String> = emptyList(),
    //download status
    val downloadResult : Int = 0, // 0 means its at rest  , means no download button clicked
    val isDownloading : Boolean = false
)

data class Stream(
    val type: Int = 1, //1 for video, 2 for audio
    val itag: Int = -1,
    val resOrBitrate: Int = 720,
    val fileSize : String = "N/A",
    val fps :String = "N/A",
    val codec : String = "N/A",
    val isSelected:Boolean = false
)
