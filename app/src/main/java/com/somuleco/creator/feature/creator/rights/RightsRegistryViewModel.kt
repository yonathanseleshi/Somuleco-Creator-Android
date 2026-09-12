package com.somuleco.creator.feature.creator.rights

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope, but still boundary-compliant via DI. */
@HiltViewModel
class RightsRegistryViewModel @Inject constructor(
    productRepository: ProductRepository
) : ViewModel() {
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()
}
