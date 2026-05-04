package com.kash.data.remote.api

import com.kash.data.local.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefs: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip auth header for login endpoint
        if (request.url.pathSegments.any { it == "login" }) {
            return chain.proceed(request)
        }

        val token = runBlocking { prefs.session.firstOrNull()?.token }

        return chain.proceed(
            if (token != null)
                request.newBuilder().addHeader("Authorization", "Bearer $token").build()
            else
                request
        )
    }
}
