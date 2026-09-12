package com.somuleco.creator.feature.creator.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChannelDetailUiModel(
    val channel: ChannelSummary,
    val posts: List<ContentPost>
)

@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val contentRepository: ContentRepository,
    private val appSessionState: AppSessionState
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<ChannelDetailUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<ChannelDetailUiModel>> = _state.asStateFlow()

    fun load(channelId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                channelRepository.refresh()
                combine(
                    channelRepository.channels,
                    contentRepository.observeContent(channelId)
                ) { channels, posts ->
                    val channel = channels.find { it.id == channelId }
                    if (channel == null) UiState.Empty else UiState.Success(ChannelDetailUiModel(channel, posts))
                }.collect { _state.value = it }
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unable to load this channel.")
            }
        }
    }

    fun selectAsActiveChannel(channelId: String) = appSessionState.selectChannel(channelId)
    fun toggleFollow(channelId: String) = channelRepository.toggleFollowChannel(channelId)
}
