package com.somuleco.creator.core.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.ui.theme.ErrorRed
import com.somuleco.creator.ui.theme.TextSecondary

/**
 * Shared four-state presentation for a migrated screen's [UiState]
 * (`v0.1-client-data-access.md` §3 / plan §7.3 task 7). [onSuccess] renders the real
 * content; Loading/Empty/Error get one consistent, testable shape each so every migrated
 * screen answers "what does Loading/Empty/Error look like" identically.
 */
@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    emptyTitle: String = "Nothing here yet",
    emptyDescription: String = "There's no data to show right now.",
    emptyIconEmoji: String = "📭",
    emptyActionLabel: String? = null,
    onEmptyAction: () -> Unit = {},
    onSuccess: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> Box(
            modifier = modifier.fillMaxSize().testTag("state_loading"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is UiState.Success -> onSuccess(state.data)

        is UiState.Empty -> Box(
            modifier = modifier.fillMaxSize().padding(24.dp).testTag("state_empty"),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                title = emptyTitle,
                description = emptyDescription,
                actionLabel = emptyActionLabel ?: "Refresh",
                onActionClick = onEmptyAction,
                iconEmoji = emptyIconEmoji
            )
        }

        is UiState.Error -> Box(
            modifier = modifier.fillMaxSize().padding(24.dp).testTag("state_error"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed
            )
        }
    }
}
