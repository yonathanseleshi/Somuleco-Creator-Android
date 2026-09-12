package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.model.NotificationItem
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.model.SubscriptionPlanInfo
import com.somuleco.creator.data.model.UserSubscription
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import com.somuleco.creator.data.repository.interfaces.NotificationRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import com.somuleco.creator.data.repository.interfaces.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * This file groups the small, interface-typed ViewModels for the consumer screens that
 * are **not** in the named Wave 05 migration scope (plan §13 Decision 5: only Home and
 * Explore are migrated on the consumer surface). These screens still must not reach the
 * deleted `CreatorRepository` object, so each gets the minimal DI-supplied surface it
 * needs — interface-typed, but without the full `UiState` treatment reserved for the ten
 * migrated features.
 */

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {
    val channels: StateFlow<List<ChannelSummary>> = channelRepository.channels
    fun toggleFollowChannel(id: String) = channelRepository.toggleFollowChannel(id)
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    productRepository: ProductRepository,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()
    val plan: StateFlow<SubscriptionPlanInfo> = subscriptionRepository.subscriptionPlan
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    val notifications: StateFlow<List<NotificationItem>> = notificationRepository.notifications
    fun markAsRead(id: String) = notificationRepository.markAsRead(id)
    fun markAllAsRead() = notificationRepository.markAllAsRead()
}

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()
}

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {
    val contentItems: StateFlow<List<ContentPost>> = contentRepository.observeContent()
    fun toggleBookmark(contentId: String) = contentRepository.toggleBookmark(contentId)
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    contentRepository: ContentRepository,
    productRepository: ProductRepository,
    channelRepository: ChannelRepository
) : ViewModel() {
    val contentItems: StateFlow<List<ContentPost>> = contentRepository.observeContent()
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()
    val channels: StateFlow<List<ChannelSummary>> = channelRepository.channels
}

@HiltViewModel
class SubscriptionPlansViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val plan: StateFlow<SubscriptionPlanInfo> = subscriptionRepository.subscriptionPlan
}

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val subscriptions: StateFlow<List<UserSubscription>> = subscriptionRepository.userSubscriptions
    fun cancelUserSubscription(id: String) = subscriptionRepository.cancelUserSubscription(id)
}
