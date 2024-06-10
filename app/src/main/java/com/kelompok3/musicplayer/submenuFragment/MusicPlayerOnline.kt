package com.kelompok3.musicplayer.submenuFragment

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

import com.kelompok3.musicplayer.API.MP3List
import com.kelompok3.musicplayer.API.MP4ToMP3
import com.kelompok3.musicplayer.API.VideoItem
import com.kelompok3.musicplayer.R
import com.kelompok3.musicplayer.handleMusicLocal.Music
import com.kelompok3.musicplayer.handleMusicLocal.MyMediaPlayer
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MusicPlayerOnline.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPlayerOnline : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var playButton: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var prevBtn: ImageView
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var imageView: ImageView
    private lateinit var musicFiles: List<VideoItem>
    private lateinit var currentTime: TextView
    private var currentMusicIndex: Int = -1
    private val handler = Handler(Looper.getMainLooper())

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    seekBar.progress = it.currentPosition
                    currentTime.text = formatTime(it.currentPosition)
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun convertVideoToAudio(videoId: String,
                            onAudioUrlReady: (String) -> Unit) {
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
//                    onDurationReady(result.duration)

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_music_player_online, container, false)

        val titleTextView: TextView = view.findViewById(R.id.music_title)
        val artistTextView: TextView = view.findViewById(R.id.artist_name)
        val durationTextView: TextView = view.findViewById(R.id.end_time)
        currentTime = view.findViewById(R.id.start_time)
        imageView = view.findViewById(R.id.album_art)
        seekBar = view.findViewById(R.id.progress_bar)

        playButton = view.findViewById(R.id.play_pause_button)
        nextBtn = view.findViewById(R.id.next_button)
        prevBtn = view.findViewById(R.id.prev_button)


        val bundle = arguments
        if (bundle != null) {
            val title = bundle.getString("title")
            val artist = bundle.getString("artist")
            val videoId = bundle.getString("videoId")
//            val duration = bundle.getString("duration")
            val path = bundle.getString("path")
            val image = bundle.getString("image")
            currentMusicIndex = bundle.getInt("currentMusicIndex")
            musicFiles = bundle.getSerializable("musicFiles") as List<VideoItem>

            titleTextView.text = title
            artistTextView.text = artist
//            durationTextView.text = duration
            val imageUrl = image
            if (!imageUrl.isNullOrEmpty()) {
                // Menggunakan Picasso untuk memuat gambar thumbnail ke ImageView
                Picasso.get()
                    .load(imageUrl)
                    .into(imageView)
            }
            val url = videoId!!
            convertVideoToAudio(url){audiourl->
                mediaPlayer = MyMediaPlayer.getInstance().apply {
                    if (isPlaying) {
                        stop()
                    }
                    reset()
                    setDataSource(audiourl)
                    prepare()
                    setOnPreparedListener {
                        seekBar.max = it.duration
                        seekBar.progress = it.currentPosition
                        it.start()
                        currentTime.text = formatTime(it.currentPosition)
                        handler.post(updateSeekBarRunnable)
                    }
                    setOnCompletionListener {
                        playNextMusic()
                    }
                    start()
                }
            }


            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    handler.removeCallbacks(updateSeekBarRunnable)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mediaPlayer?.seekTo(seekBar?.progress ?: 0)
                    handler.post(updateSeekBarRunnable)
                }
            })

            playButton.setOnClickListener {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        playButton.setImageResource(R.drawable.play_icon)
                        it.pause()
                        seekBar.progress = it.currentPosition
                        handler.post(updateSeekBarRunnable)

                    } else {
                        playButton.setImageResource(R.drawable.pause_icon)
                        it.start()
                        seekBar.progress = it.currentPosition
                        handler.post(updateSeekBarRunnable)

                    }
                }
            }

            nextBtn.setOnClickListener {
                playNextMusic()
            }

            prevBtn.setOnClickListener {
                playPrevMusic()
            }
        }

        return view

    }


    private fun playNextMusic() {
        if (currentMusicIndex < musicFiles.size - 1) {
            currentMusicIndex++
        } else {
            currentMusicIndex = 0
        }
        val nextMusic = musicFiles[currentMusicIndex]
        playButton.setImageResource(R.drawable.pause_icon)
        updateMusicPlayer(nextMusic)
    }

    private fun playPrevMusic() {
        if (currentMusicIndex > 0) {
            currentMusicIndex--
        } else {
            currentMusicIndex = musicFiles.size - 1
        }
        val prevMusic = musicFiles[currentMusicIndex]
        playButton.setImageResource(R.drawable.pause_icon)
        updateMusicPlayer(prevMusic)
    }

    fun updateMusicPlayer(music: VideoItem) {

        val url = music.id.videoId
        convertVideoToAudio(url) { audiourl ->
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                setDataSource(audiourl)
                prepare()
                setOnPreparedListener {
                    seekBar.max = it.duration
                    seekBar.progress = it.currentPosition
                    it.start()
                    currentTime.text = formatTime(it.currentPosition)
                    handler.post(updateSeekBarRunnable)
                }
            }
        }
        view?.findViewById<TextView>(R.id.music_title)?.text = music.snippet.title
        view?.findViewById<TextView>(R.id.artist_name)?.text = music.snippet.channelTitle
//        view?.findViewById<TextView>(R.id.end_time)?.text = music.duration

        val albumArt = music.snippet.thumbnails.default.url
        val imageUrl = albumArt
        if (!imageUrl.isNullOrEmpty()) {
            // Menggunakan Picasso untuk memuat gambar thumbnail ke ImageView
            Picasso.get()
                .load(imageUrl)
                .into(imageView)
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getAlbumArt(filePath: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val albumArt = retriever.embeddedPicture
        retriever.release()
        return albumArt



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicPlayerOnline.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicPlayerOnline().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}