package com.kelompok3.musicplayer.submenuFragment

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kelompok3.musicplayer.R
import com.kelompok3.musicplayer.handleMusicLocal.Music
import com.kelompok3.musicplayer.handleMusicLocal.MyMediaPlayer
import java.util.concurrent.TimeUnit

class musicPlayer : Fragment() {
    private lateinit var playButton: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var prevBtn: ImageView
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var imageView: ImageView
    private lateinit var musicFiles: List<Music>
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_music_player, container, false)

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
            val duration = bundle.getString("duration")
            val path = bundle.getString("path")
            currentMusicIndex = bundle.getInt("currentMusicIndex")
            musicFiles = bundle.getSerializable("musicFiles") as List<Music>

            titleTextView.text = title
            artistTextView.text = artist
            durationTextView.text = duration

            val currentPath: String = path.toString()

            val albumArt = getAlbumArt(currentPath)
            if (albumArt != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
                imageView.setImageBitmap(bitmap)
            } else {
                // Jika tidak ada gambar album, gunakan gambar default
                imageView.setImageResource(R.drawable.bgmain)
            }

            mediaPlayer = MyMediaPlayer.getInstance().apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                setDataSource(path)
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

    fun updateMusicPlayer(music: Music) {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            setDataSource(music.path)
            prepare()
            setOnPreparedListener {
                seekBar.max = it.duration
                seekBar.progress = it.currentPosition
                it.start()
                currentTime.text = formatTime(it.currentPosition)
                handler.post(updateSeekBarRunnable)
            }
        }
        view?.findViewById<TextView>(R.id.music_title)?.text = music.title
        view?.findViewById<TextView>(R.id.artist_name)?.text = music.artist
        view?.findViewById<TextView>(R.id.end_time)?.text = music.duration

        val albumArt = getAlbumArt(music.path)
        if (albumArt != null) {
            val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
            imageView.setImageBitmap(bitmap)
        } else {
            // Jika tidak ada gambar album, gunakan gambar default
            imageView.setImageResource(R.drawable.bgmain)
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
}
