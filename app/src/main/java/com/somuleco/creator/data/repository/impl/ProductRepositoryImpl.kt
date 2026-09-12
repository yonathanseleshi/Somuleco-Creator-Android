package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.data.datasource.MockCreatorDataSource
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.model.ProductType
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val dataSource: MockCreatorDataSource
) : ProductRepository {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun observeProducts(channelId: String?): StateFlow<List<ProductListing>> {
        fun filter(list: List<ProductListing>) = if (channelId == null) list else list.filter { it.channelId == channelId }
        return dataSource.products
            .map(::filter)
            .stateIn(repoScope, SharingStarted.Eagerly, filter(dataSource.products.value))
    }

    override fun getProduct(id: String): ProductListing? = dataSource.getProduct(id)

    override fun createProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        channelId: String?,
        productType: ProductType,
        deliverables: List<String>,
        isDprProtected: Boolean
    ): ProductListing {
        val targetChannelId = channelId ?: dataSource.channels.value.firstOrNull()?.id ?: "ch_1"
        val shortDesc = if (description.length > 100) description.take(100) + "..." else description
        return dataSource.createProduct(
            title = title,
            shortDescription = shortDesc,
            fullDescription = description,
            price = price,
            channelId = targetChannelId,
            productType = productType,
            fileFormat = deliverables.joinToString(", ").ifBlank { "Digital Download" },
            licenseName = if (isDprProtected) "Somuleco Verified DPR License" else "Standard License",
            allowDownload = true,
            allowCommercial = true
        )
    }

    override fun purchaseProduct(productId: String) = dataSource.purchaseProduct(productId)

    override fun createProductDetailed(
        title: String,
        shortDescription: String,
        fullDescription: String,
        price: Double,
        channelId: String,
        productType: ProductType,
        fileFormat: String,
        licenseName: String,
        allowDownload: Boolean,
        allowCommercial: Boolean
    ): ProductListing = dataSource.createProduct(
        title = title,
        shortDescription = shortDescription,
        fullDescription = fullDescription,
        price = price,
        channelId = channelId,
        productType = productType,
        fileFormat = fileFormat,
        licenseName = licenseName,
        allowDownload = allowDownload,
        allowCommercial = allowCommercial
    )
}
