package com.somuleco.creator.feature.creator.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.core.ui.uiStateOf
import com.somuleco.creator.data.model.ProductListing
import com.somuleco.creator.data.repository.interfaces.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Backs the Products/Store screen (plan §7.3 task 7, Creator core loop). */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    appSessionState: AppSessionState
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ProductListing>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ProductListing>>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appSessionState.selectedChannelId
                .flatMapLatest { channelId -> productRepository.observeProducts(channelId) }
                .collect { list -> _state.value = uiStateOf(list) }
        }
    }
}
