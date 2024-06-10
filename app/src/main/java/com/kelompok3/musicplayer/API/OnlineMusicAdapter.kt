package com.kelompok3.musicplayer.API


// TrackAdapter.kt
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.kelompok3.musicplayer.R
import com.kelompok3.musicplayer.handleMusicLocal.MyMediaPlayer
import com.kelompok3.musicplayer.submenuFragment.MusicPlayerOnline
import com.kelompok3.musicplayer.submenuFragment.musicPlayer
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OnlineMusicAdapter(
    private val trackList: List<VideoItem>,
    private val listener: (VideoItem) -> Unit,
    private val activity: FragmentActivity

) : RecyclerView.Adapter<OnlineMusicAdapter.TrackViewHolder>() {

    private lateinit var path :String
    private lateinit var durationSong :String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track, listener)
    }

    override fun getItemCount(): Int = trackList.size

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.music_title)
        private val artistTextView: TextView = itemView.findViewById(R.id.music_artist)
        private val duration: TextView = itemView.findViewById(R.id.music_duration)
        private val coverImageView: ImageView = itemView.findViewById(R.id.music_image)

        fun bind(track: VideoItem, listener: (VideoItem) -> Unit) {

            val shortenedTitle = if (track.snippet.title.length > 18) {
                track.snippet.title.substring(0, 18) + "..."
            } else {
                track.snippet.title
            }

            titleTextView.text = shortenedTitle
            artistTextView.text = track.snippet.channelTitle
//            duration.text = track..toString()

            val imageUrl = track.snippet.thumbnails.default.url
            if (!imageUrl.isNullOrEmpty()) {
                // Menggunakan Picasso untuk memuat gambar thumbnail ke ImageView
                Picasso.get()
                    .load(imageUrl)
                    .into(coverImageView)
            }
            itemView.setOnClickListener {
                val id = track.id.videoId

                listener(track)

                val fragment = MusicPlayerOnline()
                val bundle = Bundle().apply {
                    putString("title", track.snippet.title)
                    putString("artist", track.snippet.channelTitle)
                    putString("videoId", id)
                    putString("image", track.snippet.thumbnails.high.url)
                    putInt("currentMusicIndex", adapterPosition)
                    putSerializable("musicFiles", ArrayList(trackList))
                }
                fragment.arguments = bundle

                // Stop and release the MediaPlayer if it's already playing
                MyMediaPlayer.getInstance().apply {
                    if (isPlaying) {
                        stop()
                    }
                    reset()
                }

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
            }
        }

}


