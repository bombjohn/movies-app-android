package com.example.movies

import android.app.Application
import com.example.movies.core.di.AppContainer
import com.example.movies.core.di.DefaultAppContainer

class MovieApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
