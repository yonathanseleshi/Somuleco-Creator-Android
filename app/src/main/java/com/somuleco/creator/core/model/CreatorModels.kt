package com.somuleco.creator.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Canonical identity/creator-account/channel objects — contract §3.1-3.5.
 * See `Common.kt` for shared conventions.
 */

/** Contract §3.1 — reference-only cache of the shared Somuleco identity. */
@JsonClass(generateAdapter = true)
data class UserReference(
    val userId: String,
    val displayNameCache: String?,
    val avatarUrlCache: String?,
    val usernameCache: String?,
    val identityStatus: String,
    val createdAtReference: String,
    val updatedAtReference: String
)

enum class CreatorAccountStatus {
    @Json(name = "draft") DRAFT,
    @Json(name = "active") ACTIVE,
    @Json(name = "restricted") RESTRICTED,
    @Json(name = "suspended") SUSPENDED,
    @Json(name = "closed") CLOSED
}

/** Contract §3.2. */
@JsonClass(generateAdapter = true)
data class CreatorAccount(
    val id: String,
    val userId: String,
    val status: CreatorAccountStatus,
    val creatorType: String,
    val primaryCategoryId: String?,
    val onboardingStatus: String,
    val onboardingCompletedAt: String?,
    val defaultChannelId: String?,
    val defaultCurrency: String,
    val countryCode: String,
    val monetizationEnabled: Boolean,
    val storeEnabled: Boolean,
    val subscriptionsEnabled: Boolean,
    val aiEnabled: Boolean,
    val publicCreatorStatus: String,
    val activatedAt: String?,
    val suspendedAt: String?,
    val closedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class ProfileVisibility {
    @Json(name = "public") PUBLIC,
    @Json(name = "private") PRIVATE,
    @Json(name = "unlisted") UNLISTED
}

/** Contract §3.3. */
@JsonClass(generateAdapter = true)
data class CreatorProfile(
    val id: String,
    val creatorAccountId: String,
    val handle: String,
    val displayName: String,
    val bio: String?,
    val shortBio: String?,
    val profileImageMediaId: String?,
    val coverMediaId: String?,
    val headline: String?,
    val locationText: String?,
    val websiteUrl: String?,
    val publicEmailEnabled: Boolean,
    val discoverable: Boolean,
    val profileVisibility: ProfileVisibility,
    val verificationStatus: String,
    val followerCountCache: Int,
    val subscriberCountCache: Int,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String?
)

/** Contract §3.4. */
@JsonClass(generateAdapter = true)
data class CreatorOnboarding(
    val id: String,
    val creatorAccountId: String,
    val currentStep: String,
    val completionPercentage: Int,
    val creatorGoalsCompleted: Boolean,
    val profileCompleted: Boolean,
    val firstChannelCompleted: Boolean,
    val monetizationPreferencesCompleted: Boolean,
    val contentPreferencesCompleted: Boolean,
    val aiOnboardingCompleted: Boolean,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class ChannelOwnerType {
    @Json(name = "creator") CREATOR
    // future: organization | school | enterprise | team
}

enum class ChannelStatus {
    @Json(name = "draft") DRAFT,
    @Json(name = "active") ACTIVE,
    @Json(name = "archived") ARCHIVED,
    @Json(name = "restricted") RESTRICTED
}

enum class ChannelVisibility {
    @Json(name = "public") PUBLIC,
    @Json(name = "private") PRIVATE,
    @Json(name = "unlisted") UNLISTED
}

/** Contract §3.5. */
@JsonClass(generateAdapter = true)
data class Channel(
    val id: String,
    val creatorAccountId: String,
    val ownerType: ChannelOwnerType,
    val ownerReferenceId: String,
    val name: String,
    val slug: String,
    val handle: String,
    val description: String?,
    val shortDescription: String?,
    val profileMediaId: String?,
    val coverMediaId: String?,
    val primaryCategoryId: String?,
    val status: ChannelStatus,
    val visibility: ChannelVisibility,
    val discoverable: Boolean,
    val followersEnabled: Boolean,
    val subscriptionsEnabled: Boolean,
    val commentsEnabled: Boolean,
    val communityEnabled: Boolean,
    val storeEnabled: Boolean,
    val academyEnabled: Boolean,
    val defaultContentVisibility: String,
    val defaultCommentPolicy: String,
    val publishedAt: String?,
    val archivedAt: String?,
    val createdAt: String,
    val updatedAt: String
)
