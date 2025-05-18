package com.example.traviews.application

import android.app.Application
import com.example.traviews.data.local.AuthTokenRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TraviewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AuthTokenRepositoryImpl.initialize(this)
    }
}