package com.somuleco.creator.feature.creator.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.core.ui.uiStateOf
import com.somuleco.creator.data.model.AiChatMessage
import com.somuleco.creator.data.repository.interfaces.CreatorAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Backs the Creator AI screen (plan §7.3 task 7, Creator core loop). */
@HiltViewModel
class CreatorAIViewModel @Inject constructor(
    private val creatorAIRepository: CreatorAIRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<AiChatMessage>>>(UiState.Loading)
    val state: StateFlow<UiState<List<AiChatMessage>>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            creatorAIRepository.chatMessages.collect { list -> _state.value = uiStateOf(list) }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch { creatorAIRepository.sendMessage(text) }
    }
}
