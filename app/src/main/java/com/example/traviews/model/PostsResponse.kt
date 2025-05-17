package com.example.traviews.model

import kotlinx.serialization.Serializable

@Serializable
data class PostsResponse(
    val data: List<Post>,
    val cursor: String?,
    var hasMore: Boolean
)
