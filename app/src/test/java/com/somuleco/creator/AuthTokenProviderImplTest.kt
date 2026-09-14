package com.somuleco.creator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.somuleco.creator.core.network.AuthTokenProviderImpl
import com.somuleco.creator.core.network.FirebaseIdTokenProvider
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class EmptyChannelRepository : ChannelRepository {
    private val _channels = MutableStateFlow<List<ChannelSummary>>(emptyList())
    override val channels: StateFlow<List<ChannelSummary>> = _channels.asStateFlow()
    override suspend fun refresh() {}
    override fun getChannel(id: String): ChannelSummary? = null
    override fun createChannel(name: String, description: String, category: String, handle: String): ChannelSummary =
        throw UnsupportedOperationException()
    override fun toggleFollowChannel(channelId: String) {}
}

/**
 * Foundation Wave 08 (plan §7.4 task 4): proves [AuthTokenProviderImpl] now sources its bearer
 * token from a real Firebase ID token seam ([FirebaseIdTokenProvider]) instead of synthesizing
 * `"mock-session-token:$userId"` from [AppSessionState]'s mock identity —
 * [com.somuleco.creator.core.network.AuthInterceptor] itself needed no change (it already reads
 * through [com.somuleco.creator.core.network.AuthTokenProvider] correctly; only the token
 * *source* changed), so this test is the proof of that source change.
 *
 * No real [com.google.firebase.auth.FirebaseAuth] instance or Firebase Local Emulator Suite is
 * involved here — [FirebaseIdTokenProvider] is a `fun interface` specifically so it can be faked
 * with a plain lambda, mirroring Somuleco Connect's own `FirebaseIdTokenProvider` test seam. This
 * is a unit-level test, disclosed as such; see the Wave 08 completion report for what was (and
 * was not) exercised against a real/emulator Firebase instance.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthTokenProviderImplTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun getAuthToken_returnsTheRealFirebaseIdToken_notAMockSessionString() = runBlocking {
        val appSessionState = AppSessionState(context, EmptyChannelRepository())
        delay(50)

        var forceRefreshRequested: Boolean? = null
        val fakeTokenProvider = FirebaseIdTokenProvider { forceRefresh ->
            forceRefreshRequested = forceRefresh
            "real.firebase.id.token"
        }
        val provider = AuthTokenProviderImpl(appSessionState, fakeTokenProvider)

        val token = provider.getAuthToken()

        assertEquals("real.firebase.id.token", token)
        assertEquals(false, forceRefreshRequested)
        // The old placeholder shape is gone entirely, not merely renamed.
        assertFalse(token!!.startsWith("mock-session-token:"))
    }

    @Test
    fun getAuthToken_returnsNull_whenNoFirebaseUserIsSignedIn() = runBlocking {
        val appSessionState = AppSessionState(context, EmptyChannelRepository())
        delay(50)

        val fakeTokenProvider = FirebaseIdTokenProvider { null }
        val provider = AuthTokenProviderImpl(appSessionState, fakeTokenProvider)

        assertNull(provider.getAuthToken())
    }

    @Test
    fun getActiveChannelId_stillDelegatesToAppSessionState() = runBlocking {
        val appSessionState = AppSessionState(context, EmptyChannelRepository())
        delay(50)
        appSessionState.selectChannel("ch_9")
        delay(100)

        val provider = AuthTokenProviderImpl(appSessionState, FirebaseIdTokenProvider { "t" })

        assertEquals("ch_9", provider.getActiveChannelId())
    }
}
