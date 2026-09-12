package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.repository.interfaces.RevenueRepository
import com.somuleco.creator.data.repository.interfaces.RevenueTransaction
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * All values here are computed from [MockCreatorDataSource]'s transaction ledger and
 * product catalogue rather than hardcoded (plan §7.3 task 6 — `CreatorRepository` used to
 * declare `totalRevenue: Double get() = 18450.0` etc. as fixed constants).
 */
@Singleton
class RevenueRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : RevenueRepository {
    override val transactions: StateFlow<List<RevenueTransaction>> = dataSource.transactions
    override val totalRevenue: Double get() = dataSource.totalRevenue
    override val availableBalance: Double get() = dataSource.availableBalance
    override val pendingBalance: Double get() = dataSource.pendingBalance
    override val monthlyRecurringRevenue: Double get() = dataSource.monthlyRecurringRevenue
    override val productSalesRevenue: Double get() = dataSource.productSalesRevenue

    override fun getTransaction(id: String): RevenueTransaction? = dataSource.getTransaction(id)
}
