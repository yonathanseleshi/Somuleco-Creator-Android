package com.somuleco.creator.feature.creator.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.AnalyticsOverview
import com.somuleco.creator.data.model.TopContentPerformance
import com.somuleco.creator.data.repository.interfaces.AudienceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiModel(
    val analytics: AnalyticsOverview,
    val topContent: List<TopContentPerformance>
)

/** Backs the Analytics screen (plan §7.3 task 7, Creator core loop). */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    audienceRepository: AudienceRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<AnalyticsUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<AnalyticsUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(audienceRepository.analytics, audienceRepository.topContent) { analytics, topContent ->
                UiState.Success(AnalyticsUiModel(analytics, topContent))
            }.collect { _state.value = it }
        }
    }
}
