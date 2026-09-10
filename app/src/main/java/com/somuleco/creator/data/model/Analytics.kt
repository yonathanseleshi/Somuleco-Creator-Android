package com.somuleco.creator.data.model

import com.somuleco.creator.core.model.Money

data class AnalyticsOverview(
    val viewsTotal: String = "184.2K",
    val followersTotal: String = "12,480",
    val subscribersTotal: String = "840",
    val grossRevenue: String = "$18,450",
    val viewsGrowth: String = "+18.4%",
    val followersGrowth: String = "+9.2%",
    val subscribersGrowth: String = "+14.6%",
    val revenueGrowth: String = "+22.5%"
)

data class TransactionItem(
    val id: String,
    val date: String,
    val customerName: String,
    val itemTitle: String,
    val grossAmount: Money,
    val feeAmount: Money,
    val netAmount: Money,
    val status: String = "COMPLETED",
    val source: String = "Digital Product" // or "Subscription"
) {
    val type: String get() = source
    val platformFee: Money get() = feeAmount
}

data class TopContentPerformance(
    val id: String,
    val title: String,
    val channelName: String,
    val views: String,
    val conversionRate: String,
    val newFollowers: Int,
    val format: String
)
