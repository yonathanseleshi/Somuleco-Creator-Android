package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.MarketplaceListing
import com.somuleco.creator.data.repository.interfaces.MarketplaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketplaceRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : MarketplaceRepository {
    override fun getMarketplaceListing(productId: String): MarketplaceListing = dataSource.getMarketplaceListing(productId)
    override fun updateMarketplaceListing(listing: MarketplaceListing) = dataSource.updateMarketplaceListing(listing)
    override fun togglePublishMarketplace(productId: String): MarketplaceListing = dataSource.togglePublishMarketplace(productId)
}
