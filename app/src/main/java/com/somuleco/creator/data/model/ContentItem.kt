package com.somuleco.creator.data.model

// Vocabulary aligned to the canonical ContentItem.contentType enum in
// `GRD/Contracts/v0.1-domain-contracts.md` §3.6 (text_post/image_post/
// video_post/audio_post/article/gallery/file_post/poll/announcement).
enum class ContentType {
    TEXT_POST, IMAGE_POST, VIDEO_POST, AUDIO_POST, ARTICLE, GALLERY, FILE_POST, POLL, ANNOUNCEMENT
}

// Vocabulary aligned to the canonical ContentItem.visibility enum in the
// same contract (public/followers/free_subscribers/paid_subscribers/
// membership_tier/purchase/private/unlisted). Only the values this app
// currently uses are represented.
enum class AccessType(val label: String) {
    PUBLIC("Public"),
    FOLLOWERS("Followers Only"),
    PAID_SUBSCRIBERS("Subscribers Only"),
    MEMBERSHIP_TIER("Tier: Insider+"),
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
