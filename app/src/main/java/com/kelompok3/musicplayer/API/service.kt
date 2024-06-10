package com.kelompok3.musicplayer.API
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface YouTubeApiService {
    @GET("search")
    fun searchVideos(
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String

    ): Call<YouTubeResponse>
}


//interface  ConvertToAudioAPi{
//    @Headers(
//        "X-RapidAPI-Key: b102c0d060msh769f61d795771dap176a80jsnb4bfd56c1610",
//        "X-RapidAPI-Host: youtube-mp315.p.rapidapi.com"
//    )
//    @GET("/")
//    fun convertToAudio(
//        @Query("url") query: String
//    ): Call<YoutubeMp3Response>
//}


interface MP4ToMP3{
    @Headers(
        "X-RapidAPI-Key: b102c0d060msh769f61d795771dap176a80jsnb4bfd56c1610",
        "X-RapidAPI-Host: youtube-to-mp4-mp3.p.rapidapi.com"
    )
    @GET("audio-info/{id}")
    fun convertToMP3(
        @Path("id") id: String
    ): Call<MP3List>
}

interface  ConvertToAudioAPi{
    @Headers(
        "X-RapidAPI-Key: b102c0d060msh769f61d795771dap176a80jsnb4bfd56c1610",
        "X-RapidAPI-Host: youtube-mp3-download1.p.rapidapi.com"
    )
    @GET("dl")
    fun convertToAudio(
        @Query("id") query: String
    ): Call<Mp3ConversionResponse>
}