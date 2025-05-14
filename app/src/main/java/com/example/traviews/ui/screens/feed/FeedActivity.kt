package com.example.traviews.ui.screens.feed

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.traviews.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FeedActivity: AppCompatActivity() {
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_feed)
        val tvCEP = findViewById<TextView>(R.id.tvCEP)

        lifecycleScope.launch {
            viewModel.feedUiState.collect { state ->
                when (state) {
                    is FeedUiState.Loading -> {
                        tvCEP.text = "Carregando CEP"
                    }
                    is FeedUiState.Success -> {
                        tvCEP.text = state.photos
                    }
                    is FeedUiState.Error -> {
                        tvCEP.text = "Falha ao carregar o cep"
                    }
                }
            }
        }
    }
}