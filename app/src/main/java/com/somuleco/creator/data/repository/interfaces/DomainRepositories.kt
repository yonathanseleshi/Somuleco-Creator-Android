package com.somuleco.creator.data.repository.interfaces

import com.somuleco.creator.data.model.*
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
    val currentUser: StateFlow<UserIdentity>
    val isCreatorMode: StateFlow<Boolean>

    suspend fun login(email: String, password: String): Result<UserIdentity>
    suspend fun signup(name: String, email: String, password: String): Result<UserIdentity>

    // Foundation Wave 08 (plan §7.4 task 5, §13 Decision 8): Google Sign-In via Credential
    // Manager. [idToken] is the Google ID token Credential Manager returns
    // (`feature/auth/GoogleSignInHelper.kt`), exchanged here for a Firebase credential.
    suspend fun loginWithGoogle(idToken: String): Result<UserIdentity>

    suspend fun verifyEmail(code: String): Result<Boolean>
    suspend fun forgotPassword(email: String): Result<Boolean>
    suspend fun resetPassword(token: String, newPassword: String): Result<Boolean>

    // Foundation Wave 08: re-sends Firebase's own verification email to the currently
    // signed-in user (mirrors Somuleco Connect's `resendEmailVerification`).
    suspend fun resendEmailVerification(): Result<Unit>

    suspend fun logout()

    fun setCreatorMode(enabled: Boolean)
    fun toggleCreatorMode()
    fun completeOnboarding()
}

interface ChannelRepository {
    // Note: no `selectedChannelId` here (v0.1-client-data-access.md §7 / plan §13 Decision 9)
    // — Channel context is shell state owned by AppSessionState. This is a stateless domain
    // boundary; any method whose result can be scoped to a channel takes channelId as an
    // explicit parameter instead.
    val channels: StateFlow<List<ChannelSummary>>

    // A genuine suspend boundary (v0.1-client-data-access.md §4 / plan §13 Decision 1) that a
    // ViewModel calls from viewModelScope on load — the seam that lets a fake implementation
    // simulate a failed load for the four-state (Loading/Success/Empty/Error) test.
    suspend fun refresh()

    fun getChannel(id: String): ChannelSummary?
    fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary
    fun toggleFollowChannel(channelId: String)
}

interface ContentRepository {
    // channelId: String? = null -> "All Channels" (v0.1-client-data-access.md §7). The caller
    // (ViewModel) reads AppSessionState.selectedChannelId and passes it down; this repository
    // never reads shell state itself.
    fun observeContent(channelId: String? = null): StateFlow<List<ContentPost>>

    fun getContent(id: String): ContentPost?
    fun publishContent(
        title: String,
        body: String,
        type: ContentType,
        access: AccessType,
        channelId: String?,
        tags: List<String>
    ): ContentPost
    fun toggleLike(contentId: String)
    fun toggleBookmark(contentId: String)
    fun addComment(contentId: String, text: String)

    // Standalone discussion feed (consumer content-detail thread), distinct from a single
    // post's own `comments` list — preserved from the pre-Wave-05 behavior.
    val comments: StateFlow<List<ContentComment>>
    fun addComment(text: String)
}

interface ProductRepository {
    // channelId: String? = null -> "All Channels" (v0.1-client-data-access.md §7).
    fun observeProducts(channelId: String? = null): StateFlow<List<ProductListing>>

    fun getProduct(id: String): ProductListing?
    fun createProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        channelId: String?,
        productType: ProductType,
        deliverables: List<String>,
        isDprProtected: Boolean
    ): ProductListing

    // The richer creation path the Product Wizard screen needs (explicit file format,
    // license name, and download/commercial-use rights) — kept alongside the simpler
    // `createProduct` overload above rather than collapsing the wizard's fidelity into it.
    fun createProductDetailed(
        title: String,
        shortDescription: String,
        fullDescription: String,
        price: Double,
        channelId: String,
        productType: ProductType,
        fileFormat: String,
        licenseName: String,
        allowDownload: Boolean,
        allowCommercial: Boolean
    ): ProductListing

    fun purchaseProduct(productId: String)
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
    val subscriptionPlan: StateFlow<SubscriptionPlanInfo>
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

    // Analytics-overview + top-content-performance data. These were previously reachable only
    // as bare (non-interface) members on the `CreatorRepository` god object — both the
    // Audience and Analytics migrated features now reach them through this interface.
    val analytics: StateFlow<AnalyticsOverview>
    val topContent: StateFlow<List<TopContentPerformance>>
}

interface MediaRepository {
    val mediaAssets: StateFlow<List<MediaFile>>

    fun uploadMedia(name: String, type: MediaType, size: String, channelName: String): MediaFile
    fun deleteMedia(id: String)
}

interface CreatorAIRepository {
    val chatMessages: StateFlow<List<AiChatMessage>>
    val suggestedPrompts: List<String>
    val recommendations: StateFlow<List<CreatorRecommendationCard>>

    suspend fun sendMessage(userText: String): String
    fun dismissRecommendation(id: String)
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
    val creatorProfile: StateFlow<CreatorProfileInfo>
    val creatorAccount: StateFlow<CreatorAccountInfo>
    val goals: StateFlow<List<CreatorGoal>>
    val integrations: StateFlow<List<CreatorIntegration>>

    fun updateProfile(profile: CreatorProfileInfo)
    fun toggleIntegration(id: String)
}
