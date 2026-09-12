package com.somuleco.creator.feature.creator.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.AnalyticsOverview
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.CreatorGoal
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.model.CreatorRecommendationCard
import com.somuleco.creator.data.model.TopContentPerformance
import com.somuleco.creator.data.model.TransactionItem
import com.somuleco.creator.data.repository.interfaces.AudienceRepository
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.CreatorAIRepository
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import com.somuleco.creator.data.repository.interfaces.RevenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreatorDashboardUiModel(
    val profile: CreatorProfileInfo,
    val analytics: AnalyticsOverview,
    val recommendations: List<CreatorRecommendationCard>,
    val topContent: List<TopContentPerformance>,
    val transactions: List<TransactionItem>,
    val goals: List<CreatorGoal>,
    val channels: List<ChannelSummary>,
    val selectedChannelId: String?
)

/**
 * Backs the Creator Dashboard (Creator core loop, plan §7.3 task 7) — this ViewModel
 * aggregates several repository interfaces because the Dashboard is inherently a
 * cross-domain summary screen, not because it re-introduces a shared god object: it
 * combines read-only streams from each domain's own repository, owns no domain state
 * itself, and other features do not depend on it (plan §13 Decision 8).
 */
@HiltViewModel
class CreatorDashboardViewModel @Inject constructor(
    private val creatorProfileRepository: CreatorProfileRepository,
    private val audienceRepository: AudienceRepository,
    private val creatorAIRepository: CreatorAIRepository,
    private val revenueRepository: RevenueRepository,
    private val channelRepository: ChannelRepository,
    private val appSessionState: AppSessionState
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<CreatorDashboardUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<CreatorDashboardUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                creatorProfileRepository.creatorProfile,
                audienceRepository.analytics,
                creatorAIRepository.recommendations,
                audienceRepository.topContent,
                revenueRepository.transactions,
                creatorProfileRepository.goals,
                channelRepository.channels,
                appSessionState.selectedChannelId
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                UiState.Success(
                    CreatorDashboardUiModel(
                        profile = values[0] as CreatorProfileInfo,
                        analytics = values[1] as AnalyticsOverview,
                        recommendations = values[2] as List<CreatorRecommendationCard>,
                        topContent = values[3] as List<TopContentPerformance>,
                        transactions = values[4] as List<TransactionItem>,
                        goals = values[5] as List<CreatorGoal>,
                        channels = values[6] as List<ChannelSummary>,
                        selectedChannelId = values[7] as String?
                    )
                )
            }.collect { _state.value = it }
        }
    }

    fun dismissRecommendation(id: String) = creatorAIRepository.dismissRecommendation(id)
}
