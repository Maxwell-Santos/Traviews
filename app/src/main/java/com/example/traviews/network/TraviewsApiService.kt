package com.example.traviews.network

import com.example.traviews.model.LoginRequest
import com.example.traviews.model.LoginResponse
import com.example.traviews.model.PostsResponse
import com.example.traviews.model.PublishPostRequest
import com.example.traviews.model.PublishPostResponse
import com.example.traviews.model.SignUpRequest
import com.example.traviews.model.SignUpResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.15.2:3000/api/v1/"

val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface TraviewsApiService {

    @POST("auth/login")
    @Headers("No-Auth: true")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users")
    @Headers("No-Auth: true")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @POST("posts/publish")
    @Headers("No-Auth: false")
    suspend fun publishPost(@Body request: PublishPostRequest): PublishPostResponse

    @GET("posts")
    @Headers("No-Auth: false")
    suspend fun getPosts(): PostsResponse
}

object TraviewsApi {
    val retrofitService: TraviewsApiService by lazy {
        retrofit.create(TraviewsApiService::class.java)
    }
}