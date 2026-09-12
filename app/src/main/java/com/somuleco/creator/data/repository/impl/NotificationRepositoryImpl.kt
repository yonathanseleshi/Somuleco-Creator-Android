package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.NotificationItem
import com.somuleco.creator.data.repository.interfaces.NotificationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : NotificationRepository {
    override val notifications: StateFlow<List<NotificationItem>> = dataSource.notifications
    override fun markAsRead(id: String) = dataSource.markAsRead(id)
    override fun markAllAsRead() = dataSource.markAllAsRead()
}
