package com.kelompok3.musicplayer.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kelompok3.musicplayer.R

class singup : AppCompatActivity() {

    private lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_singup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        val toLogin : TextView = findViewById(R.id.tologin)
        toLogin.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        val signupButton : Button = findViewById(R.id.register_button)
        signupButton.setOnClickListener {
            val confirmPassword: String = findViewById<EditText>(R.id.textconfirmpassword).text.toString()

            val fullname: String = findViewById<EditText>(R.id.textfullname).text.toString()
            val email: String = findViewById<EditText>(R.id.textemail).text.toString()
            val password: String = findViewById<EditText>(R.id.textpassword).text.toString()

            if (confirmPassword.isEmpty()|| fullname.isEmpty() || email.isEmpty() || password.isEmpty() ||confirmPassword!=password){
                Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show()
            }else{
                val isSuccess = db.addUser(fullname, email, password)>0
                if (isSuccess) {
                    val intent = Intent(this, login::class.java)
                    startActivity(intent) }
            }

        }
    }
}