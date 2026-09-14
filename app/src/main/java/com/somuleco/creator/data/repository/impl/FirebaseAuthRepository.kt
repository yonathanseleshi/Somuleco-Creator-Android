package com.somuleco.creator.data.repository.impl

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.somuleco.creator.core.network.CoreApiService
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real Somuleco identity/session implementation of [AuthRepository] (Foundation Wave 08, plan
 * §7.4 task 3), backed by Firebase Authentication (the ecosystem's shared identity provider,
 * `somuleco-firebase`) for credentials, and this API's `GET /me` bootstrap endpoint for the
 * app's own `InternalUser` context. Replaces [AuthRepositoryImpl]'s mock login/signup, which
 * ignored submitted credentials entirely. Mirrors Somuleco Connect's own
 * `data/repository/FirebaseAuthRepository.kt` structure — proven, working, adapted only for
 * Creator's existing [AuthRepository] interface shape and its [AppSessionState]-owned
 * [authState]/[isCreatorMode] (Creator's architecture keeps that state on [AppSessionState]
 * rather than duplicating it on this repository, unlike Connect's own StateFlow-per-repository
 * shape — see [AuthRepositoryImpl], the mock this replaces, which already delegated the same way).
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val coreApiService: CoreApiService,
    private val appSessionState: AppSessionState
) : AuthRepository {

    // A repository-lifetime scope (this class is a Hilt @Singleton, force-constructed at process
    // start by SomulecoApplication.onCreate — see that file's doc comment) for reacting to
    // Firebase's AuthStateListener callback, which is not itself a suspend function.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow(UserIdentity.unauthenticatedPlaceholder())
    override val currentUser: StateFlow<UserIdentity> = _currentUser.asStateFlow()

    // AppSessionState is the single source of truth for authState/isCreatorMode across this
    // app (Wave 05) — this repository mirrors it rather than owning a second, parallel StateFlow,
    // exactly as the mock AuthRepositoryImpl it replaces already did.
    override val authState: StateFlow<AuthState> = appSessionState.authState
    override val isCreatorMode: StateFlow<Boolean> = appSessionState.isCreatorMode

    init {
        // Cold-start / session-restoration path: Firebase persists sessions locally on Android,
        // so this listener is what resolves AppSessionState's Unknown starting value (see
        // AppSessionState's own doc comment) to Authenticated/Unauthenticated once Firebase has
        // checked for a restorable session — not synchronously, hence AppSessionState starts
        // Unknown rather than assuming either outcome.
        firebaseAuth.addAuthStateListener { auth -> onFirebaseAuthStateChanged(auth.currentUser) }
    }

    private fun onFirebaseAuthStateChanged(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            _currentUser.value = UserIdentity.unauthenticatedPlaceholder()
            // appSessionState.logout() also resets Creator mode + clears Channel selection —
            // correct here too: a session ending via a sign-out on another surface (token
            // revocation, Firebase console) should reset exactly like an explicit logout() call.
            appSessionState.logout()
            return
        }
        repositoryScope.launch { bootstrapAndApply() }
    }

    /** Calls this API's `GET /me` bootstrap endpoint for the currently Firebase-signed-in user,
     * and applies the result to [_currentUser]/[AppSessionState.authState]. Used both by the
     * cold-start listener above (fire-and-forget) and by the explicit sign-in/sign-up/Google
     * methods below (awaited, so they can return a real [Result] to their caller). Firing it
     * from both places means a successful explicit sign-in bootstraps twice (once from the
     * listener, once from the explicit call) — a harmless, idempotent redundancy per
     * `v0.1-identity-foundation.md` §5, not a correctness issue. */
    private suspend fun bootstrapAndApply(): Result<UserIdentity> = withContext(Dispatchers.IO) {
        val result = runCatching {
            val firebaseUser = firebaseAuth.currentUser ?: error("No signed-in Firebase user")
            val response = coreApiService.getMe()
            if (!response.isSuccessful) {
                error("Identity bootstrap failed: HTTP ${response.code()}")
            }
            val dto = response.body()?.data ?: error("Empty /me response body")
            UserIdentity(
                id = dto.userId,
                displayName = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: firebaseUser.email
                    ?: dto.userId,
                username = firebaseUser.email?.substringBefore("@") ?: dto.userId,
                email = firebaseUser.email.orEmpty(),
                // Creator Account activation is explicitly out of Wave 08's scope
                // (`v0.1-identity-foundation.md` §6) — every bootstrapped identity is modeled
                // as a Creator here only in the sense that this is the Creator app; whether a
                // real CreatorAccount exists is a separate, later `GET /creator-account` call
                // this repository does not make.
                isCreator = true
            )
        }
        result.onSuccess { user ->
            _currentUser.value = user
            appSessionState.setAuthState(
                AuthState.Authenticated(user = user, isCreator = true, hasCompletedOnboarding = true)
            )
        }.onFailure {
            // Firebase itself verified the credential, but this API's own bootstrap failed
            // (network error, unexpected response shape, etc.) — treat the session as
            // unauthenticated rather than leaving AppSessionState stuck; retrying sign-in
            // re-triggers this same path.
            appSessionState.setAuthState(AuthState.Unauthenticated)
        }
        result
    }

    override suspend fun login(email: String, password: String): Result<UserIdentity> =
        withContext(Dispatchers.IO) {
            val signInResult =
                runCatching { Tasks.await(firebaseAuth.signInWithEmailAndPassword(email, password)) }
            signInResult.exceptionOrNull()?.let { return@withContext Result.failure(it) }
            bootstrapAndApply()
        }

    override suspend fun signup(name: String, email: String, password: String): Result<UserIdentity> =
        withContext(Dispatchers.IO) {
            val createResult =
                runCatching { Tasks.await(firebaseAuth.createUserWithEmailAndPassword(email, password)) }
            createResult.exceptionOrNull()?.let { return@withContext Result.failure(it) }

            runCatching {
                firebaseAuth.currentUser?.let { user ->
                    val update = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    Tasks.await(user.updateProfile(update))
                }
            }
            // Fire the initial verification email right after account creation, best-effort —
            // a failure here should not fail the signup itself.
            runCatching { firebaseAuth.currentUser?.let { Tasks.await(it.sendEmailVerification()) } }

            bootstrapAndApply()
        }

    override suspend fun loginWithGoogle(idToken: String): Result<UserIdentity> =
        withContext(Dispatchers.IO) {
            val signInResult = runCatching {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                Tasks.await(firebaseAuth.signInWithCredential(credential))
            }
            signInResult.exceptionOrNull()?.let { return@withContext Result.failure(it) }
            bootstrapAndApply()
        }

    override suspend fun verifyEmail(code: String): Result<Boolean> = withContext(Dispatchers.IO) {
        // Firebase's own email verification is link-based (an emailed oobCode consumed via a
        // deep link, not a typed numeric code) — there is no client API to "verify" an
        // arbitrary user-entered code against it. Reload the current user and report their real
        // emailVerified flag instead of unconditionally succeeding, as the previous mock did.
        val user = firebaseAuth.currentUser ?: return@withContext Result.success(false)
        runCatching { Tasks.await(user.reload()) }
        Result.success(user.isEmailVerified)
    }

    override suspend fun forgotPassword(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching { Tasks.await(firebaseAuth.sendPasswordResetEmail(email)) }.map { true }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            // [token] is the oobCode from the emailed password-reset link (Firebase's real
            // confirmPasswordReset API) — not an app-issued token.
            runCatching { Tasks.await(firebaseAuth.confirmPasswordReset(token, newPassword)) }.map { true }
        }

    override suspend fun resendEmailVerification(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val user = firebaseAuth.currentUser ?: error("No signed-in user")
            Tasks.await(user.sendEmailVerification())
        }.map { }
    }

    override suspend fun logout() {
        // firebaseAuth.signOut() synchronously notifies the AuthStateListener above, which
        // resets AppSessionState back to Unauthenticated/mode-off/channel-cleared via
        // appSessionState.logout() — a subsequent call to a protected NestJS endpoint then has
        // no token to attach (AuthTokenProviderImpl -> FirebaseIdTokenProvider.getIdToken()
        // returns null once firebaseAuth.currentUser is null).
        withContext(Dispatchers.IO) { firebaseAuth.signOut() }
    }

    override fun setCreatorMode(enabled: Boolean) = appSessionState.setCreatorMode(enabled)
    override fun toggleCreatorMode() = appSessionState.toggleCreatorMode()

    override fun completeOnboarding() {
        val current = appSessionState.authState.value
        if (current is AuthState.Authenticated) {
            appSessionState.setAuthState(current.copy(hasCompletedOnboarding = true))
        }
    }
}
