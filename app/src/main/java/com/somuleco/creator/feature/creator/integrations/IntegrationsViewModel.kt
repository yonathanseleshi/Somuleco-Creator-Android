package com.somuleco.creator.feature.creator.integrations

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.CreatorIntegration
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class IntegrationsViewModel @Inject constructor(
    private val creatorProfileRepository: CreatorProfileRepository
) : ViewModel() {
    val integrations: StateFlow<List<CreatorIntegration>> = creatorProfileRepository.integrations
    fun toggleIntegration(id: String) = creatorProfileRepository.toggleIntegration(id)
}
