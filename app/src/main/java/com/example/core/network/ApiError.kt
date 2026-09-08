package com.example.core.network

import com.squareup.moshi.JsonClass

/**
 * Standard error response model and sealed class for network failures.
 */
@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    val statusCode: Int = 400,
    val message: String = "An unexpected error occurred",
    val error: String? = null,
    val details: Map<String, Any>? = null
)

sealed class ApiError(val userMessage: String, cause: Throwable? = null) : Exception(userMessage, cause) {
    class Network(message: String = "Unable to connect to Somuleco servers. Please check your network connection.") : ApiError(message)
    class Unauthorized(message: String = "Session expired or invalid credentials. Please sign in again.") : ApiError(message)
    class Forbidden(message: String = "You do not have permission to access this creator resource.") : ApiError(message)
    class NotFound(message: String = "Requested resource not found.") : ApiError(message)
    class Server(message: String = "Somuleco server is experiencing issues. Please try again later.") : ApiError(message)
    class Validation(message: String, val fieldErrors: Map<String, String> = emptyMap()) : ApiError(message)
    class Unknown(message: String = "An unexpected error occurred.") : ApiError(message)
}
