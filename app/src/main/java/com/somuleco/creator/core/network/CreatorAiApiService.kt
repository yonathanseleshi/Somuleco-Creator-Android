package com.somuleco.creator.core.network

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// -----------------------------------------------------------------------------
// DTOs for FastAPI Creator AI Service
// -----------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class AiChatRequest(
    val message: String,
    val channelContextId: String? = null,
    val previousMessages: List<AiChatMessageDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AiChatMessageDto(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String? = null
)

@JsonClass(generateAdapter = true)
data class AiChatResponse(
    val reply: String,
    val suggestedActions: List<String> = emptyList(),
    val confidenceScore: Float = 0.95f
)

@JsonClass(generateAdapter = true)
data class ContentIdeaGenerationRequest(
    val channelCategory: String,
    val channelName: String,
    val currentAudienceInterest: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentIdeaGenerationResponse(
    val ideas: List<AiContentIdeaDto>
)

@JsonClass(generateAdapter = true)
data class AiContentIdeaDto(
    val title: String,
    val description: String,
    val recommendedFormat: String,
    val targetAudience: String
)

// -----------------------------------------------------------------------------
// FastAPI Creator AI Retrofit Service
// -----------------------------------------------------------------------------

interface CreatorAiApiService {

    @POST("chat")
    suspend fun sendChatMessage(@Body request: AiChatRequest): Response<AiChatResponse>

    @POST("generate-ideas")
    suspend fun generateContentIdeas(@Body request: ContentIdeaGenerationRequest): Response<ContentIdeaGenerationResponse>
}
