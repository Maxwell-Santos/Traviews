package com.example.traviews.model
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)
