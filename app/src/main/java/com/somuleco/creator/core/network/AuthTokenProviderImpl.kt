package com.somuleco.creator.core.network

import com.somuleco.creator.core.session.AppSessionState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real [AuthTokenProvider] implementation (Foundation Wave 08, plan §7.4 task 4). Sources the
 * bearer token directly from [FirebaseIdTokenProvider] — a real Firebase ID token — replacing
 * the Wave 05 placeholder ("mock-session-token:$userId") that was derived from
 * [AppSessionState]'s mock identity. [AuthInterceptor] itself required **no change**: it was
 * already reading through this [AuthTokenProvider] seam correctly; only the token source needed
 * to become real.
 */
@Singleton
class AuthTokenProviderImpl @Inject constructor(
    private val appSessionState: AppSessionState,
    private val firebaseIdTokenProvider: FirebaseIdTokenProvider
) : AuthTokenProvider {

    override fun getAuthToken(): String? = firebaseIdTokenProvider.getIdToken(forceRefresh = false)

    override fun getActiveChannelId(): String? = appSessionState.selectedChannelId.value
}
