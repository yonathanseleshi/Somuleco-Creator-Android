package com.somuleco.creator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class StaticChannelRepository(channels: List<ChannelSummary>) : ChannelRepository {
    private val _channels = MutableStateFlow(channels)
    override val channels: StateFlow<List<ChannelSummary>> = _channels.asStateFlow()
    override suspend fun refresh() {}
    override fun getChannel(id: String): ChannelSummary? = _channels.value.find { it.id == id }
    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary =
        throw UnsupportedOperationException()
    override fun toggleFollowChannel(channelId: String) {}
}

private fun channel(id: String) = ChannelSummary(
    id = id, creatorAccountId = "acc", name = id, slug = id, handle = "@$id",
    description = "d", category = "c", followersCount = 0, subscribersCount = 0, contentCount = 0
)

/**
 * Closes the Wave 04 gap recorded in the Wave 05 plan §2.5(1): mode + Channel context
 * previously lived in a plain in-memory `MutableStateFlow` on `CreatorRepository` and did
 * not survive process death, despite Wave 04 reporting persistence as implemented. This
 * test proves [AppSessionState] now persists both through DataStore and survives a
 * simulated process restart — a fresh [AppSessionState] instance pointed at the same
 * on-disk DataStore file (same [Context]) is exactly that simulation, since the real
 * in-memory object is destroyed and recreated from scratch either way.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppSessionStatePersistenceTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun modeAndChannelSelection_surviveASimulatedProcessRestart() = runBlocking {
        val channelRepo = StaticChannelRepository(listOf(channel("ch_1"), channel("ch_2")))

        val firstProcess = AppSessionState(context, channelRepo)
        // Let the constructor's hydration coroutine (launched on a real IO dispatcher, not
        // a test dispatcher here) settle before asserting the pre-restart baseline.
        kotlinx.coroutines.delay(50)

        firstProcess.setCreatorMode(false)
        firstProcess.selectChannel("ch_2")
        // Give the DataStore writes (launched on their own coroutine) time to land.
        kotlinx.coroutines.delay(200)

        // Simulate process death: a brand-new AppSessionState instance is exactly what
        // happens on relaunch — the previous in-memory object is gone, and only the
        // on-disk DataStore file (scoped to the same Context) remains.
        val secondProcess = AppSessionState(context, channelRepo)
        kotlinx.coroutines.delay(200)

        assertEquals(false, secondProcess.isCreatorMode.value)
        assertEquals("ch_2", secondProcess.selectedChannelId.value)
    }

    @Test
    fun missingChannelFallback_resolvesToNull_whenPersistedChannelNoLongerExists() = runBlocking {
        val fullChannelSet = StaticChannelRepository(listOf(channel("ch_1"), channel("ch_stale")))
        val firstProcess = AppSessionState(context, fullChannelSet)
        kotlinx.coroutines.delay(50)
        firstProcess.selectChannel("ch_stale")
        kotlinx.coroutines.delay(200)

        // On "restart", the channel that was selected no longer exists in the current
        // channel list (deleted, or mock seed data changed) — v0.1-navigation-semantics.md
        // §4.3 requires falling back to null ("All Channels"), never a stale reference.
        val reducedChannelSet = StaticChannelRepository(listOf(channel("ch_1")))
        val secondProcess = AppSessionState(context, reducedChannelSet)
        kotlinx.coroutines.delay(200)

        assertNull(secondProcess.selectedChannelId.value)
    }
}
