package com.somuleco.creator.core.network

import com.somuleco.creator.BuildConfig

/**
 * Configuration for Somuleco Core API (NestJS) and Creator AI API (FastAPI).
 *
 * Base URLs are resolved per Gradle build type via [BuildConfig] fields defined in
 * `app/build.gradle.kts` (`debug` -> local/dev loopback hosts, `release` -> documented
 * production placeholders), so this object is the single source of truth read by the
 * rest of the app rather than a place new URLs get hardcoded.
 *
 * NOTE: this network layer is not yet wired into any real HTTP calls (see Wave 01
 * findings) — the app runs entirely against the in-memory mock repository. This
 * configuration boundary is established ahead of that wiring, not in place of it.
 */
object ApiConfig {
    val NESTJS_CORE_BASE_URL: String = BuildConfig.CORE_API_BASE_URL
    val FASTAPI_CREATOR_AI_BASE_URL: String = BuildConfig.AI_API_BASE_URL

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_SOMULECO_CLIENT = "X-Somuleco-Client"
    const val HEADER_CHANNEL_CONTEXT = "X-Somuleco-Channel-Context"
    
    const val CLIENT_VALUE = "android-creator/0.1.0"
    
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
