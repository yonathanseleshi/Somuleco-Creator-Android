package com.somuleco.creator.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interface providing authentication tokens for network calls.
 */
interface AuthTokenProvider {
    fun getAuthToken(): String?
    fun getActiveChannelId(): String?
}

/**
 * Interceptor that appends bearer authorization tokens, client identifier,
 * and active channel context headers to outbound requests.
 */
class AuthInterceptor(
    private val tokenProvider: AuthTokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header(ApiConfig.HEADER_SOMULECO_CLIENT, ApiConfig.CLIENT_VALUE)

        val token = tokenProvider.getAuthToken()
        if (!token.isNullOrBlank()) {
            builder.header(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
        }

        val channelId = tokenProvider.getActiveChannelId()
        if (!channelId.isNullOrBlank()) {
            builder.header(ApiConfig.HEADER_CHANNEL_CONTEXT, channelId)
        }

        return chain.proceed(builder.build())
    }
}
