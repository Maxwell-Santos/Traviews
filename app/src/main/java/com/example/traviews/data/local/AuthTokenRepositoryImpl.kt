package com.example.traviews.data.local

import android.content.Context
import android.util.Base64
import org.json.JSONObject

fun decodeJwt(jwt: String): JSONObject? {
    return try {
        val parts = jwt.split(".")
        if (parts.size != 3) return null

        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val decodedString = String(decodedBytes, Charsets.UTF_8)
        JSONObject(decodedString)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

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

    suspend fun getUserId(): String {
        val token = getDataSource().getToken() ?: ""
        var userId = ""

        if (!token.isEmpty()) {
            val payload = decodeJwt(token)

            payload?.let {
                userId = it.getString("sub")
            }
        }

        return userId
    }

    override suspend fun clear() {
        getDataSource().clearToken()
    }
}