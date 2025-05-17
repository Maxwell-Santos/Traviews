package com.example.traviews.network

import androidx.lifecycle.lifecycleScope
import com.example.traviews.data.local.AuthTokenRepositoryImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.header("No-Auth").toBoolean()) {
            val newRequest = request.newBuilder()
                .removeHeader("No-Auth")
                .build()
            return chain.proceed(newRequest)
        }

        println("Cheguei até aqui e nada")
        val token = runBlocking {
            AuthTokenRepositoryImpl.get() // Agora você pode chamar a suspend fun
        }

        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
