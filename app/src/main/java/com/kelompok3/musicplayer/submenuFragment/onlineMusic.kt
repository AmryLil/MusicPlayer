package com.kelompok3.musicplayer.submenuFragment
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelompok3.musicplayer.API.ConvertToAudioAPi
import com.kelompok3.musicplayer.API.MP3List
import com.kelompok3.musicplayer.API.MP4ToMP3
import com.kelompok3.musicplayer.API.Mp3
import com.kelompok3.musicplayer.API.Mp3ConversionResponse
//import com.kelompok3.musicplayer.API.DeezerApiResponse

import com.kelompok3.musicplayer.API.OnlineMusicAdapter
import com.kelompok3.musicplayer.API.VideoItem
import com.kelompok3.musicplayer.API.YTSongURL
import com.kelompok3.musicplayer.API.YouTubeApiService
//import com.kelompok3.musicplayer.API.Track

import com.kelompok3.musicplayer.API.YouTubeResponse
import com.kelompok3.musicplayer.R
import com.kelompok3.musicplayer.handleMusicLocal.Music
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class onlineMusic : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: OnlineMusicAdapter
    private var trackList: MutableList<VideoItem> = mutableListOf()
    private var resultConvertList: MutableList<YTSongURL> = mutableListOf()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var VidioID : String
    private lateinit var audioUrl : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_online_music, container, false)
        val fragmentActivity = requireActivity()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        trackAdapter =  OnlineMusicAdapter(trackList, listener = {track -> },fragmentActivity)
        recyclerView.adapter =  activity?.let {trackAdapter}

        val searchEditText :EditText= view.findViewById(R.id.seacrh)
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    fetchTracks(query)
                }

                true
            } else {
                false
            }
        }
        val searchButton :ImageView= view.findViewById(R.id.BTNSeacrh)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                fetchTracks(query)
            }
        }


        return view
    }

    private fun fetchTracks(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apikey = "AIzaSyAsbJgLZKwedkqudAl0gWXIBiLMpmiGTrE"
        val apiService = retrofit.create(YouTubeApiService::class.java)
        apiService.searchVideos("snippet",query,"songs",20,apikey).enqueue(object : Callback<YouTubeResponse> {
            override fun onResponse(call: Call<YouTubeResponse>, response: Response<YouTubeResponse>) {
                Log.d("Response", "Response body: ${response.body()}")
                if (response.isSuccessful && response.body() != null) {
                    trackList.clear()
                    trackList.addAll(response.body()!!.items)
                    trackAdapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(context, "Failed to fetch tracks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch tracks", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun playTrack(audioUrl: String) {
        if (audioUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid audio URL", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("OnlineMusicFragment", "Error playing track", e)
            Toast.makeText(context, "Error playing track", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}






