package com.example.traviews.data.local

import android.content.Context

object AuthTokenRepositoryImpl : AuthTokenRepository {

    private var localDataSource: AuthLocalDataSource? = null

    fun initialize(context: Context) {
        if (localDataSource == null) {
            localDataSource = AuthLocalDataSource(context.applicationContext)
        }
    }

    private fun getDataSource(): AuthLocalDataSource {
        return localDataSource
            ?: throw IllegalStateException("AuthTokenRepositoryImpl não foi inicializado. Chame initialize() primeiro.")
    }

    override suspend fun save(token: String) {
        getDataSource().saveToken(token)
    }

    override suspend fun get(): String? {
        return getDataSource().getToken()
    }

    override suspend fun clear() {
        getDataSource().clearToken()
    }
}