package com.somuleco.creator.feature.creator.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.core.ui.uiStateOf
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Wave 05 vertical slice (plan §7.3 task 1 / §14): the first ViewModel validated end to
 * end — DI-supplied [ChannelRepository], `viewModelScope`-driven load, [UiState] rendering
 * — before the pattern was fanned out to the rest of the Creator core loop.
 */
@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ChannelSummary>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ChannelSummary>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                channelRepository.refresh()
                collectChannels()
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unable to load channels.")
            }
        }
    }

    private suspend fun collectChannels() {
        channelRepository.channels.collect { list ->
            _state.value = uiStateOf(list)
        }
    }

    fun createChannel(name: String, description: String, category: String, handle: String) {
        channelRepository.createChannel(name, description, category, handle)
    }
}
