package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsumerHomeUiModel(
    val feed: List<ContentPost>,
    val channels: List<ChannelSummary>,
    val profile: CreatorProfileInfo
)

/** Backs the consumer Home screen (plan §13 Decision 5's consumer scope). */
@HiltViewModel
class ConsumerHomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val channelRepository: ChannelRepository,
    private val creatorProfileRepository: CreatorProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<ConsumerHomeUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<ConsumerHomeUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                contentRepository.observeContent(),
                channelRepository.channels,
                creatorProfileRepository.creatorProfile
            ) { feed, channels, profile ->
                // Home's feed is never modeled as Empty even with zero posts today — the
                // banner/stories row above it is always meaningful content, matching
                // pre-Wave-05 behavior of never hiding the whole screen.
                UiState.Success(ConsumerHomeUiModel(feed, channels, profile))
            }.collect { _state.value = it }
        }
    }

    fun toggleLike(contentId: String) = contentRepository.toggleLike(contentId)
}
