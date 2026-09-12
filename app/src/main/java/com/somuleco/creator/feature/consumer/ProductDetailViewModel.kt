package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.MarketplaceListing
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.model.ProductRightsConfig
import com.somuleco.creator.data.repository.interfaces.MarketplaceRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import com.somuleco.creator.data.repository.interfaces.RightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val rightsRepository: RightsRepository,
    private val marketplaceRepository: MarketplaceRepository,
    appSessionState: AppSessionState
) : ViewModel() {
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()
    val isCreatorMode: StateFlow<Boolean> = appSessionState.isCreatorMode

    fun purchaseProduct(productId: String) = productRepository.purchaseProduct(productId)
    fun updateProductRights(config: ProductRightsConfig) = rightsRepository.updateProductRights(config)
    fun updateMarketplaceListing(listing: MarketplaceListing) = marketplaceRepository.updateMarketplaceListing(listing)
}
