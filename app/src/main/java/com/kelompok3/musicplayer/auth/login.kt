package com.kelompok3.musicplayer.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kelompok3.musicplayer.MenuFragmentActivity
import com.kelompok3.musicplayer.R




class login : AppCompatActivity() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        handleLogin()
        val toSingUp : TextView = findViewById(R.id.tosingup)
        toSingUp.setOnClickListener {
            val intent = Intent(this, singup::class.java)
            startActivity(intent)
        }
    }
    private fun handleLogin() {
        db = DBHelper(this)
        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {

            val email: String = findViewById<EditText>(R.id.textemail).text.toString()
            val password: String = findViewById<EditText>(R.id.textpassword).text.toString()
            if (db.checkUser(email, password) && !email.isEmpty() && !password.isEmpty()){
                val intent = Intent(this, MenuFragmentActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}