import os
from pytube import YouTube
from pytube import Playlist
from pytube.exceptions import VideoUnavailable

def showStreamsVideo(url):
    print("inside python show stream function")
    try:
        yt = YouTube(url)
        streams = yt.streams
        stream_list = []
        for stream in streams:
            if stream.type == "video":
                resolution = int(stream.resolution.rstrip("p")) if stream.resolution else 0
                filesize = yt.streams.get_by_itag(stream.itag).filesize_mb
                fps = stream.fps if stream.fps else "Unknown"
                codec = stream.video_codec if stream.video_codec else "Unknown"
                stream_list.append((1, stream.itag, resolution, filesize, fps, codec))
            elif stream.type == "audio":
                audio_bitrate = int(stream.abr.rstrip("kbps")) if stream.abr else 0
                filesize = yt.streams.get_by_itag(stream.itag).filesize_mb
                codec = stream.audio_codec if stream.audio_codec else "Unknown"
                stream_list.append((2, stream.itag, audio_bitrate, filesize, "N/A", codec))
        print("stream list:" ,stream_list)
        return stream_list
    except VideoUnavailable:
        print("The video is unavailable.")
        return []

def downloadStreams(url, itag, folder_path):
    try:
        yt = YouTube(url)
        stream = yt.streams.get_by_itag(itag)
        if stream:
            # Ensure the folder exists
            if not os.path.exists(folder_path):
                os.makedirs(folder_path)
            # Download the stream to the specified folder
            stream.download(output_path=folder_path)
            print("Download completed.")
            return True
        else:
            print("Stream with itag", itag, "not found.")
            return False
    except VideoUnavailable:
        print("The video is unavailable.")
        return False
    except Exception as e:
        print(f"An error occurred: {e}")
        return False

def showVideosPlayList(url):
    try:
        yt = Playlist(url)
        return yt.video_urls
    except VideoUnavailable:
        print("The playlist is unavailable.")
        return []

def downloadPlaylist(url,folder_path):
    try:
        yt = Playlist(url)
        for video in yt.videos:
            # Ensure the folder exists
            if not os.path.exists(folder_path):
                os.makedirs(folder_path)
            # Download the stream to the specified folder
            video.streams.get_highest_resolution().download(output_path=folder_path)
            print("Download completed.")
            return True
    except VideoUnavailable:
        print("The video is unavailable.")
        return False
    except Exception as e:
        print(f"An error occurred: {e}")
        return False
