import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelompok3.musicplayer.R
import com.kelompok3.musicplayer.handleMusicLocal.Music
import com.kelompok3.musicplayer.handleMusicLocal.MyMediaPlayer
import com.kelompok3.musicplayer.submenuFragment.musicPlayer

class MusicAdapter(private val musicList: List<Music>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    companion object {
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(musicList[position])
    }

    override fun getItemCount() = musicList.size

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.music_image)
        private val titleTextView: TextView = itemView.findViewById(R.id.music_title)
        private val artistTextView: TextView = itemView.findViewById(R.id.music_artist)
        private val durationTextView: TextView = itemView.findViewById(R.id.music_duration)

        fun bind(music: Music) {
            val shortenedTitle = if (music.title.length > 18) {
                music.title.substring(0, 18) + "..."
            } else {
                music.title
            }

            imageView.setImageResource(R.drawable.bgmain)
            titleTextView.text = shortenedTitle
            artistTextView.text = music.artist
            durationTextView.text = music.duration

            val currentPath: String = music.path.toString()

            val albumArt = getAlbumArt(currentPath)
            if (albumArt != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.bgmain)
            }

            itemView.setOnClickListener {

                val fragment = musicPlayer()
                val bundle = Bundle().apply {
                    putString("title", music.title)
                    putString("artist", music.artist)
                    putString("duration", music.duration)
                    putString("path", music.path)
                    putInt("currentMusicIndex", adapterPosition)
                    putSerializable("musicFiles", ArrayList(musicList))
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
        private fun getAlbumArt(filePath: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val albumArt = retriever.embeddedPicture
            retriever.release()
            return albumArt
        }
    }
}
