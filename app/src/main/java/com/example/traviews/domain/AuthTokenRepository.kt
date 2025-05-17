package com.example.traviews.domain

interface AuthTokenRepository {
    suspend fun save(token: String)
    suspend fun get(): String?
    suspend fun clear()
}
