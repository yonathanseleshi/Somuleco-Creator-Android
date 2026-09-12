package com.somuleco.creator.feature.creator.audience

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.AnalyticsOverview
import com.somuleco.creator.data.model.SubscriberMember
import com.somuleco.creator.data.repository.interfaces.AudienceRepository
import com.somuleco.creator.data.repository.interfaces.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudienceUiModel(
    val analytics: AnalyticsOverview,
    val subscribers: List<SubscriberMember>
)

/** Backs the Audience screen (plan §7.3 task 7, Creator core loop). */
@HiltViewModel
class AudienceViewModel @Inject constructor(
    audienceRepository: AudienceRepository,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<AudienceUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<AudienceUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(audienceRepository.analytics, subscriptionRepository.subscriberMembers) { analytics, subscribers ->
                UiState.Success(AudienceUiModel(analytics, subscribers))
            }.collect { _state.value = it }
        }
    }
}
