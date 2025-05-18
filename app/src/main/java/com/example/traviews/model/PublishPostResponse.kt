package com.example.traviews.model

import kotlinx.serialization.Serializable

@Serializable
data class FileToUpload(
    val filePath: String,
    val pathToUpload: String
)

@Serializable
data class PublishPostResponse(
 val id: String,
    val filesToUpload: List<FileToUpload>
)