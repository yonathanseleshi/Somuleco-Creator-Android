package com.somuleco.creator.core.ui

/**
 * The one modeled four-state UI type for every migrated ViewModel
 * (`GRD/Contracts/v0.1-client-data-access.md` §3 / plan §13 Decision 2).
 *
 * [Empty] means "loaded successfully, nothing to show" — a distinct case from
 * [Success], so a view can never accidentally render a blank success as if data
 * were present. [Error] carries a user-safe message only (no stack traces or
 * provider/library internals).
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}

/** Maps [UiState.Success]'s payload, leaving Loading/Empty/Error untouched. */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Empty -> UiState.Empty
    is UiState.Error -> UiState.Error(message)
    is UiState.Success -> UiState.Success(transform(data))
}

/** Builds a [UiState] from a nullable/collection result: null or empty -> [UiState.Empty]. */
fun <T> uiStateOf(data: T?): UiState<T> = when {
    data == null -> UiState.Empty
    data is Collection<*> && data.isEmpty() -> UiState.Empty
    else -> UiState.Success(data)
}
