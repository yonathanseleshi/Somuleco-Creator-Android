package com.example.core.network

/**
 * Configuration for Somuleco Core API (NestJS) and Creator AI API (FastAPI).
 */
object ApiConfig {
    const val NESTJS_CORE_BASE_URL = "https://api.somuleco.internal/v1/"
    const val FASTAPI_CREATOR_AI_BASE_URL = "https://ai.somuleco.internal/v1/"
    
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_SOMULECO_CLIENT = "X-Somuleco-Client"
    const val HEADER_CHANNEL_CONTEXT = "X-Somuleco-Channel-Context"
    
    const val CLIENT_VALUE = "android-creator/0.1.0"
    
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
