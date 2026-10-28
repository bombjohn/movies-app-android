package com.example.movies.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Append default language query parameter if not present
        val urlBuilder = originalUrl.newBuilder()
        if (originalUrl.queryParameter("language") == null) {
            urlBuilder.addQueryParameter("language", "es-ES")
        }

        // If token looks like a standard v3 API key (32 hex chars), add as query param too,
        // otherwise if it's a Bearer JWT, add via Authorization header.
        val isApiKey = token.isNotBlank() && token.length <= 40 && !token.contains(".")
        if (isApiKey && originalUrl.queryParameter("api_key") == null) {
            urlBuilder.addQueryParameter("api_key", token)
        }

        val requestBuilder = originalRequest.newBuilder()
            .url(urlBuilder.build())

        if (token.isNotBlank() && !isApiKey) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        requestBuilder.header("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
