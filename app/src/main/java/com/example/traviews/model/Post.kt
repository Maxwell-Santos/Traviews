package com.example.traviews.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String? = null,
    val userId: String,
    val date: String,
    val likes: Int,
    val medias: List<String>,
    val description: String,
    val foodCost: Int?,
    val accommodationCost: Int?,
    val entertainmentCost: Int?,
    val createdAt: String,
    val updatedAt: String?
)