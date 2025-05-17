package com.example.traviews.data.local

interface AuthTokenRepository {
    suspend fun save(token: String)
    suspend fun get(): String?
    suspend fun clear()
}
