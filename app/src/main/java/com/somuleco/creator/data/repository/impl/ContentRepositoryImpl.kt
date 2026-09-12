package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.AccessType
import com.somuleco.creator.data.model.ContentComment
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.model.ContentType
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : ContentRepository {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // channelId: String? = null -> "All Channels" (v0.1-client-data-access.md §7). This
    // repository never reads shell/Channel-selection state itself — the caller (ViewModel)
    // supplies channelId, sourced from AppSessionState.selectedChannelId.
    override fun observeContent(channelId: String?): StateFlow<List<ContentPost>> {
        fun filter(list: List<ContentPost>) = if (channelId == null) list else list.filter { it.channelId == channelId }
        return dataSource.contentItems
            .map(::filter)
            .stateIn(repoScope, SharingStarted.Eagerly, filter(dataSource.contentItems.value))
    }

    override fun getContent(id: String): ContentPost? = dataSource.getContent(id)

    override fun publishContent(
        title: String,
        body: String,
        type: ContentType,
        access: AccessType,
        channelId: String?,
        tags: List<String>
    ): ContentPost {
        val targetChannelId = channelId ?: dataSource.channels.value.firstOrNull()?.id ?: "ch_1"
        val summary = if (body.length > 120) body.take(120) + "..." else body
        return dataSource.createContentItem(
            title = title,
            body = body,
            summary = summary,
            channelId = targetChannelId,
            contentType = type,
            accessType = access
        )
    }

    override fun toggleLike(contentId: String) = dataSource.toggleLike(contentId)
    override fun toggleBookmark(contentId: String) = dataSource.toggleSave(contentId)
    override fun addComment(contentId: String, text: String) = dataSource.addContentComment(contentId, text)

    override val comments: kotlinx.coroutines.flow.StateFlow<List<ContentComment>> = dataSource.comments
    override fun addComment(text: String) = dataSource.addComment(text)
}
