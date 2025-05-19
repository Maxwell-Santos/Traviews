package com.example.traviews.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traviews.data.local.AuthTokenRepositoryImpl
import com.example.traviews.model.UserProfileResponse
import com.example.traviews.network.TraviewsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

interface UserProfileUiState {
    data class Success(val userProfile: UserProfileResponse) : UserProfileUiState
    object Error : UserProfileUiState
    object Loading : UserProfileUiState
}

class UserProfileViewModel: ViewModel() {
    private val _userProfileUiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val userProfileUiState: StateFlow<UserProfileUiState> = _userProfileUiState

    init {
        getUserProfile()
    }

    fun logout() {
        viewModelScope.launch {
            AuthTokenRepositoryImpl.clear()
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            _userProfileUiState.value = UserProfileUiState.Loading
            _userProfileUiState.value = try {
                val userProfile = TraviewsApi.retrofitService.getUserProfile()
                UserProfileUiState.Success(userProfile)
            } catch (e: IOException) {
                UserProfileUiState.Error
            } catch (e: HttpException) {
                println(e.message())
                UserProfileUiState.Error
            }
        }
    }
}