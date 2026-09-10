package com.somuleco.creator.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Canonical monetization/commerce/analytics objects — contract §3.10-3.20.
 * See `Common.kt` for shared conventions.
 */

enum class SubscriptionPlanType {
    @Json(name = "free") FREE,
    @Json(name = "paid") PAID
}

enum class SubscriptionPlanStatus {
    @Json(name = "draft") DRAFT,
    @Json(name = "active") ACTIVE,
    @Json(name = "archived") ARCHIVED
}

/** Contract §3.10. */
@JsonClass(generateAdapter = true)
data class SubscriptionPlan(
    val id: String,
    val creatorAccountId: String,
    val channelId: String?,
    val name: String,
    val description: String?,
    val planType: SubscriptionPlanType,
    val billingPeriod: String,
    val price: Money,
    val status: SubscriptionPlanStatus,
    val externalPaymentPlanId: String?,
    val trialDays: Int,
    val public: Boolean,
    val createdAt: String,
    val updatedAt: String
)

/** Contract §3.11. */
@JsonClass(generateAdapter = true)
data class MembershipTier(
    val id: String,
    val creatorAccountId: String,
    val channelId: String?,
    val subscriptionPlanId: String,
    val name: String,
    val description: String?,
    val price: Money,
    val displayOrder: Int,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

enum class CreatorSubscriptionStatus {
    @Json(name = "pending") PENDING,
    @Json(name = "active") ACTIVE,
    @Json(name = "past_due") PAST_DUE,
    @Json(name = "paused") PAUSED,
    @Json(name = "cancelled") CANCELLED,
    @Json(name = "expired") EXPIRED
}

/**
 * Contract §3.12 — a consumer's subscription to a Creator (`Subscription`
 * in the data model inventory, renamed here to avoid ambiguity with
 * [SubscriptionPlan]).
 */
@JsonClass(generateAdapter = true)
data class CreatorSubscription(
    val id: String,
    val subscriberUserId: String,
    val creatorAccountId: String,
    val channelId: String?,
    val subscriptionPlanId: String,
    val membershipTierId: String?,
    val externalSubscriptionId: String?,
    val status: CreatorSubscriptionStatus,
    val startedAt: String,
    val currentPeriodStart: String,
    val currentPeriodEnd: String,
    val cancelledAt: String?,
    val endedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

/** Contract §3.13. */
@JsonClass(generateAdapter = true)
data class CreatorStore(
    val id: String,
    val creatorAccountId: String,
    val channelId: String?,
    val name: String,
    val slug: String,
    val description: String?,
    val logoMediaId: String?,
    val coverMediaId: String?,
    val status: String,
    val visibility: String,
    val defaultCurrency: String,
    val marketplaceEnabled: Boolean,
    val createdAt: String,
    val updatedAt: String
)

enum class CreatorProductType {
    @Json(name = "digital_product") DIGITAL_PRODUCT,
    @Json(name = "media") MEDIA,
    @Json(name = "course_reference") COURSE_REFERENCE,
    @Json(name = "service_reference") SERVICE_REFERENCE
}

enum class CreatorProductStatus {
    @Json(name = "draft") DRAFT,
    @Json(name = "active") ACTIVE,
    @Json(name = "archived") ARCHIVED,
    @Json(name = "restricted") RESTRICTED
}

/** Contract §3.14. */
@JsonClass(generateAdapter = true)
data class CreatorProduct(
    val id: String,
    val creatorAccountId: String,
    val creatorStoreId: String,
    val channelId: String?,
    val productType: CreatorProductType,
    val title: String,
    val slug: String,
    val shortDescription: String?,
    val description: String?,
    val primaryMediaId: String?,
    val status: CreatorProductStatus,
    val visibility: String,
    val price: Money,
    val free: Boolean,
    val marketplaceEnabled: Boolean,
    val marketplaceListingId: String?,
    val rightsRequired: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String?
)

/** Contract §3.15 — an extension of [CreatorProduct]. */
@JsonClass(generateAdapter = true)
data class DigitalProduct(
    val id: String,
    val creatorProductId: String,
    val deliveryType: String,
    val downloadEnabled: Boolean,
    val protectedViewEnabled: Boolean,
    val protectedStreamEnabled: Boolean,
    val previewEnabled: Boolean,
    val maxDownloads: Int?,
    val rightsRecordId: String?,
    val currentVersionId: String?,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Contract §3.16 — reference only in the Creator DB (system of record:
 * Digital Product Rights). No `updatedAt`; use `lastSyncedAt` as the
 * freshness indicator instead.
 */
@JsonClass(generateAdapter = true)
data class RightsRecordReference(
    val id: String,
    val creatorAccountId: String,
    val creatorProductId: String?,
    val mediaAssetId: String?,
    val dprAssetId: String?,
    val dprRightsRecordId: String?,
    val ownershipStatus: String,
    val licenseId: String?,
    val rightsStatus: String,
    val registeredAt: String?,
    val lastSyncedAt: String?
)

/** Contract §3.17 — reference/sync metadata in the Creator DB. */
@JsonClass(generateAdapter = true)
data class MarketplaceListingReference(
    val id: String,
    val creatorProductId: String,
    val marketplaceListingId: String,
    val syncStatus: String,
    val marketplaceStatus: String,
    val lastSyncedAt: String?,
    val lastError: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class EntitlementType {
    @Json(name = "product") PRODUCT,
    @Json(name = "subscription") SUBSCRIPTION,
    @Json(name = "membership") MEMBERSHIP,
    @Json(name = "media") MEDIA,
    @Json(name = "course") COURSE,
    @Json(name = "event") EVENT,
    @Json(name = "community") COMMUNITY
}

/** Contract §3.18. */
@JsonClass(generateAdapter = true)
data class EntitlementReference(
    val id: String,
    val userId: String,
    val entitlementType: EntitlementType,
    val resourceType: String,
    val resourceId: String,
    val externalEntitlementId: String?,
    val sourceType: String,
    val sourceReferenceId: String?,
    val status: String,
    val startsAt: String?,
    val expiresAt: String?,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Contract §3.19 (`CreatorAnalyticsDailySummary` in the data model
 * inventory). Per contract §2.7, aggregate entities carry one shared
 * top-level `currency` field and bare integer minor-unit amounts rather
 * than repeating `{amount, currency}` per metric.
 */
@JsonClass(generateAdapter = true)
data class AnalyticsSummary(
    val id: String,
    val creatorAccountId: String,
    val date: String,
    val currency: String,
    val profileViews: Int,
    val channelViews: Int,
    val contentViews: Int,
    val uniqueViewers: Int,
    val newFollowers: Int,
    val newSubscribers: Int,
    val cancelledSubscriptions: Int,
    val productViews: Int,
    val salesCount: Int,
    val grossRevenueAmount: Long,
    val engagementCount: Int,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Contract §3.20 (`CreatorRevenueSummary` in the data model inventory) — a
 * reporting representation, not the accounting ledger.
 */
@JsonClass(generateAdapter = true)
data class RevenueSummary(
    val id: String,
    val creatorAccountId: String,
    val periodType: String,
    val periodStart: String,
    val periodEnd: String,
    val currency: String,
    val subscriptionRevenueAmount: Long,
    val productRevenueAmount: Long,
    val courseRevenueAmount: Long,
    val eventRevenueAmount: Long,
    val otherRevenueAmount: Long,
    val grossRevenueAmount: Long,
    val feesAmount: Long,
    val netRevenueAmount: Long,
    val updatedAt: String
)
