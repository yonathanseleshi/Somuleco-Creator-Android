package com.somuleco.creator

import com.somuleco.creator.core.network.ChannelDto
import com.somuleco.creator.core.network.CoreApiService
import com.somuleco.creator.core.network.CreateChannelRequest
import com.somuleco.creator.core.network.toChannelSummary
import com.somuleco.creator.data.repository.impl.RemoteChannelRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * A hand-written [CoreApiService] double — no Retrofit instance, no real network access,
 * exactly as `v0.1-client-data-access.md` §8 requires for the reference-remote-
 * implementation proof ("Android and iOS ... should likewise assert the remote
 * implementation's mapping without real network access"). Every endpoint this test does
 * not exercise fails loudly rather than silently returning a default.
 */
private class FakeCoreApiService : CoreApiService {
    private fun fail(): Nothing = throw UnsupportedOperationException("not stubbed for this test")

    val seededDtos = listOf(
        ChannelDto(
            id = "ch_remote_1", name = "Remote Photography", description = "A remote channel",
            category = "Photography", handle = "@remote/photo", followersCount = 42, contentCount = 7, isPublic = true
        )
    )

    override suspend fun getChannels(): Response<List<ChannelDto>> = Response.success(seededDtos)

    override suspend fun createChannel(request: CreateChannelRequest): Response<ChannelDto> =
        Response.success(
            ChannelDto(
                id = "ch_remote_new", name = request.name, description = request.description,
                category = request.category, handle = request.handle, followersCount = 0, contentCount = 0,
                isPublic = request.isPublic
            )
        )

    override suspend fun getMe() = fail()
    override suspend fun login(request: com.somuleco.creator.core.network.AuthRequest) = fail()
    override suspend fun signup(request: com.somuleco.creator.core.network.SignupRequest) = fail()
    override suspend fun verifyEmail(request: com.somuleco.creator.core.network.VerifyEmailRequest) = fail()
    override suspend fun forgotPassword(request: com.somuleco.creator.core.network.ForgotPasswordRequest) = fail()
    override suspend fun resetPassword(request: com.somuleco.creator.core.network.ResetPasswordRequest) = fail()
    override suspend fun getChannel(id: String) = fail()
    override suspend fun getContentList(channelId: String?) = fail()
    override suspend fun getContent(id: String) = fail()
    override suspend fun createContent(request: com.somuleco.creator.core.network.CreateContentRequest) = fail()
    override suspend fun getProducts() = fail()
    override suspend fun getProduct(id: String) = fail()
    override suspend fun createProduct(request: com.somuleco.creator.core.network.CreateProductRequest) = fail()
    override suspend fun getProductRights(productId: String) = fail()
    override suspend fun updateProductRights(productId: String, rights: com.somuleco.creator.core.network.ProductRightsDto) = fail()
    override suspend fun getMarketplaceListing(productId: String) = fail()
    override suspend fun updateMarketplaceListing(productId: String, listing: com.somuleco.creator.core.network.MarketplaceListingDto) = fail()
    override suspend fun toggleMarketplacePublish(productId: String) = fail()
    override suspend fun getRevenueSummary() = fail()
    override suspend fun getTransactions() = fail()
}

/**
 * Proves the Wave 05 reference remote implementation (plan §7.3 task 9 / §13 Decision 6):
 * [RemoteChannelRepository] satisfies the same `ChannelRepository` interface
 * `ChannelRepositoryImpl` (mock) and `ChannelsViewModel` are typed against, and correctly
 * maps [ChannelDto] into the app-facing `ChannelSummary` shape. No Retrofit/OkHttp
 * instance is created and no real network call is made.
 */
class RemoteChannelRepositoryTest {

    @Test
    fun refresh_mapsDtoFieldsIntoChannelSummary() = runBlocking {
        val api = FakeCoreApiService()
        val repository = RemoteChannelRepository(api)

        repository.refresh("acc_test")

        val channels = repository.channels.value
        assertEquals(1, channels.size)
        val mapped = channels.first()
        assertEquals("ch_remote_1", mapped.id)
        assertEquals("Remote Photography", mapped.name)
        assertEquals("A remote channel", mapped.description)
        assertEquals("Photography", mapped.category)
        assertEquals("@remote/photo", mapped.handle)
        assertEquals(42, mapped.followersCount)
        assertEquals(7, mapped.contentCount)
        assertEquals("acc_test", mapped.creatorAccountId)
    }

    @Test
    fun createChannel_roundTripsThroughFakeApi_andAppendsMappedResult() {
        val api = FakeCoreApiService()
        val repository = RemoteChannelRepository(api)

        val created = repository.createChannel("New Channel", "desc", "cat", "@handle")

        assertEquals("ch_remote_new", created.id)
        assertEquals("New Channel", created.name)
        assertTrue(repository.channels.value.any { it.id == "ch_remote_new" })
    }

    @Test
    fun dtoMapper_appliesSensibleDefaults_forFieldsTheDtoDoesNotCarry() {
        // ChannelDto (a NestJS Core API contract) has no cosmetic/presentation fields
        // (emoji, color) or per-channel subscriber count — the mapper documents that gap
        // rather than inventing server fields that don't exist.
        val dto = ChannelDto(
            id = "ch_x", name = "X", description = "d", category = "c",
            handle = "@x", followersCount = 1, contentCount = 1, isPublic = true
        )
        val mapped = dto.toChannelSummary("acc_x")
        assertEquals(0, mapped.subscribersCount)
        assertEquals(false, mapped.isDefault)
        assertEquals(false, mapped.isFollowed)
    }
}
