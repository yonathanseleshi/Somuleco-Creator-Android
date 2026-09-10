package com.somuleco.creator.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Canonical content/media/audience/notification objects — contract
 * §3.6-3.9 and §3.21. See `Common.kt` for shared conventions.
 */

enum class ContentType {
    @Json(name = "text_post") TEXT_POST,
    @Json(name = "image_post") IMAGE_POST,
    @Json(name = "video_post") VIDEO_POST,
    @Json(name = "audio_post") AUDIO_POST,
    @Json(name = "article") ARTICLE,
    @Json(name = "gallery") GALLERY,
    @Json(name = "file_post") FILE_POST,
    @Json(name = "poll") POLL,
    @Json(name = "announcement") ANNOUNCEMENT
}

enum class ContentStatus {
    @Json(name = "draft") DRAFT,
    @Json(name = "scheduled") SCHEDULED,
    @Json(name = "published") PUBLISHED,
    @Json(name = "unpublished") UNPUBLISHED,
    @Json(name = "archived") ARCHIVED,
    @Json(name = "restricted") RESTRICTED
}

enum class ContentVisibility {
    @Json(name = "public") PUBLIC,
    @Json(name = "followers") FOLLOWERS,
    @Json(name = "free_subscribers") FREE_SUBSCRIBERS,
    @Json(name = "paid_subscribers") PAID_SUBSCRIBERS,
    @Json(name = "membership_tier") MEMBERSHIP_TIER,
    @Json(name = "purchase") PURCHASE,
    @Json(name = "private") PRIVATE,
    @Json(name = "unlisted") UNLISTED
}

/** Contract §3.6. */
@JsonClass(generateAdapter = true)
data class ContentItem(
    val id: String,
    val creatorAccountId: String,
    val primaryChannelId: String,
    val contentType: ContentType,
    val title: String?,
    val subtitle: String?,
    val body: String?,
    val summary: String?,
    val slug: String,
    val status: ContentStatus,
    val visibility: ContentVisibility,
    val monetizationType: String?,
    val commentsEnabled: Boolean,
    val sharingEnabled: Boolean,
    val repostingEnabled: Boolean,
    val discoverable: Boolean,
    val matureContentFlag: Boolean,
    val publishedAt: String?,
    val scheduledAt: String?,
    val archivedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class MediaAssetStatus {
    @Json(name = "uploading") UPLOADING,
    @Json(name = "uploaded") UPLOADED,
    @Json(name = "processing") PROCESSING,
    @Json(name = "ready") READY,
    @Json(name = "failed") FAILED,
    @Json(name = "restricted") RESTRICTED,
    @Json(name = "deleted") DELETED
}

/** Contract §3.7. */
@JsonClass(generateAdapter = true)
data class MediaAsset(
    val id: String,
    val creatorAccountId: String,
    val storageProvider: String,
    val storageObjectKey: String,
    val mediaType: String,
    val originalFilename: String?,
    val mimeType: String,
    val sizeBytes: Long,
    val durationSeconds: Int?,
    val width: Int?,
    val height: Int?,
    val status: MediaAssetStatus,
    val thumbnailMediaId: String?,
    val checksum: String?,
    val rightsReferenceId: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class AudienceRelationshipLevel {
    @Json(name = "viewer") VIEWER,
    @Json(name = "follower") FOLLOWER,
    @Json(name = "subscriber") SUBSCRIBER,
    @Json(name = "customer") CUSTOMER,
    @Json(name = "member") MEMBER,
    @Json(name = "advocate") ADVOCATE
}

/** Contract §3.8. */
@JsonClass(generateAdapter = true)
data class AudienceMember(
    val id: String,
    val creatorAccountId: String,
    val userId: String,
    val firstSeenAt: String?,
    val firstFollowedAt: String?,
    val firstSubscribedAt: String?,
    val firstPurchasedAt: String?,
    val relationshipLevel: AudienceRelationshipLevel,
    val engagementScore: Double,
    val lifetimeValueCache: Money?,
    val lastActivityAt: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class FollowTargetType {
    @Json(name = "creator") CREATOR,
    @Json(name = "channel") CHANNEL
}

enum class FollowStatus {
    @Json(name = "active") ACTIVE,
    @Json(name = "blocked") BLOCKED,
    @Json(name = "removed") REMOVED
}

/** Contract §3.9. */
@JsonClass(generateAdapter = true)
data class Follow(
    val id: String,
    val followerUserId: String,
    val targetType: FollowTargetType,
    val targetId: String,
    val status: FollowStatus,
    val notificationsEnabled: Boolean,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Contract §3.21 (`CreatorNotificationReference` in the data model
 * inventory). No `updatedAt` — read-state changes are represented by
 * `read: Boolean` toggling, not a timestamped revision.
 */
@JsonClass(generateAdapter = true)
data class Notification(
    val id: String,
    val userId: String,
    val externalNotificationId: String?,
    val notificationType: String,
    val resourceType: String?,
    val resourceId: String?,
    val read: Boolean,
    val createdAt: String
)
