package com.kelompok3.musicplayer.API

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


    fun convertVideoToAudio(videoId: String, onAudioUrlReady: (String) -> Unit, onDurationReady:(String)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://youtube-to-mp4-mp3.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(MP4ToMP3::class.java)
        val youtubeUrl = "https://youtu.be/$videoId"
        apiService.convertToMP3(videoId).enqueue(object : Callback<MP3List> {
            override fun onResponse(call: Call<MP3List>, response: Response<MP3List>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val mp3List = result.mp3
                    onDurationReady(result.duration)

                    if (mp3List.isNotEmpty()) {
                        val audioUrl = mp3List[0].url
                        // Panggil onAudioUrlReady dan kirimkan URL audio ke pemanggil
                        onAudioUrlReady(audioUrl)
                    } else {
                        Log.e("OnlineMusicFragment", "No mp3 URL found in the response")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OnlineMusicFragment", "Failed to convert video to audio: $errorBody")
                }
            }

            override fun onFailure(call: Call<MP3List>, t: Throwable) {
                Log.e("OnlineMusicFragment", "Failed to convert video to audio", t)
            }
        })
    }

