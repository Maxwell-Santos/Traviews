package com.example.traviews.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traviews.network.TraviewsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface FeedUiState {
    data class Success(val photos: String) : FeedUiState
    object Error : FeedUiState
    object Loading : FeedUiState
}

class FeedViewModel : ViewModel() {

    private val _feedUiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val feedUiState: StateFlow<FeedUiState> = _feedUiState

    init {
        getPosts()
    }

    private fun getPosts() {
        viewModelScope.launch {
            _feedUiState.value = FeedUiState.Loading
            _feedUiState.value = try {
                val listResult = TraviewsApi.retrofitService.getPosts().toString()
                println(listResult)
                FeedUiState.Success(listResult)
            } catch (e: IOException) {
                FeedUiState.Error
            } catch (e: HttpException) {
                FeedUiState.Error
            }
        }
    }
}
