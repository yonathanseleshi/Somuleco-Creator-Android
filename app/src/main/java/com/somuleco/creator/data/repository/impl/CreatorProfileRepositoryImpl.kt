package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.CreatorAccountInfo
import com.somuleco.creator.data.model.CreatorGoal
import com.somuleco.creator.data.model.CreatorIntegration
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatorProfileRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : CreatorProfileRepository {
    override val creatorProfile: StateFlow<CreatorProfileInfo> = dataSource.creatorProfile
    override val creatorAccount: StateFlow<CreatorAccountInfo> = dataSource.creatorAccount
    override val goals: StateFlow<List<CreatorGoal>> = dataSource.goals
    override val integrations: StateFlow<List<CreatorIntegration>> = dataSource.integrations

    override fun updateProfile(profile: CreatorProfileInfo) = dataSource.updateProfile(profile)
    override fun toggleIntegration(id: String) = dataSource.toggleIntegration(id)
}
