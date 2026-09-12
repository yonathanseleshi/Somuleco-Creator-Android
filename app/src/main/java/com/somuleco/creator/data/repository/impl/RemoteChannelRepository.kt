package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.core.network.CoreApiService
import com.somuleco.creator.core.network.toChannelSummary
import com.somuleco.creator.core.network.toCreateChannelRequest
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * The Wave 05 reference remote implementation (plan §7.3 task 9 / §13 Decision 6):
 * [ChannelRepository] backed by the existing `core/network` Retrofit [CoreApiService],
 * using the DTO->UI-projection mappers in `ChannelMappers.kt` that this wave adds
 * (`core/network` previously defined its own DTOs with no mapper to any app-facing type).
 *
 * This class is compiled and exercised by [com.somuleco.creator.RemoteChannelRepositoryTest]
 * but is **not** bound in [com.somuleco.creator.core.di.RepositoryModule] — the mock stays
 * the default runtime binding (`v0.1-client-data-access.md` §8). Swapping it in for real
 * would be a one-line change to that module's `@Binds`, touching no ViewModel or screen.
 */
class RemoteChannelRepository @Inject constructor(
    private val api: CoreApiService
) : ChannelRepository {

    private val _channels = MutableStateFlow<List<ChannelSummary>>(emptyList())
    override val channels: StateFlow<List<ChannelSummary>> = _channels.asStateFlow()

    /** Fetches and caches the channel list from the Core API, mapping DTOs to [ChannelSummary]. */
    override suspend fun refresh() = refresh("acc_remote")

    suspend fun refresh(creatorAccountId: String) {
        val response = api.getChannels()
        if (response.isSuccessful) {
            _channels.value = response.body().orEmpty().map { it.toChannelSummary(creatorAccountId) }
        } else {
            throw java.io.IOException("Failed to load channels: HTTP ${response.code()}")
        }
    }

    override fun getChannel(id: String): ChannelSummary? = _channels.value.find { it.id == id }

    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary {
        val placeholder = ChannelSummary(
            id = "pending",
            creatorAccountId = "acc_remote",
            name = name,
            slug = name.lowercase().replace(" ", "-"),
            handle = handle,
            description = description,
            category = category,
            followersCount = 0,
            subscribersCount = 0,
            contentCount = 0
        )
        return runBlocking {
            val response = api.createChannel(placeholder.toCreateChannelRequest())
            val created = if (response.isSuccessful) {
                response.body()?.toChannelSummary("acc_remote") ?: placeholder
            } else placeholder
            _channels.value = _channels.value + created
            created
        }
    }

    override fun toggleFollowChannel(channelId: String) {
        // Not part of CoreApiService yet — no-op for the reference implementation; a real
        // follow endpoint would be added alongside the rest of Wave 06/10's API surface.
    }
}
