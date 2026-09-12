package com.somuleco.creator.feature.creator.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.core.ui.uiStateOf
import com.somuleco.creator.data.model.AccessType
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.model.ContentType
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Content Management screen (plan §7.3 task 7, Creator core loop). Channel
 * scoping reads [AppSessionState.selectedChannelId] and passes it to
 * [ContentRepository.observeContent] as an explicit parameter — the repository itself
 * never reads shell state (v0.1-client-data-access.md §7 / plan §13 Decision 9).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ContentViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val appSessionState: AppSessionState
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ContentPost>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ContentPost>>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appSessionState.selectedChannelId
                .flatMapLatest { channelId -> contentRepository.observeContent(channelId) }
                .collect { list -> _state.value = uiStateOf(list) }
        }
    }

    fun toggleLike(contentId: String) = contentRepository.toggleLike(contentId)

    fun publish(
        title: String,
        body: String,
        type: ContentType,
        access: AccessType,
        tags: List<String>,
        channelId: String? = appSessionState.selectedChannelId.value
    ): ContentPost = contentRepository.publishContent(title, body, type, access, channelId, tags)
}

/**
 * Thin channel-list supplier for the content editor's channel picker — kept separate
 * from [ContentViewModel] so the editor doesn't have to depend on the whole Channels
 * feature's ViewModel.
 */
@HiltViewModel
class ContentEditorChannelListViewModel @Inject constructor(
    channelRepository: com.somuleco.creator.data.repository.interfaces.ChannelRepository
) : ViewModel() {
    val channels: StateFlow<List<com.somuleco.creator.data.model.ChannelSummary>> = channelRepository.channels
}
