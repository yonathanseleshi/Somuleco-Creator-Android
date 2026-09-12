package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One of the per-domain repositories that replace `object CreatorRepository` (plan
 * §7.3 task 4). `authState`/`isCreatorMode` are read-only mirrors of
 * [AppSessionState] — the actual cross-app session holder — so this repository does
 * not duplicate mutable session state; it exposes the mock login/signup identity
 * seam (Wave 08 replaces the implementation, not this interface).
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val appSessionState: AppSessionState,
    private val dataSource: MockCreatorDataSource
) : AuthRepository {

    override val authState: StateFlow<AuthState> = appSessionState.authState
    override val currentUser: StateFlow<UserIdentity> = dataSource.currentUser
    override val isCreatorMode: StateFlow<Boolean> = appSessionState.isCreatorMode

    override suspend fun login(email: String, password: String): Result<UserIdentity> {
        val user = dataSource.currentUser.value
        appSessionState.setAuthState(
            AuthState.Authenticated(user = user, isCreator = user.isCreator, hasCompletedOnboarding = true)
        )
        return Result.success(user)
    }

    override suspend fun signup(name: String, email: String, password: String): Result<UserIdentity> {
        val newUser = UserIdentity(
            id = "usr_${System.currentTimeMillis()}",
            displayName = name,
            username = name.lowercase().replace(" ", "_"),
            email = email,
            isCreator = true
        )
        dataSource.setCurrentUser(newUser)
        appSessionState.setAuthState(AuthState.Authenticated(user = newUser, isCreator = true, hasCompletedOnboarding = false))
        return Result.success(newUser)
    }

    override suspend fun verifyEmail(code: String): Result<Boolean> = Result.success(true)
    override suspend fun forgotPassword(email: String): Result<Boolean> = Result.success(true)
    override suspend fun resetPassword(token: String, newPassword: String): Result<Boolean> = Result.success(true)

    override suspend fun logout() {
        appSessionState.logout()
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
