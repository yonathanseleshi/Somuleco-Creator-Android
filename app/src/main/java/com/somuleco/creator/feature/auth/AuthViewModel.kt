package com.somuleco.creator.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Local, per-screen form state for the auth screens — distinct from the app-wide
 * [com.somuleco.creator.data.model.AuthState] on [com.somuleco.creator.core.session.AppSessionState],
 * which [AuthViewModel] does not duplicate: on success, the real navigation transition is driven
 * by that shared state changing to `Authenticated` (`SomulecoApp`'s `LaunchedEffect(authState)`),
 * not by anything here. This state exists only to drive the button's loading spinner and surface
 * a real error message in place of the removed mock demo-login affordances. */
sealed interface AuthFormState {
    data object Idle : AuthFormState
    data object Loading : AuthFormState
    data class Error(val message: String) : AuthFormState
}

/**
 * Foundation Wave 08 (plan §7.4, acceptance criterion 9 — "the dev-shell / mock demo-login
 * paths are gone from every client's UI"): backs [LoginScreen]/[SignupScreen]/
 * [ForgotPasswordScreen]/[VerifyEmailScreen] against the real [AuthRepository] (Firebase-backed
 * as of this wave) instead of those screens invoking no repository at all, as they did before
 * (the "Sign In"/"Create Account" buttons previously called their `onLoginSuccess`/
 * `onSignupSuccess` navigation callbacks directly, with zero credential verification).
 *
 * Constructed against the [AuthRepository] *interface* only, so a fake implementation can be
 * substituted in a unit test with no ViewModel code change (`AuthViewModelTest`), the same
 * substitutability shape `RepositorySubstitutabilityTest` already proves for
 * [com.somuleco.creator.feature.creator.channels.ChannelsViewModel].
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow<AuthFormState>(AuthFormState.Idle)
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _formState.value = AuthFormState.Loading
        viewModelScope.launch {
            authRepository.login(email, password)
                .onSuccess {
                    _formState.value = AuthFormState.Idle
                    onSuccess()
                }
                .onFailure { _formState.value = AuthFormState.Error(it.message ?: "Sign in failed") }
        }
    }

    fun signup(name: String, email: String, password: String, onSuccess: () -> Unit) {
        _formState.value = AuthFormState.Loading
        viewModelScope.launch {
            authRepository.signup(name, email, password)
                .onSuccess {
                    _formState.value = AuthFormState.Idle
                    onSuccess()
                }
                .onFailure { _formState.value = AuthFormState.Error(it.message ?: "Sign up failed") }
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        _formState.value = AuthFormState.Loading
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    _formState.value = AuthFormState.Idle
                    onSuccess()
                }
                .onFailure { _formState.value = AuthFormState.Error(it.message ?: "Google sign-in failed") }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        _formState.value = AuthFormState.Loading
        viewModelScope.launch {
            authRepository.forgotPassword(email)
                .onSuccess {
                    _formState.value = AuthFormState.Idle
                    onSuccess()
                }
                .onFailure { _formState.value = AuthFormState.Error(it.message ?: "Could not send reset email") }
        }
    }

    fun resendEmailVerification() {
        viewModelScope.launch { authRepository.resendEmailVerification() }
    }
}
