package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ContentPost
import com.somuleco.creator.data.model.CreatorProfileInfo
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ContentRepository
import com.somuleco.creator.data.repository.interfaces.CreatorProfileRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Not in the named Wave 05 migration scope (Decision 5) but still boundary-compliant. */
@HiltViewModel
class CreatorProfileViewModel @Inject constructor(
    creatorProfileRepository: CreatorProfileRepository,
    channelRepository: ChannelRepository,
    private val contentRepository: ContentRepository,
    productRepository: ProductRepository
) : ViewModel() {
    val profile: StateFlow<CreatorProfileInfo> = creatorProfileRepository.creatorProfile
    val channels: StateFlow<List<ChannelSummary>> = channelRepository.channels
    val contentItems: StateFlow<List<ContentPost>> = contentRepository.observeContent()
    val products: StateFlow<List<ProductListing>> = productRepository.observeProducts()

    fun toggleLike(contentId: String) = contentRepository.toggleLike(contentId)
}
