package com.somuleco.creator.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Synchronous access to the current Firebase ID token, for use from OkHttp's synchronous
 * [okhttp3.Interceptor] callback ([AuthInterceptor], which always runs off the main thread).
 * Kept as an interface — rather than [AuthTokenProviderImpl] depending on [FirebaseAuth]
 * directly — so it can be unit tested with a fake implementation instead of a real Firebase
 * instance (Foundation Wave 08, plan §7.4 task 4). Mirrors Somuleco Connect's own
 * `data/network/FirebaseIdTokenProvider.kt` near-verbatim; this is a proven pattern, not a new
 * design.
 */
fun interface FirebaseIdTokenProvider {
    /** Returns the current user's ID token, or null if signed out or the fetch failed.
     * [forceRefresh] requests a freshly-minted token rather than a locally cached one - reserved
     * for expired-session recovery after a 401 (not yet wired into a retry-on-401 path; see the
     * Wave 08 completion report's remaining-issues section). */
    fun getIdToken(forceRefresh: Boolean): String?
}

@Singleton
class FirebaseAuthIdTokenProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseIdTokenProvider {
    override fun getIdToken(forceRefresh: Boolean): String? {
        val user = firebaseAuth.currentUser ?: return null
        return try {
            Tasks.await(user.getIdToken(forceRefresh))?.token
        } catch (e: ExecutionException) {
            null
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            null
        }
    }
}
