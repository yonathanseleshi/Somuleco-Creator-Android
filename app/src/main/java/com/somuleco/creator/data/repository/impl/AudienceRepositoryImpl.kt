package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.AnalyticsOverview
import com.somuleco.creator.data.model.TopContentPerformance
import com.somuleco.creator.data.repository.interfaces.AudienceMetrics
import com.somuleco.creator.data.repository.interfaces.AudienceRepository
import com.somuleco.creator.data.repository.interfaces.AudienceSegment
import com.somuleco.creator.data.repository.interfaces.TrafficSource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudienceRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : AudienceRepository {
    override val audienceMetrics: StateFlow<AudienceMetrics> = dataSource.audienceMetrics
    override val topReferrers: StateFlow<List<TrafficSource>> = dataSource.topReferrers
    override val segments: StateFlow<List<AudienceSegment>> = dataSource.segments
    override val analytics: StateFlow<AnalyticsOverview> = dataSource.analytics
    override val topContent: StateFlow<List<TopContentPerformance>> = dataSource.topContent
}
