package com.somuleco.creator.feature.creator.revenue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.data.model.TransactionItem
import com.somuleco.creator.data.repository.interfaces.RevenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RevenueUiModel(
    val transactions: List<TransactionItem>,
    val availableBalance: Double,
    val pendingBalance: Double,
    val monthlyRecurringRevenue: Double,
    val productSalesRevenue: Double
)

/**
 * Backs the Revenue screen (plan §7.3 task 7, Creator core loop). The balance figures are
 * read from [RevenueRepository] on every emission (not hardcoded display strings as the
 * screen previously had, independent of the also-hardcoded repository values — plan §7.3
 * task 6 closes both ends of that regression).
 */
@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val revenueRepository: RevenueRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<RevenueUiModel>>(UiState.Loading)
    val state: StateFlow<UiState<RevenueUiModel>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            revenueRepository.transactions
                .map { transactions ->
                    UiState.Success(
                        RevenueUiModel(
                            transactions = transactions,
                            availableBalance = revenueRepository.availableBalance,
                            pendingBalance = revenueRepository.pendingBalance,
                            monthlyRecurringRevenue = revenueRepository.monthlyRecurringRevenue,
                            productSalesRevenue = revenueRepository.productSalesRevenue
                        )
                    )
                }
                .collect { _state.value = it }
        }
    }
}
