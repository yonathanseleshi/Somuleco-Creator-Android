package com.somuleco.creator.data.model

enum class ContentType {
    POST, ARTICLE, IMAGE, VIDEO, AUDIO, POLL, ANNOUNCEMENT
}

enum class AccessType(val label: String) {
    PUBLIC("Public"),
    FOLLOWERS("Followers Only"),
    SUBSCRIBERS("Subscribers Only"),
    PREMIUM_TIER("Tier: Insider+"),
    PURCHASE("Requires Purchase")
}

data class ContentComment(
    val id: String,
    val authorName: String,
    val authorEmoji: String = "👤",
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0
)

data class ContentItem(
    val id: String,
    val creatorAccountId: String,
    val creatorName: String = "Elena Rostova",
    val creatorHandle: String = "@elenarostova",
    val channelId: String,
    val channelName: String,
    val contentType: ContentType,
    val title: String,
    val summary: String,
    val body: String,
    val mediaDuration: String? = null,
    val coverEmoji: String = "✨",
    val status: String = "PUBLISHED", // PUBLISHED, DRAFT, SCHEDULED
    val accessType: AccessType = AccessType.PUBLIC,
    val publishedDate: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isUnlocked: Boolean = true,
    val comments: List<ContentComment> = emptyList()
)
