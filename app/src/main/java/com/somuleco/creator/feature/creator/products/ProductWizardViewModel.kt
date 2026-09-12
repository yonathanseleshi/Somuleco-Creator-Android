package com.somuleco.creator.feature.creator.products

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.model.ProductType
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Backs the Product Wizard (Creator core loop, plan §7.3 task 7). All wizard form
 * fields (title, description, price text, DPR flags, etc.) stay local `remember` state
 * in the screen — this ViewModel only holds the channel list and the create action, per
 * task 5 (draft/editor state does not belong in the data layer).
 */
@HiltViewModel
class ProductWizardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    channelRepository: ChannelRepository
) : ViewModel() {
    val channels: StateFlow<List<ChannelSummary>> = channelRepository.channels

    fun createProduct(
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
    ): ProductListing = productRepository.createProductDetailed(
        title, shortDescription, fullDescription, price, channelId,
        productType, fileFormat, licenseName, allowDownload, allowCommercial
    )
}
