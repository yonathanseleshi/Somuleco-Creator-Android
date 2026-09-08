package com.example.data.model

data class CreatorRecommendation(
    val id: String,
    val title: String,
    val reason: String,
    val supportingMetric: String,
    val actionLabel: String,
    val actionCategory: String, // "CONTENT", "PRODUCT", "COMMUNITY"
    val isDismissed: Boolean = false
)

enum class AIMessageRole {
    USER, ASSISTANT, SYSTEM
}

data class AIMessage(
    val id: String,
    val role: AIMessageRole,
    val content: String,
    val timestamp: String,
    val artifactTitle: String? = null,
    val artifactType: String? = null,
    val artifactContent: String? = null
) {
    val isUser: Boolean get() = role == AIMessageRole.USER
    val text: String get() = content
}

data class AIArtifact(
    val id: String,
    val title: String,
    val artifactType: String, // "POST_DRAFT", "PRODUCT_CONCEPT", "CONTENT_CALENDAR"
    val content: String,
    val channelId: String = "ch_1",
    val channelName: String = "Photography Masterclass"
)
