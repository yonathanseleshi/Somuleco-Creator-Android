package com.somuleco.creator

import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import com.somuleco.creator.feature.auth.AuthFormState
import com.somuleco.creator.feature.auth.AuthViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Fake [AuthRepository] — [AuthViewModel] is constructed against the [AuthRepository]
 * *interface* only (constructor injection, the same shape Hilt uses at runtime via
 * [com.somuleco.creator.data.repository.impl.FirebaseAuthRepository]), so this fake substitutes
 * for the real Firebase-backed implementation with no ViewModel code change — the same
 * substitutability shape `RepositorySubstitutabilityTest` already proves for
 * `ChannelsViewModel`/`ChannelRepository`. This never touches a real
 * [com.google.firebase.auth.FirebaseAuth] instance or the Firebase Local Emulator Suite; see the
 * Wave 08 completion report for what *is* validated against the emulator vs. only unit-tested
 * here.
 */
private class FakeAuthRepository(
    private val loginResult: Result<UserIdentity>? = null,
    private val signupResult: Result<UserIdentity>? = null,
    private val googleResult: Result<UserIdentity>? = null,
    private val forgotPasswordResult: Result<Boolean>? = null
) : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow(UserIdentity.unauthenticatedPlaceholder())
    override val currentUser: StateFlow<UserIdentity> = _currentUser.asStateFlow()

    private val _isCreatorMode = MutableStateFlow(false)
    override val isCreatorMode: StateFlow<Boolean> = _isCreatorMode.asStateFlow()

    var loginCallCount = 0
    var signupCallCount = 0
    var googleCallCount = 0
    var forgotPasswordCallCount = 0
    var logoutCallCount = 0

    override suspend fun login(email: String, password: String): Result<UserIdentity> {
        loginCallCount++
        val result = loginResult ?: Result.failure(IllegalStateException("no result configured"))
        result.onSuccess { user ->
            _currentUser.value = user
            _authState.value = AuthState.Authenticated(user = user, isCreator = true, hasCompletedOnboarding = true)
        }
        return result
    }

    override suspend fun signup(name: String, email: String, password: String): Result<UserIdentity> {
        signupCallCount++
        val result = signupResult ?: Result.failure(IllegalStateException("no result configured"))
        result.onSuccess { user -> _currentUser.value = user }
        return result
    }

    override suspend fun loginWithGoogle(idToken: String): Result<UserIdentity> {
        googleCallCount++
        val result = googleResult ?: Result.failure(IllegalStateException("no result configured"))
        result.onSuccess { user ->
            _currentUser.value = user
            _authState.value = AuthState.Authenticated(user = user, isCreator = true, hasCompletedOnboarding = true)
        }
        return result
    }

    override suspend fun verifyEmail(code: String): Result<Boolean> = Result.success(true)

    override suspend fun forgotPassword(email: String): Result<Boolean> {
        forgotPasswordCallCount++
        return forgotPasswordResult ?: Result.success(true)
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> = Result.success(true)

    override suspend fun resendEmailVerification(): Result<Unit> = Result.success(Unit)

    override suspend fun logout() {
        logoutCallCount++
        _authState.value = AuthState.Unauthenticated
    }

    override fun setCreatorMode(enabled: Boolean) { _isCreatorMode.value = enabled }
    override fun toggleCreatorMode() { _isCreatorMode.value = !_isCreatorMode.value }
    override fun completeOnboarding() {}
}

private fun fakeUser(id: String = "uid_123") =
    UserIdentity(id = id, displayName = "Test User", username = "testuser", email = "test@example.com", isCreator = true)

/**
 * Foundation Wave 08 required test (plan §7.4 "Required tests" per the task brief): proves
 * [AuthViewModel] reacts correctly to [AuthRepository] state/result changes via a fake
 * implementation — the substitutability test this wave's task brief calls for, mirroring
 * `RepositorySubstitutabilityTest`'s existing pattern in this repo (Somuleco Connect's own
 * `FakeAuthRepository`/`AuthViewModelTest` shape, adapted to Creator's [AuthRepository]
 * interface, which differs from Connect's).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

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
    fun login_onSuccess_movesToIdle_invokesCallback_andUpdatesRepositoryAuthState() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(loginResult = Result.success(fakeUser()))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        assertEquals(AuthFormState.Idle, viewModel.formState.value)
        viewModel.login("test@example.com", "password123") { successInvoked = true }
        assertEquals(AuthFormState.Loading, viewModel.formState.value)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(AuthFormState.Idle, viewModel.formState.value)
        assertTrue(successInvoked)
        assertEquals(1, fake.loginCallCount)
        assertTrue(fake.authState.value is AuthState.Authenticated)
    }

    @Test
    fun login_onFailure_surfacesErrorState_andNeverInvokesSuccessCallback() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(loginResult = Result.failure(RuntimeException("invalid-credential")))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        viewModel.login("test@example.com", "wrong-password") { successInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.formState.value is AuthFormState.Error)
        assertEquals("invalid-credential", (viewModel.formState.value as AuthFormState.Error).message)
        assertFalse(successInvoked)
        assertEquals(AuthState.Unknown, fake.authState.value)
    }

    @Test
    fun signup_onSuccess_invokesCallback() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(signupResult = Result.success(fakeUser(id = "uid_new")))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        viewModel.signup("Test User", "test@example.com", "password123") { successInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successInvoked)
        assertEquals(1, fake.signupCallCount)
    }

    @Test
    fun loginWithGoogle_onSuccess_updatesAuthStateAndInvokesCallback() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(googleResult = Result.success(fakeUser(id = "uid_google")))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        viewModel.loginWithGoogle("fake-id-token") { successInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successInvoked)
        assertEquals(1, fake.googleCallCount)
        assertTrue(fake.authState.value is AuthState.Authenticated)
    }

    @Test
    fun loginWithGoogle_onFailure_surfacesError() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(googleResult = Result.failure(RuntimeException("google sign-in cancelled")))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        viewModel.loginWithGoogle("fake-id-token") { successInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(successInvoked)
        assertTrue(viewModel.formState.value is AuthFormState.Error)
    }

    @Test
    fun forgotPassword_onSuccess_invokesCallback() = runTest(testDispatcher) {
        val fake = FakeAuthRepository(forgotPasswordResult = Result.success(true))
        val viewModel = AuthViewModel(fake)
        var successInvoked = false

        viewModel.forgotPassword("test@example.com") { successInvoked = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successInvoked)
        assertEquals(1, fake.forgotPasswordCallCount)
    }
}
