package com.example.traviews.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.traviews.MainActivity
import com.example.traviews.R
import com.example.traviews.ui.screens.feed.FeedActivity

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        val tvEntrar = findViewById<TextView>(R.id.btnEntrar)

        tvCadastrar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        tvEntrar.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java )
            startActivity(intent)
        }
    }
}