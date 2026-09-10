package com.somuleco.creator.feature.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.somuleco.creator.core.design.EmptyStateCard
import com.somuleco.creator.ui.theme.BackgroundLight

/**
 * Real, distinct "access denied" destination per v0.1-navigation-semantics.md §5/§7:
 * an authenticated user who is not Creator-activated landed on a Creator-only route.
 * Renders the shared empty-state schema (title/description/iconEmoji/actionLabel).
 */
@Composable
fun AccessDeniedScreen(
    onGoToCreatorActivation: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
            .testTag("screen_access_denied"),
        contentAlignment = Alignment.Center
    ) {
        EmptyStateCard(
            title = "Creator Access Required",
            description = "This area is only available to activated Creator Accounts. Activate your Creator Account to unlock the Creator workspace.",
            actionLabel = "Become a Creator",
            onActionClick = onGoToCreatorActivation,
            iconEmoji = "🔒"
        )
    }
}
