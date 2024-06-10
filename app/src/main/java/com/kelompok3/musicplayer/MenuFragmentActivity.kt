package com.kelompok3.musicplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kelompok3.musicplayer.auth.login
import com.kelompok3.musicplayer.submenuFragment.myMusic
import com.kelompok3.musicplayer.submenuFragment.onlineMusic
import com.kelompok3.musicplayer.submenuFragment.playlist





class MenuFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myMusic())
                .commit()
        }
        val btnMyMusic : LinearLayout = findViewById(R.id.subMenu1)
        btnMyMusic.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, myMusic())
                    .commit()
            }
        }
        val btnOnlineMusic : LinearLayout = findViewById(R.id.subMenu2)
        btnOnlineMusic.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, onlineMusic())
                    .commit()
            }
        }
        val btnPlaylist : LinearLayout = findViewById(R.id.subMenu3)
        btnPlaylist.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, playlist())
                    .commit()
            }
        }
    }

}