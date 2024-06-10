package com.kelompok3.musicplayer.API



data class Mp3(
    val size: String,
    val hasAudio: Boolean,
    val itag: Int,
    val quality: String,
    val codec: String,
    val url: String
)

data class MP3List(
    val title: String,
    val thumbnail: String,
    val duration: String,
    val mp3: List<Mp3>
)

data class Mp3ConversionResponse(
    val status: String,
    val title: String,
    val link: String,
    val size: String,
    val quality: String,
    val expireTime: Long
)

data class YTSongURL(
    val status: String,
    val title: String,
    val channel: String,
    val duration: String,
    val thumbnail: String,
    val url: String
)

//data class YoutubeMp3Response(
//    val result: List<YTSongURL>
//)
//
//data class Song (
//    val type: String,
//    val videoId: String,
//    val name: String,
//    val artist: Artist,
//    val duration: Int,
//    val thumbnails: List<Thumbnail>
//)
//
//data class Artist(
//    val name: String,
//    val artistId: String
//)
//
//data class Thumbnail(
//    val url: String,
//    val width: Int,
//    val height: Int
//)


// YouTubeResponse.kt
data class YouTubeResponse(
    val items: List<VideoItem>
)

data class VideoItem(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(
    val videoId: String
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)

data class Thumbnails(
    val default: ThumbnailYT,
    val medium: ThumbnailYT,
    val high: ThumbnailYT
)

data class ThumbnailYT(
    val url: String
)








