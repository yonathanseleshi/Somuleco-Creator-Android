package com.somuleco.creator.core.network

import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.AuthState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real [AuthTokenProvider] implementation reading session state
 * (plan §7.3 task 9 — this interface previously had zero implementations). There is no
 * real token yet (Wave 08 owns real authentication) — this derives a placeholder bearer
 * value from the current mock identity so the header-attachment mechanics in
 * [AuthInterceptor] are exercised end to end, and Wave 08 only needs to swap the token
 * source, not build the interceptor plumbing.
 */
@Singleton
class AuthTokenProviderImpl @Inject constructor(
    private val appSessionState: AppSessionState
) : AuthTokenProvider {

    override fun getAuthToken(): String? {
        val state = appSessionState.authState.value
        return (state as? AuthState.Authenticated)?.user?.id?.let { userId -> "mock-session-token:$userId" }
    }

    override fun getActiveChannelId(): String? = appSessionState.selectedChannelId.value
}
