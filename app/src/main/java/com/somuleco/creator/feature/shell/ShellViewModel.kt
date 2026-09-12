package com.somuleco.creator.feature.shell

import androidx.lifecycle.ViewModel
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.model.NotificationItem
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import com.somuleco.creator.data.repository.interfaces.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Aggregates cross-app shell chrome data for [CreatorShell] — mode + Channel selection
 * come from [AppSessionState] (Wave 05's new cross-app state holder), everything else
 * from repository interfaces via DI. This is a shell-chrome aggregator, not a domain
 * store: it holds no mutation logic of its own beyond delegating to the session/
 * repositories it wraps (plan §13 Decision 8).
 */
@HiltViewModel
class ShellViewModel @Inject constructor(
    private val appSessionState: AppSessionState,
    channelRepository: ChannelRepository,
    notificationRepository: NotificationRepository,
    authRepository: AuthRepository,
    creatorProfileRepository: CreatorProfileRepository
) : ViewModel() {

    val isCreatorMode: StateFlow<Boolean> = appSessionState.isCreatorMode
    val selectedChannelId: StateFlow<String?> = appSessionState.selectedChannelId
    val channels: StateFlow<List<ChannelSummary>> = channelRepository.channels
    val notifications: StateFlow<List<NotificationItem>> = notificationRepository.notifications
    val currentUser: StateFlow<UserIdentity> = authRepository.currentUser
    val creatorProfile: StateFlow<CreatorProfileInfo> = creatorProfileRepository.creatorProfile

    fun toggleCreatorMode() = appSessionState.toggleCreatorMode()
    fun selectChannel(id: String?) = appSessionState.selectChannel(id)
}
