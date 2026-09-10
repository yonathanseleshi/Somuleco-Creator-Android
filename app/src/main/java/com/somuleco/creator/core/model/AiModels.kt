package com.somuleco.creator.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Canonical AI + recommendation objects — contract §3.22-3.25. See
 * `Common.kt` for shared conventions.
 */

enum class AIConversationType {
    @Json(name = "creator_chat") CREATOR_CHAT,
    @Json(name = "content_strategy") CONTENT_STRATEGY,
    @Json(name = "business_strategy") BUSINESS_STRATEGY,
    @Json(name = "product_strategy") PRODUCT_STRATEGY,
    @Json(name = "analytics") ANALYTICS,
    @Json(name = "onboarding") ONBOARDING
}

/** Contract §3.22. */
@JsonClass(generateAdapter = true)
data class AIConversation(
    val id: String,
    val creatorAccountId: String,
    val userId: String,
    val conversationType: AIConversationType,
    val title: String?,
    val status: String,
    val contextScope: String?,
    val startedAt: String,
    val lastMessageAt: String?,
    val createdAt: String
)

enum class AIMessageRole {
    @Json(name = "user") USER,
    @Json(name = "assistant") ASSISTANT,
    @Json(name = "system") SYSTEM,
    @Json(name = "tool") TOOL
}

/** Contract §3.23. */
@JsonClass(generateAdapter = true)
data class AIMessage(
    val id: String,
    val conversationId: String,
    val role: AIMessageRole,
    val content: String,
    val modelReference: String?,
    val metadata: Map<String, Any?>?,
    val createdAt: String
)

enum class AIArtifactType {
    @Json(name = "post_draft") POST_DRAFT,
    @Json(name = "product_draft") PRODUCT_DRAFT,
    @Json(name = "channel_plan") CHANNEL_PLAN,
    @Json(name = "content_plan") CONTENT_PLAN,
    @Json(name = "recommendation") RECOMMENDATION,
    @Json(name = "analysis") ANALYSIS,
    @Json(name = "course_outline") COURSE_OUTLINE
}

/**
 * Contract §3.24 — represents AI output before it becomes an authoritative
 * Creator object. Per the data model inventory's "AI Data Boundary": once
 * accepted, the path is `AIArtifact -> NestJS API -> ContentItem` (or
 * `CreatorProduct`, etc.) - `persistedResourceType`/`persistedResourceId`
 * record that outcome. FastAPI must never write directly to Creator
 * business tables.
 */
@JsonClass(generateAdapter = true)
data class AIArtifact(
    val id: String,
    val aiJobId: String,
    val artifactType: AIArtifactType,
    val title: String?,
    val content: String?,
    val structuredContent: Map<String, Any?>?,
    val status: String,
    val persistedResourceType: String?,
    val persistedResourceId: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class CreatorRecommendationStatus {
    @Json(name = "active") ACTIVE,
    @Json(name = "dismissed") DISMISSED,
    @Json(name = "accepted") ACCEPTED,
    @Json(name = "completed") COMPLETED,
    @Json(name = "expired") EXPIRED
}

/** Contract §3.25. */
@JsonClass(generateAdapter = true)
data class CreatorRecommendation(
    val id: String,
    val creatorAccountId: String,
    val recommendationType: String,
    val title: String,
    val explanation: String?,
    val confidence: Double?,
    val priority: String?,
    val supportingData: Map<String, Any?>?,
    val actionType: String?,
    val actionReference: String?,
    val status: CreatorRecommendationStatus,
    val generatedAt: String,
    val expiresAt: String?,
    val actedOnAt: String?
)
