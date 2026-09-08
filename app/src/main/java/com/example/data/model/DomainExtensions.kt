package com.example.data.model

/**
 * Authentication and session states for durable navigation resolution.
 */
sealed interface AuthState {
    data object Unknown : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(
        val user: UserReference,
        val isCreator: Boolean,
        val hasCompletedOnboarding: Boolean
    ) : AuthState
}

/**
 * Detailed configuration for product-level Digital Product Rights (DPR).
 */
data class ProductRightsConfig(
    val productId: String,
    val productTitle: String,
    val certificateId: String,
    val registeredOwner: String,
    val allowCommercialUse: Boolean = true,
    val allowRedistribution: Boolean = false,
    val allowModification: Boolean = true,
    val allowDigitalResale: Boolean = false,
    val allowAiTraining: Boolean = false,
    val licenseType: String = "Commercial Creator License v2",
    val registrationTimestamp: String = "2026-09-01T10:00:00Z",
    val fingerprintHash: String = "sha256:8f4c2b9a7d3e1f0c5b8a6e9d4c2b1a0f5a7e3d1c"
)

/**
 * Product-specific marketplace publishing state and metadata.
 */
data class MarketplaceListing(
    val productId: String,
    val productTitle: String,
    val isPublished: Boolean = true,
    val listingPrice: Double,
    val currency: String = "USD",
    val commissionRate: Double = 0.05,
    val discoverableInSomulecoGlobal: Boolean = true,
    val syncStatus: String = "SYNCED",
    val lastSyncedAt: String = "Just now",
    val category: String = "Photography & Lighting",
    val previewDescription: String = "Instant digital download with verified DPR cryptographic certificate."
)

/**
 * Creator Subscription Plan and Tier management item.
 */
data class CreatorSubscriptionPlanItem(
    val id: String,
    val name: String,
    val monthlyPrice: Double,
    val isFree: Boolean = false,
    val billingPeriod: String = "MONTHLY",
    val channelId: String? = null,
    val channelName: String = "All Channels",
    val benefits: List<String> = emptyList(),
    val subscriberCount: Int = 0,
    val isActive: Boolean = true,
    val badgeColorHex: Long = 0xFF7C3AED
)

/**
 * Consumer active subscription representation.
 */
data class UserSubscription(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorHandle: String,
    val avatarEmoji: String,
    val tierName: String,
    val monthlyPrice: Double,
    val nextBillingDate: String,
    val status: String = "ACTIVE",
    val benefitsSummary: String
)

/**
 * Consumer purchased product record.
 */
data class PurchasedProduct(
    val id: String,
    val productId: String,
    val title: String,
    val creatorName: String,
    val pricePaid: Double,
    val purchaseDate: String,
    val licenseId: String,
    val fileFormat: String,
    val fileSizeMb: Double,
    val iconEmoji: String = "📘"
)

/**
 * Hierarchical Creator Settings configuration.
 */
data class CreatorSettingsData(
    val displayName: String = "Elena Rostova",
    val handle: String = "@elenarostova",
    val bio: String = "Professional portrait & commercial lighting educator.",
    val category: String = "Photography & Lighting",
    val autoDprProtect: Boolean = true,
    val publicDiscoverable: Boolean = true,
    val instantSubscriberAlerts: Boolean = true,
    val weeklyDigestEmail: Boolean = true,
    val defaultContentVisibility: String = "SUBSCRIBERS_ONLY",
    val watermarkMediaAssets: Boolean = true,
    val twoFactorEnabled: Boolean = true,
    val payoutAccount: String = "Chase Bank (•••• 4921)",
    val connectedPassportId: String = "somuleco_pass_9921_elena",
    val notifyNewComments: Boolean = true,
    val notifyDirectMessages: Boolean = false
)
