package com.somuleco.creator.di

import com.somuleco.creator.core.di.AuthRepositoryModule
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Foundation Wave 08: an in-memory [AuthRepository] fake for Hilt+Robolectric Compose UI tests
 * (`NavigationFlowTest`). Replaces the real Firebase-backed [com.somuleco.creator.data.
 * repository.impl.FirebaseAuthRepository] for the whole `DebugUnitTest` Hilt component — none of
 * these tests can reach the Firebase Local Emulator Suite or a real Firebase project from a
 * Robolectric process, and `NavigationFlowTest` predates Wave 08, asserting shell/navigation
 * behavior that has nothing to do with credential verification itself. This mirrors the
 * pre-Wave-08 mock's "login always succeeds as the current session's user" shape, scoped to
 * tests only — real authentication *behavior* is covered separately by `AuthViewModelTest`
 * (fake-repository substitutability) and `AuthTokenProviderImplTest` (fake Firebase ID token
 * seam), neither of which touches this class.
 *
 * Delegates authState/isCreatorMode through [AppSessionState] exactly like
 * [com.somuleco.creator.data.repository.impl.FirebaseAuthRepository] does in production — this
 * matters because `MainActivity`/`SomulecoApp` read [AppSessionState.authState] directly (not
 * `AuthRepository.authState`) to pick the nav graph's start destination; an AuthRepository fake
 * with its own independent StateFlow would leave that navigation-driving state stuck at
 * `AuthState.Unknown` forever, which was a real bug caught by this file's first draft (every
 * `NavigationFlowTest` assertion failed until this delegation was added, since the shell never
 * left the auth graph).
 */
@Singleton
class FakeAuthRepositoryForTests @Inject constructor(
    private val appSessionState: AppSessionState
) : AuthRepository {

    private val _currentUser = MutableStateFlow(testUser)
    override val currentUser: StateFlow<UserIdentity> = _currentUser.asStateFlow()

    override val authState: StateFlow<AuthState> = appSessionState.authState
    override val isCreatorMode: StateFlow<Boolean> = appSessionState.isCreatorMode

    init {
        // Boots pre-authenticated, matching this repo's pre-Wave-08 behavior — NavigationFlowTest
        // asserts shell/navigation behavior starting from an already-authenticated session, not
        // the sign-in flow itself (AuthViewModelTest/AuthTokenProviderImplTest cover that).
        appSessionState.setAuthState(
            AuthState.Authenticated(user = testUser, isCreator = true, hasCompletedOnboarding = true)
        )
    }

    override suspend fun login(email: String, password: String): Result<UserIdentity> {
        appSessionState.setAuthState(
            AuthState.Authenticated(user = _currentUser.value, isCreator = true, hasCompletedOnboarding = true)
        )
        return Result.success(_currentUser.value)
    }

    override suspend fun signup(name: String, email: String, password: String): Result<UserIdentity> {
        val user = UserIdentity(
            id = "usr_test_${System.currentTimeMillis()}",
            displayName = name,
            username = name.lowercase().replace(" ", "_"),
            email = email,
            isCreator = true
        )
        _currentUser.value = user
        appSessionState.setAuthState(AuthState.Authenticated(user = user, isCreator = true, hasCompletedOnboarding = false))
        return Result.success(user)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<UserIdentity> = login(_currentUser.value.email, "")

    override suspend fun verifyEmail(code: String): Result<Boolean> = Result.success(true)
    override suspend fun forgotPassword(email: String): Result<Boolean> = Result.success(true)
    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> = Result.success(true)
    override suspend fun resendEmailVerification(): Result<Unit> = Result.success(Unit)

    override suspend fun logout() = appSessionState.logout()

    override fun setCreatorMode(enabled: Boolean) = appSessionState.setCreatorMode(enabled)
    override fun toggleCreatorMode() = appSessionState.toggleCreatorMode()

    override fun completeOnboarding() {
        val current = appSessionState.authState.value
        if (current is AuthState.Authenticated) {
            appSessionState.setAuthState(current.copy(hasCompletedOnboarding = true))
        }
    }

    companion object {
        private val testUser = UserIdentity(
            id = "usr_elena",
            displayName = "Elena Rostova",
            username = "elenarostova",
            email = "elena@rostovaphoto.com",
            isCreator = true
        )
    }
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AuthRepositoryModule::class])
abstract class TestAuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepositoryForTests): AuthRepository
}
