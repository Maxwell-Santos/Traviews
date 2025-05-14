package com.example.traviews.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val simbolo: String,
    val nome: String,
    @SerialName("tipo_moeda")
    val tipoMoeda: String
)