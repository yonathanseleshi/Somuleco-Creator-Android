package com.somuleco.creator.feature.consumer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.ChannelSummary
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiModel(
    val products: List<ProductListing>,
    val channels: List<ChannelSummary>
)

/** Backs the consumer Explore screen (plan §13 Decision 5's consumer scope). */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    channelRepository: ChannelRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<ExploreUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<ExploreUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(productRepository.observeProducts(), channelRepository.channels) { products, channels ->
                UiState.Success(ExploreUiModel(products, channels))
            }.collect { _state.value = it }
        }
    }
}
