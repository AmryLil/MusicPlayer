package com.kelompok3.musicplayer.handleMusicLocal

import android.media.MediaPlayer

class MyMediaPlayer {
    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun getInstance(): MediaPlayer {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
            return mediaPlayer!!
        }
    }
}