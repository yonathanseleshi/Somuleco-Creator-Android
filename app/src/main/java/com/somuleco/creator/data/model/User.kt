package com.somuleco.creator.data.model

data class UserReference(
    val id: String,
    val displayName: String,
    val avatarUrl: String = "",
    val username: String,
    val email: String,
    val isCreator: Boolean = true
)

// Vocabulary aligned to the canonical CreatorAccount.status enum in
// `GRD/Contracts/v0.1-domain-contracts.md` §3.2 (draft/active/restricted/
// suspended/closed).
enum class CreatorAccountStatus {
    DRAFT, ACTIVE, RESTRICTED, SUSPENDED, CLOSED
}

data class CreatorAccount(
    val id: String,
    val userId: String,
    val status: CreatorAccountStatus = CreatorAccountStatus.ACTIVE,
    val creatorType: String = "Educator & Creative",
    val primaryCategory: String = "Photography & Visual Arts",
    val defaultChannelId: String = "ch_1",
    val currency: String = "USD",
    val storeEnabled: Boolean = true,
    val subscriptionsEnabled: Boolean = true,
    val aiEnabled: Boolean = true,
    val onboardingCompleted: Boolean = true
)

data class CreatorProfile(
    val id: String,
    val creatorAccountId: String,
    val handle: String,
    val displayName: String,
    val bio: String,
    val headline: String,
    val location: String = "Seattle, WA & Global",
    val websiteUrl: String = "https://elenarostova.somuleco.me",
    val verified: Boolean = true,
    val followerCount: Int = 12480,
    val subscriberCount: Int = 840,
    val avatarEmoji: String = "📸",
    val category: String = "Photography & Lighting",
    val coverGradientStart: Long = 0xFF7C3AED,
    val coverGradientEnd: Long = 0xFF2563EB
)

data class CreatorIntegration(
    val id: String,
    val name: String,
    val category: String, // "ECOSYSTEM", "SOCIAL", "STORAGE"
    val description: String,
    val iconEmoji: String,
    val isConnected: Boolean
)

data class CreatorGoal(
    val id: String,
    val title: String,
    val targetValue: String,
    val targetDate: String,
    val isAchieved: Boolean = false,
    val progress: Float = 0.65f
)
