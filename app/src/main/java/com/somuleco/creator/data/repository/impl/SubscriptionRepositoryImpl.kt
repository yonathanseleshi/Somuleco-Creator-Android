package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.CreatorSubscriptionPlanItem
import com.somuleco.creator.data.model.SubscriberMember
import com.somuleco.creator.data.model.SubscriptionPlanInfo
import com.somuleco.creator.data.model.UserSubscription
import com.somuleco.creator.data.repository.interfaces.SubscriptionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : SubscriptionRepository {
    override val subscriptionPlan: StateFlow<SubscriptionPlanInfo> = dataSource.subscriptionPlan
    override val subscriberMembers: StateFlow<List<SubscriberMember>> = dataSource.subscribers
    override val creatorSubscriptionPlans: StateFlow<List<CreatorSubscriptionPlanItem>> = dataSource.creatorSubscriptionPlans
    override val userSubscriptions: StateFlow<List<UserSubscription>> = dataSource.userSubscriptions

    override fun createSubscriptionPlan(plan: CreatorSubscriptionPlanItem) = dataSource.createSubscriptionPlan(plan)
    override fun updateSubscriptionPlan(plan: CreatorSubscriptionPlanItem) = dataSource.updateSubscriptionPlan(plan)
    override fun cancelUserSubscription(id: String) = dataSource.cancelUserSubscription(id)
}
