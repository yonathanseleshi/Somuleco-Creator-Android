package com.somuleco.creator.feature.creator.settings

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.model.CreatorSettingsData
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import com.somuleco.creator.data.repository.interfaces.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class CreatorSettingsViewModel @Inject constructor(
    creatorProfileRepository: CreatorProfileRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val profile: StateFlow<CreatorProfileInfo> = creatorProfileRepository.creatorProfile
    val settings: StateFlow<CreatorSettingsData> = settingsRepository.settings

    fun updateSettings(settings: CreatorSettingsData) = settingsRepository.updateSettings(settings)
}
