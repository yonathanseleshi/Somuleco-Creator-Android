package com.example.data.model

data class MembershipTier(
    val id: String,
    val name: String,
    val monthlyPrice: Double,
    val description: String = "Exclusive subscriber benefits and creator access",
    val benefits: List<String> = emptyList(),
    val perks: List<String> = benefits,
    val subscriberCount: Int = 0,
    val isFeatured: Boolean = false,
    val badgeColorHex: Long = 0xFF7C3AED
)

data class SubscriptionPlan(
    val id: String,
    val creatorAccountId: String,
    val channelId: String,
    val name: String,
    val description: String,
    val tiers: List<MembershipTier>,
    val isSubscribed: Boolean = false,
    val activeTierId: String? = null
)

data class SubscriberMember(
    val id: String,
    val name: String,
    val handle: String,
    val avatarEmoji: String,
    val tierName: String,
    val monthlyAmount: Double,
    val joinedDate: String,
    val status: String = "ACTIVE"
)
