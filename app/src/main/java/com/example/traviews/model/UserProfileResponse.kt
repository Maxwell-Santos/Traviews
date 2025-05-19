package com.example.traviews.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse (
    val email: String,
    val name: String,
    @SerialName("created_at")
    val createdAt: String
)