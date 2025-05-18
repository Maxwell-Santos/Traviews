package com.example.traviews.model
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)
