package com.somuleco.creator.feature.creator.subscribers

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.CreatorSubscriptionPlanItem
import com.somuleco.creator.data.model.SubscriberMember
import com.somuleco.creator.data.model.SubscriptionPlanInfo
import com.somuleco.creator.data.repository.interfaces.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class SubscribersViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val subscribers: StateFlow<List<SubscriberMember>> = subscriptionRepository.subscriberMembers
    val plan: StateFlow<SubscriptionPlanInfo> = subscriptionRepository.subscriptionPlan

    fun createSubscriptionPlan(plan: CreatorSubscriptionPlanItem) = subscriptionRepository.createSubscriptionPlan(plan)
}
