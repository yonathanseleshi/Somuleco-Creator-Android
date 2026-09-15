package com.somuleco.creator.feature.creator.onboarding

import androidx.lifecycle.ViewModel
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.repository.interfaces.CreatorAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Onboarding/auth screens are out of the named Wave 05 migration scope (they are owned
 * by Wave 08's authentication work), but still must not reach a deleted singleton. */
@HiltViewModel
class CreatorOnboardingViewModel @Inject constructor(
    private val appSessionState: AppSessionState,
    private val creatorAccountRepository: CreatorAccountRepository
) : ViewModel() {
    fun setCreatorMode(enabled: Boolean) = appSessionState.setCreatorMode(enabled)

    /**
     * Foundation Wave 09 (`v0.1-creator-authorization.md` §2-3, plan §7.4 task 5). This mock
     * wizard's final step previously only called [setCreatorMode] — flipping the local
     * presentation-only mode chip with no real Creator Account behind it, exactly the
     * "mode toggle = activated" gap this wave closes. It now performs a real `POST
     * /creator-account` + `POST /creator-account/:id/complete-onboarding` so the real,
     * selected-account-driven navigation gate (`AppSessionState.isCreatorAccountActive`, read
     * by `navigateFromShellSafe`) reflects genuine server state once this flow finishes, not
     * just the mode chip. Best-effort: this wizard is a pre-existing local-only marketing flow
     * (not this wave's primary activation surface — see `CreatorAccountScreen`, which has its
     * own explicit error UI), so a failure here is swallowed by the caller rather than blocking
     * the wizard's completion.
     */
    // `creatorType` is the ONLY field the real NestJS `CreateCreatorAccountDto` accepts
    // (`@IsNotEmpty creatorType: string`, verified against
    // `src/creator-account/dto/create-creator-account.dto.ts`); `forbidNonWhitelisted: true`
    // rejects anything else. An earlier draft of this call sent accountName/displayName/
    // description, which is guaranteed to 400 against the real API.
    suspend fun activateRealCreatorAccount(creatorType: String): Result<Unit> {
        val account = creatorAccountRepository.activate(creatorType)
            .getOrElse { return Result.failure(it) }
        val completed = creatorAccountRepository.completeOnboarding(account.id)
            .getOrElse { return Result.failure(it) }
        appSessionState.selectCreatorAccount(completed.id)
        appSessionState.setCreatorAccountActive(completed.status == "active")
        return Result.success(Unit)
    }
}
