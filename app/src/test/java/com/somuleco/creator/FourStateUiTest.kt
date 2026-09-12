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
 * A [ChannelRepository] fake whose `refresh()` can be told to fail — the seam
 * `ChannelsViewModel` needs to genuinely reach [UiState.Error], since a `StateFlow` alone
 * cannot represent a failed load.
 */
private class ScriptedChannelRepository(private val initial: List<ChannelSummary>) : ChannelRepository {
    private val _channels = MutableStateFlow(initial)
    override val channels: StateFlow<List<ChannelSummary>> = _channels.asStateFlow()

    var failNextRefresh = false

    override suspend fun refresh() {
        if (failNextRefresh) throw RuntimeException("upstream unavailable")
    }

    override fun getChannel(id: String): ChannelSummary? = _channels.value.find { it.id == id }
    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary =
        throw UnsupportedOperationException("not used in this test")
    override fun toggleFollowChannel(channelId: String) {}

    fun emit(list: List<ChannelSummary>) { _channels.value = list }
}

private fun ch(id: String) = ChannelSummary(
    id = id, creatorAccountId = "acc", name = id, slug = id, handle = "@$id",
    description = "d", category = "c", followersCount = 0, subscribersCount = 0, contentCount = 0
)

/**
 * Drives one migrated feature's ViewModel ([ChannelsViewModel]) through all four
 * [UiState] conditions — Loading, Success, Empty, and a real failed-load Error — proving
 * `v0.1-client-data-access.md` §3/§9 (acceptance criterion 3): "Empty" is a distinct case
 * from a `Success` holding an empty list, and the error path is not thrown away.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FourStateUiTest {

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
    fun channelsViewModel_movesThroughLoading_success_empty_andError() = runTest(testDispatcher) {
        val repository = ScriptedChannelRepository(listOf(ch("ch_1")))
        val viewModel = ChannelsViewModel(repository)

        // 1) Loading — the coroutine launched in init() has not yet run on the (paused)
        // test dispatcher, so the initial value set synchronously in the constructor is
        // still the current state.
        assertTrue("Expected Loading immediately after construction", viewModel.state.value is UiState.Loading)

        // 2) Success — once the scheduler runs, refresh() succeeds and the seeded channel
        // list is collected.
        testDispatcher.scheduler.advanceUntilIdle()
        val success = viewModel.state.value
        assertTrue("Expected Success, got $success", success is UiState.Success)
        assertEquals(1, (success as UiState.Success).data.size)

        // 3) Empty — a genuinely empty list is its own distinct state, never a Success
        // wrapping an empty collection.
        repository.emit(emptyList())
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue("Expected Empty, got ${viewModel.state.value}", viewModel.state.value is UiState.Empty)

        // 4) Error — a real failed load (not swallowed) surfaces a user-safe message.
        repository.failNextRefresh = true
        viewModel.load()
        testDispatcher.scheduler.advanceUntilIdle()
        val error = viewModel.state.value
        assertTrue("Expected Error, got $error", error is UiState.Error)
        assertEquals("upstream unavailable", (error as UiState.Error).message)
    }
}
