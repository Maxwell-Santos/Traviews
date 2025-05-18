package com.example.traviews.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class VisitCosts (
    val food: Double?,
    val accommodation: Double?,
    val entertainment: Double?
)

@Serializable
data class PublishPostRequest(
    val date: String,
    val medias: List<String>,
    val description: String,
    @Contextual val visitCosts: VisitCosts,
)