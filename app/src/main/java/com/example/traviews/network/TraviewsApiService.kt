package com.example.traviews.network

import com.example.traviews.model.Post
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://brasilapi.com.br/api/cambio/v1/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    interface TraviewsApiService {
        @GET("moedas")
        suspend fun getPosts(): List<Post>
    }

    object TraviewsApi {
        val retrofitService: TraviewsApiService by lazy {
            retrofit.create(TraviewsApiService::class.java)
        }
    }