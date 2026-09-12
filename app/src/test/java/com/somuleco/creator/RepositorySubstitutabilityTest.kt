package com.somuleco.creator

import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.feature.creator.channels.ChannelsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Fake [ChannelRepository] the substitutability test below swaps in for
 * [ChannelsViewModel] — a fake implementation satisfying the same interface the mock
 * (`ChannelRepositoryImpl`) and the reference remote implementation
 * (`RemoteChannelRepository`) also satisfy.
 */
private class FakeChannelRepository : ChannelRepository {
    private val _channels = MutableStateFlow<List<ChannelSummary>>(emptyList())
    override val channels: StateFlow<List<ChannelSummary>> = _channels.asStateFlow()

    var shouldFailRefresh = false
    var refreshCallCount = 0

    override suspend fun refresh() {
        refreshCallCount++
        if (shouldFailRefresh) throw IllegalStateException("Simulated network failure")
    }

    override fun getChannel(id: String): ChannelSummary? = _channels.value.find { it.id == id }

    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary {
        val channel = ChannelSummary(
            id = "fake_$name", creatorAccountId = "acc_fake", name = name, slug = name, handle = handle,
            description = description, category = category, followersCount = 0, subscribersCount = 0, contentCount = 0
        )
        _channels.value = _channels.value + channel
        return channel
    }

    override fun toggleFollowChannel(channelId: String) {
        _channels.value = _channels.value.map { if (it.id == channelId) it.copy(isFollowed = !it.isFollowed) else it }
    }

    fun emit(list: List<ChannelSummary>) {
        _channels.value = list
    }

    private fun ChannelSummary.copy(isFollowed: Boolean) = ChannelSummary(
        id, creatorAccountId, name, slug, handle, description, category, followersCount, subscribersCount,
        contentCount, isDefault, iconEmoji, primaryColorHex, isSubscribed, isFollowed
    )
}

private fun fakeChannel(id: String, name: String) = ChannelSummary(
    id = id, creatorAccountId = "acc_fake", name = name, slug = name, handle = "@$name",
    description = "desc", category = "cat", followersCount = 1, subscribersCount = 1, contentCount = 1
)

/**
 * Proves acceptance criterion 2 of `v0.1-client-data-access.md` §9: "at least one test must
 * substitute a fake/stub implementation for a repository/data source and observe the
 * view-facing state change accordingly." [ChannelsViewModel] is constructed against the
 * [ChannelRepository] *interface* (constructor injection, the same shape Hilt uses at
 * runtime) — swapping [FakeChannelRepository] in for the real `ChannelRepositoryImpl`
 * requires no change to the ViewModel or the screen, which is the entire point of the
 * boundary.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepositorySubstitutabilityTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun channelsViewModel_reflectsFakeRepositoryState_withNoViewModelCodeChange() = runTest(testDispatcher) {
        val fake = FakeChannelRepository()
        fake.emit(listOf(fakeChannel("ch_a", "Fake Channel A")))

        val viewModel = ChannelsViewModel(fake)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("Expected Success, got $state", state is UiState.Success)
        val channels = (state as UiState.Success).data
        assertEquals(1, channels.size)
        assertEquals("Fake Channel A", channels.first().name)

        // The view-facing state reacts to further changes on the fake, exactly as it would
        // to the real mock or remote implementation emitting new data.
        fake.emit(listOf(fakeChannel("ch_a", "Fake Channel A"), fakeChannel("ch_b", "Fake Channel B")))
        testDispatcher.scheduler.advanceUntilIdle()

        val updated = viewModel.state.value as UiState.Success
        assertEquals(2, updated.data.size)
    }
}
