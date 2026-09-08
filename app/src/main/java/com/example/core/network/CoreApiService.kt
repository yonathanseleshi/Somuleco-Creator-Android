package com.example.core.network

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

// -----------------------------------------------------------------------------
// DTOs for NestJS Core API
// -----------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class AuthRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class SignupRequest(
    val fullName: String,
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto,
    val isCreator: Boolean,
    val onboardingCompleted: Boolean
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val handle: String? = null,
    val avatarUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyEmailRequest(val code: String)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequest(val email: String)

@JsonClass(generateAdapter = true)
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@JsonClass(generateAdapter = true)
data class GenericMessageResponse(
    val success: Boolean,
    val message: String
)

@JsonClass(generateAdapter = true)
data class ChannelDto(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val handle: String,
    val followersCount: Int,
    val contentCount: Int,
    val isPublic: Boolean
)

@JsonClass(generateAdapter = true)
data class CreateChannelRequest(
    val name: String,
    val description: String,
    val category: String,
    val handle: String,
    val isPublic: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ContentDto(
    val id: String,
    val title: String,
    val body: String,
    val contentType: String,
    val accessLevel: String,
    val channelId: String?,
    val publishedAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val tags: List<String>
)

@JsonClass(generateAdapter = true)
data class CreateContentRequest(
    val title: String,
    val body: String,
    val contentType: String,
    val accessLevel: String,
    val channelId: String?,
    val tags: List<String>
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val category: String,
    val channelId: String?,
    val isDprProtected: Boolean,
    val rightsId: String?,
    val salesCount: Int,
    val revenue: Double,
    val fileFormat: String
)

@JsonClass(generateAdapter = true)
data class CreateProductRequest(
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val channelId: String?,
    val deliverables: List<String>,
    val isDprProtected: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ProductRightsDto(
    val productId: String,
    val certificateId: String,
    val registeredOwner: String,
    val allowCommercialUse: Boolean,
    val allowRedistribution: Boolean,
    val allowModification: Boolean,
    val allowDigitalResale: Boolean,
    val licenseType: String,
    val fingerprintHash: String
)

@JsonClass(generateAdapter = true)
data class MarketplaceListingDto(
    val productId: String,
    val isPublished: Boolean,
    val listingPrice: Double,
    val commissionRate: Double,
    val discoverableInSomulecoGlobal: Boolean,
    val syncStatus: String
)

@JsonClass(generateAdapter = true)
data class RevenueSummaryDto(
    val totalRevenue: Double,
    val availableBalance: Double,
    val pendingBalance: Double,
    val monthlyRecurringRevenue: Double,
    val storeSalesRevenue: Double
)

@JsonClass(generateAdapter = true)
data class RevenueTransactionDto(
    val id: String,
    val itemTitle: String,
    val customerName: String,
    val type: String,
    val grossAmount: Double,
    val platformFee: Double,
    val netAmount: Double,
    val date: String,
    val status: String
)

// -----------------------------------------------------------------------------
// NestJS Core API Retrofit Service
// -----------------------------------------------------------------------------

interface CoreApiService {

    // Auth & Identity
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<GenericMessageResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<GenericMessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<GenericMessageResponse>

    // Channels
    @GET("creator/channels")
    suspend fun getChannels(): Response<List<ChannelDto>>

    @GET("creator/channels/{id}")
    suspend fun getChannel(@Path("id") id: String): Response<ChannelDto>

    @POST("creator/channels")
    suspend fun createChannel(@Body request: CreateChannelRequest): Response<ChannelDto>

    // Content
    @GET("creator/content")
    suspend fun getContentList(@Query("channelId") channelId: String? = null): Response<List<ContentDto>>

    @GET("creator/content/{id}")
    suspend fun getContent(@Path("id") id: String): Response<ContentDto>

    @POST("creator/content")
    suspend fun createContent(@Body request: CreateContentRequest): Response<ContentDto>

    // Products & Store
    @GET("creator/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("creator/products/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ProductDto>

    @POST("creator/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductDto>

    // Digital Product Rights (DPR)
    @GET("creator/products/{id}/rights")
    suspend fun getProductRights(@Path("id") productId: String): Response<ProductRightsDto>

    @PUT("creator/products/{id}/rights")
    suspend fun updateProductRights(@Path("id") productId: String, @Body rights: ProductRightsDto): Response<ProductRightsDto>

    // Marketplace
    @GET("creator/products/{id}/marketplace")
    suspend fun getMarketplaceListing(@Path("id") productId: String): Response<MarketplaceListingDto>

    @PUT("creator/products/{id}/marketplace")
    suspend fun updateMarketplaceListing(@Path("id") productId: String, @Body listing: MarketplaceListingDto): Response<MarketplaceListingDto>

    @POST("creator/products/{id}/marketplace/toggle")
    suspend fun toggleMarketplacePublish(@Path("id") productId: String): Response<MarketplaceListingDto>

    // Revenue & Transactions
    @GET("creator/revenue/summary")
    suspend fun getRevenueSummary(): Response<RevenueSummaryDto>

    @GET("creator/revenue/transactions")
    suspend fun getTransactions(): Response<List<RevenueTransactionDto>>
}
