package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : ChannelRepository {

    override val channels: StateFlow<List<ChannelSummary>> = dataSource.channels

    override suspend fun refresh() {
        // The mock source is already in memory; refresh is a no-op that still crosses a
        // real suspend boundary so a remote implementation's genuine network fetch is a
        // drop-in swap with no ViewModel-level signature change.
    }

    override fun getChannel(id: String): ChannelSummary? = dataSource.getChannel(id)

    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary =
        dataSource.createChannel(name, description, category, handle)

    override fun toggleFollowChannel(channelId: String) = dataSource.toggleFollowChannel(channelId)
}
