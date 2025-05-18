package com.example.traviews.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.traviews.R
import com.example.traviews.application.MainActivity
import com.example.traviews.data.local.AuthTokenRepositoryImpl
import com.example.traviews.model.LoginRequest
import com.example.traviews.network.TraviewsApi
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        val tvEntrar = findViewById<TextView>(R.id.btnEntrar)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmail)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)

        tvCadastrar.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        tvEntrar.setOnClickListener {
            if (edtEmail.text.toString().trim().isEmpty() || edtPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(applicationContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(edtEmail.text.toString(), edtPassword.text.toString())
        }
    }

    fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = TraviewsApi.retrofitService.login(LoginRequest(email, password))
                AuthTokenRepositoryImpl.save(response.token)

                val intent = Intent(this@LoginActivity, MainActivity::class.java )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                println("Erro: ${e.message}")
            }
        }
    }

}