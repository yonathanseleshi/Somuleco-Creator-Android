package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.AiChatMessage
import com.somuleco.creator.data.model.CreatorRecommendationCard
import com.somuleco.creator.data.repository.interfaces.CreatorAIRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatorAIRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : CreatorAIRepository {
    override val chatMessages: StateFlow<List<AiChatMessage>> = dataSource.aiMessages
    override val suggestedPrompts: List<String> = dataSource.suggestedPrompts
    override val recommendations: StateFlow<List<CreatorRecommendationCard>> = dataSource.recommendations

    override suspend fun sendMessage(userText: String): String {
        // Genuinely suspends (v0.1-client-data-access.md §4) rather than resolving
        // synchronously, so a real HTTP-backed implementation is a drop-in swap.
        delay(150)
        return dataSource.sendAIMessage(userText).content
    }

    override fun dismissRecommendation(id: String) = dataSource.dismissRecommendation(id)
}
