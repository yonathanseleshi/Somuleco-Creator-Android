package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.ContentComment
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * `ContentDetailScreen` is a shared consumer screen not in the named Wave 05 migration
 * scope (plan §13 Decision 5 lists only Home + Explore for the consumer surface), but it
 * still must not reach past the repository boundary — it gets a lightweight
 * interface-typed ViewModel rather than the full `UiState` treatment reserved for the
 * ten migrated features.
 */
@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {
    val contentItems: StateFlow<List<ContentPost>> = contentRepository.observeContent()
    val comments: StateFlow<List<ContentComment>> = contentRepository.comments

    fun toggleLike(contentId: String) = contentRepository.toggleLike(contentId)
    fun addComment(text: String) = contentRepository.addComment(text)
}
