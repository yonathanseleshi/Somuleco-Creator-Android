package com.example.data.repository.interfaces

import com.example.data.model.*
import kotlinx.coroutines.flow.StateFlow

typealias RevenueTransaction = TransactionItem

data class AudienceMetrics(
    val totalAudience: String = "14,280",
    val paidSubscribers: String = "920",
    val retentionRate: String = "94.2%",
    val avgEngagement: String = "18.4%",
    val newFollowersThisMonth: Int = 1240
)

data class TrafficSource(
    val name: String,
    val percentage: String,
    val visitors: String,
    val iconEmoji: String = "🌐"
)

data class AudienceSegment(
    val id: String,
    val name: String,
    val memberCount: Int,
    val description: String,
    val conversionRate: String
)

interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentUser: StateFlow<UserReference>
    val isCreatorMode: StateFlow<Boolean>

    suspend fun login(email: String, password: String): Result<UserReference>
    suspend fun signup(name: String, email: String, password: String): Result<UserReference>
    suspend fun verifyEmail(code: String): Result<Boolean>
    suspend fun forgotPassword(email: String): Result<Boolean>
    suspend fun resetPassword(token: String, newPassword: String): Result<Boolean>
    suspend fun logout()

    fun setCreatorMode(enabled: Boolean)
    fun toggleCreatorMode()
    fun completeOnboarding()
}

interface ChannelRepository {
    val channels: StateFlow<List<Channel>>
    val selectedChannelId: StateFlow<String?>

    fun selectChannel(id: String?)
    fun getChannel(id: String): Channel?
    fun createChannel(name: String, description: String, category: String, handle: String): Channel
    fun toggleFollowChannel(channelId: String)
}

interface ContentRepository {
    val contentItems: StateFlow<List<ContentItem>>

    fun getContent(id: String): ContentItem?
    fun publishContent(
        title: String,
        body: String,
        type: ContentType,
        access: AccessType,
        channelId: String?,
        tags: List<String>
    ): ContentItem
    fun toggleLike(contentId: String)
    fun toggleBookmark(contentId: String)
    fun addComment(contentId: String, text: String)

    // Draft preservation
    var draftContentTitle: String
    var draftContentBody: String
    var draftContentType: ContentType
    var draftContentAccessType: AccessType
}

interface ProductRepository {
    val products: StateFlow<List<CreatorProduct>>

    fun getProduct(id: String): CreatorProduct?
    fun createProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        channelId: String?,
        productType: ProductType,
        deliverables: List<String>,
        isDprProtected: Boolean
    ): CreatorProduct

    // Draft preservation
    var draftProductTitle: String
    var draftProductDescription: String
    var draftProductPrice: String
    var draftProductCategory: String
    var draftProductType: ProductType
    var draftProductDeliverables: String
}

interface RightsRepository {
    val rightsRecords: StateFlow<List<RightsRecord>>

    fun getProductRights(productId: String): ProductRightsConfig?
    fun updateProductRights(config: ProductRightsConfig)
}

interface MarketplaceRepository {
    fun getMarketplaceListing(productId: String): MarketplaceListing
    fun updateMarketplaceListing(listing: MarketplaceListing)
    fun togglePublishMarketplace(productId: String): MarketplaceListing
}

interface SubscriptionRepository {
    val subscriptionPlan: StateFlow<SubscriptionPlan>
    val subscriberMembers: StateFlow<List<SubscriberMember>>
    val creatorSubscriptionPlans: StateFlow<List<CreatorSubscriptionPlanItem>>
    val userSubscriptions: StateFlow<List<UserSubscription>>

    fun createSubscriptionPlan(plan: CreatorSubscriptionPlanItem)
    fun updateSubscriptionPlan(plan: CreatorSubscriptionPlanItem)
    fun cancelUserSubscription(id: String)
}

interface RevenueRepository {
    val transactions: StateFlow<List<RevenueTransaction>>
    val totalRevenue: Double
    val availableBalance: Double
    val pendingBalance: Double
    val monthlyRecurringRevenue: Double
    val productSalesRevenue: Double

    fun getTransaction(id: String): RevenueTransaction?
}

interface AudienceRepository {
    val audienceMetrics: StateFlow<AudienceMetrics>
    val topReferrers: StateFlow<List<TrafficSource>>
    val segments: StateFlow<List<AudienceSegment>>
}

interface MediaRepository {
    val mediaAssets: StateFlow<List<MediaAsset>>

    fun uploadMedia(name: String, type: MediaType, size: String, channelName: String): MediaAsset
    fun deleteMedia(id: String)
}

interface CreatorAIRepository {
    val chatMessages: StateFlow<List<AIMessage>>
    val suggestedPrompts: List<String>

    suspend fun sendMessage(userText: String): String
}

interface NotificationRepository {
    val notifications: StateFlow<List<NotificationItem>>

    fun markAsRead(id: String)
    fun markAllAsRead()
}

interface SettingsRepository {
    val settings: StateFlow<CreatorSettingsData>

    fun updateSettings(settings: CreatorSettingsData)
}

interface CreatorProfileRepository {
    val creatorProfile: StateFlow<CreatorProfile>
    val creatorAccount: StateFlow<CreatorAccount>
    val goals: StateFlow<List<CreatorGoal>>
    val integrations: StateFlow<List<CreatorIntegration>>

    fun updateProfile(profile: CreatorProfile)
    fun toggleIntegration(id: String)
}
