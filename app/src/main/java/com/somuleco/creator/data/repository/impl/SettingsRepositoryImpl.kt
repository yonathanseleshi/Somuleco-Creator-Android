package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.CreatorSettingsData
import com.somuleco.creator.data.repository.interfaces.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : SettingsRepository {
    override val settings: StateFlow<CreatorSettingsData> = dataSource.settings
    override fun updateSettings(settings: CreatorSettingsData) = dataSource.updateSettings(settings)
}
