package com.somuleco.creator.feature.creator.onboarding

import androidx.lifecycle.ViewModel
import com.somuleco.creator.core.session.AppSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Onboarding/auth screens are out of the named Wave 05 migration scope (they are owned
 * by Wave 08's authentication work), but still must not reach a deleted singleton. */
@HiltViewModel
class CreatorOnboardingViewModel @Inject constructor(
    private val appSessionState: AppSessionState
) : ViewModel() {
    fun setCreatorMode(enabled: Boolean) = appSessionState.setCreatorMode(enabled)
}
