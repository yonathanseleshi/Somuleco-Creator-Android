package com.somuleco.creator.core.network

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

// -----------------------------------------------------------------------------
// Identity bootstrap (Foundation Wave 08, `GRD/Contracts/v0.1-identity-foundation.md` §3/§5).
// `GET /me` bootstraps (create-on-first-call) this API's own `InternalUser` row from the
// caller's verified Firebase ID token — `userId` is the shared Somuleco identity id (a Firebase
// uid), NOT a UUID, hence a plain String here exactly as [com.somuleco.creator.data.model.
// UserIdentity.id] already is.
// -----------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class MeDto(
    val userId: String
)

@JsonClass(generateAdapter = true)
data class MeEnvelope(
    val data: MeDto?,
    val meta: Map<String, Any>? = null
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
// Creator Account (Foundation Wave 09, `GRD/Contracts/v0.1-creator-authorization.md` §2-3).
// Collection + `:id`-scoped shape — a Somuleco identity may own multiple Creator Accounts,
// so `GET creator-account` lists all of the caller's accounts rather than resolving "the one
// row for this user" the way pre-Wave-09 code assumed.
//
// [CreatorAccountDto] mirrors the field set on [com.somuleco.creator.core.model.CreatorAccount]
// (Wave 07/08's already-declared, previously-unwired contract model) as a `String`-typed
// Retrofit/Moshi wire shape (status/onboardingStatus are transmitted as raw enum strings, not
// re-parsed into an enum here, so an additional server-side status this client doesn't yet know
// about degrades to "an unrecognized string" rather than a hard Moshi parse failure).
//
// [CreateCreatorAccountRequest] deliberately carries ONLY `creatorType` — verified directly
// against the real NestJS `CreateCreatorAccountDto`
// (`src/creator-account/dto/create-creator-account.dto.ts`), which declares `creatorType:
// string` (`@IsNotEmpty`) and nothing else. The API's global ValidationPipe is
// `whitelist: true, forbidNonWhitelisted: true` (`src/app.module.ts:59-60`), so any additional
// property (an earlier draft of this file sent `accountName`/`displayName`/`description`,
// per the Wave 09 plan's own loosely-worded §7.4 task 4) is rejected outright as a
// non-whitelisted property, and omitting `creatorType` fails required-field validation — i.e.
// the earlier shape made every real POST /creator-account call a guaranteed 400. Matches Web's
// corrected implementation (`services/domain/creator-account.service.ts`), which resolved the
// same plan-vs-actual-DTO mismatch the same way.
// -----------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class CreatorAccountDto(
    val id: String,
    val internalUserId: String,
    val status: String,
    val creatorType: String? = null,
    val primaryCategoryId: String? = null,
    val onboardingStatus: String,
    val onboardingCompletedAt: String? = null,
    val defaultChannelId: String? = null,
    val defaultCurrency: String? = null,
    val countryCode: String? = null,
    val monetizationEnabled: Boolean = false,
    val storeEnabled: Boolean = false,
    val subscriptionsEnabled: Boolean = false,
    val aiEnabled: Boolean = false,
    val publicCreatorStatus: String? = null,
    val activatedAt: String? = null,
    val suspendedAt: String? = null,
    val closedAt: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class CreateCreatorAccountRequest(
    val creatorType: String
)

// -----------------------------------------------------------------------------
// NestJS Core API Retrofit Service
// -----------------------------------------------------------------------------

interface CoreApiService {

    // Identity bootstrap (Foundation Wave 08). Called by FirebaseAuthRepository after every
    // successful Firebase sign-in/session-restoration to obtain this API's own InternalUser
    // context — creates the row on first call for a brand-new identity, per
    // `v0.1-identity-foundation.md` §5.
    @GET("me")
    suspend fun getMe(): Response<MeEnvelope>

    // Auth & Identity — legacy password-accepting endpoints speculatively authored pre-Wave-08.
    // NestJS never verifies a password directly (Firebase does that; see
    // `v0.1-identity-foundation.md` §2) — these routes have no server-side counterpart and are
    // not called anywhere in this app (FirebaseAuthRepository never invokes them).
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

    // Creator Account (Foundation Wave 09, `v0.1-creator-authorization.md` §2). Collection +
    // `:id`-scoped — a caller may own several accounts, so `GET creator-account` lists all of
    // them rather than resolving a singular "current" row.
    @GET("creator-account")
    suspend fun listCreatorAccounts(): Response<List<CreatorAccountDto>>

    @GET("creator-account/{id}")
    suspend fun getCreatorAccount(@Path("id") id: String): Response<CreatorAccountDto>

    // Creates a new, independent `draft` account for the caller every call — no
    // one-per-user uniqueness check (`v0.1-creator-authorization.md` §2, §13 Decision 3).
    @POST("creator-account")
    suspend fun activateCreatorAccount(@Body request: CreateCreatorAccountRequest): Response<CreatorAccountDto>

    // `draft` -> `active` only (409 otherwise, surfaced here as a non-2xx Response).
    @POST("creator-account/{id}/complete-onboarding")
    suspend fun completeCreatorAccountOnboarding(@Path("id") id: String): Response<CreatorAccountDto>

    // `suspended` -> `active` only.
    @POST("creator-account/{id}/reactivate")
    suspend fun reactivateCreatorAccount(@Path("id") id: String): Response<CreatorAccountDto>

    // `draft`/`active`/`suspended` -> `closed`.
    @POST("creator-account/{id}/close")
    suspend fun closeCreatorAccount(@Path("id") id: String): Response<CreatorAccountDto>
}
