package com.example.traviews.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.traviews.R
import com.example.traviews.model.SignUpRequest
import com.example.traviews.network.TraviewsApi
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SingUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        val tvEntrar = findViewById<TextView>(R.id.tvEntrar)
        val btnSignUp = findViewById<AppCompatButton>(R.id.btnSignUp)
        val edtName = findViewById<TextInputEditText>(R.id.edtName)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmail)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)

        tvEntrar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener {
            if (
                edtName.text.toString().trim().isEmpty() ||
                edtEmail.text.toString().trim().isEmpty() ||
                edtPassword.text.toString().trim().isEmpty()
                ) {
                Toast.makeText(applicationContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUp(edtName.text.toString(), edtEmail.text.toString(), edtPassword.text.toString())
        }
    }

    fun signUp(name: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = TraviewsApi.retrofitService.signUp(SignUpRequest(name, email, password))

                Toast.makeText(applicationContext, "Nova conta cadastrada!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@SingUpActivity, LoginActivity::class.java )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                println("Erro: ${e.message}")
            }
        }
    }

}