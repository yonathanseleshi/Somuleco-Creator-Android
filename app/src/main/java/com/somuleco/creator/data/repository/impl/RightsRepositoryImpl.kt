package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.ProductRightsConfig
import com.somuleco.creator.data.model.RightsRecord
import com.somuleco.creator.data.repository.interfaces.RightsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RightsRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : RightsRepository {
    override val rightsRecords: StateFlow<List<RightsRecord>> = dataSource.rightsRecords
    override fun getProductRights(productId: String): ProductRightsConfig? = dataSource.getProductRights(productId)
    override fun updateProductRights(config: ProductRightsConfig) = dataSource.updateProductRights(config)
}
