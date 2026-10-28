package com.example.movies.core.di

import android.content.Context
import com.example.movies.BuildConfig
import com.example.movies.core.util.ConnectivityObserver
import com.example.movies.core.util.NetworkConnectivityObserver
import com.example.movies.data.remote.AuthInterceptor
import com.example.movies.data.remote.TmdbApi
import com.example.movies.data.repository.MovieRepositoryImpl
import com.example.movies.domain.repository.MovieRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val movieRepository: MovieRepository
    val connectivityObserver: ConnectivityObserver
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor(BuildConfig.TMDB_ACCESS_TOKEN)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(TmdbApi.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbApi: TmdbApi by lazy {
        retrofit.create(TmdbApi::class.java)
    }

    override val movieRepository: MovieRepository by lazy {
        MovieRepositoryImpl(tmdbApi)
    }

    override val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }
}
