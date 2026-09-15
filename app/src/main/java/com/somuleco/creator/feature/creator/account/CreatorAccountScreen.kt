package com.somuleco.creator.feature.creator.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.somuleco.creator.core.design.UiStateContent
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.core.network.CreatorAccountDto
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.ui.theme.*

/**
 * Foundation Wave 09 (plan §7.4 task 4; `v0.1-creator-authorization.md` §2-3, §7). Did not
 * exist before this wave — Android had no real Creator Account activation UI at all (the
 * pre-existing [com.somuleco.creator.feature.creator.onboarding.CreatorActivationScreen] /
 * [com.somuleco.creator.feature.creator.onboarding.CreatorOnboardingScreen] are a local-only
 * mock wizard, kept unchanged and untouched by this wave).
 *
 * A single minimal screen combining the account list ("switcher" — tap a row to select it,
 * per §7's "a list + select, not a full management UI"), the activation form, and the
 * complete-onboarding step for a still-`draft` account — deliberately not split into separate
 * `feature/creator/activate` and `feature/creator/switch` packages, since a combined
 * list+switch+activate screen already satisfies both without extra navigation-graph plumbing
 * for what plan §8 itself calls a non-binding, indicative file layout.
 *
 * The activation form collects `creatorType` only — verified directly against the real NestJS
 * `CreateCreatorAccountDto` (`src/creator-account/dto/create-creator-account.dto.ts`), which
 * declares only `creatorType: string` (`@IsNotEmpty`); the API's `forbidNonWhitelisted: true`
 * validation pipe rejects any other property. No monetization/Stripe/tier fields, matching
 * Web's corrected implementation.
 */
@Composable
fun CreatorAccountScreen(
    onNavigate: (Screen) -> Unit,
    onManageComplete: () -> Unit,
    viewModel: CreatorAccountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var showActivateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_creator_account_manage"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Creator Accounts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Activate a Creator Account, finish onboarding, or switch which one is active",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Button(
                onClick = { showActivateDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple),
                modifier = Modifier.testTag("button_new_creator_account")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Account", fontWeight = FontWeight.Bold)
            }
        }

        if (actionState is UiState.Error) {
            Surface(shape = RoundedCornerShape(12.dp), color = ErrorRed.copy(alpha = 0.1f)) {
                Text(
                    text = (actionState as UiState.Error).message,
                    color = ErrorRed,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        UiStateContent(
            state = state,
            emptyTitle = "No Creator Accounts yet",
            emptyDescription = "Activate one to unlock Channels, Content, and the rest of the Creator workspace.",
            emptyActionLabel = "New Account",
            onEmptyAction = { showActivateDialog = true }
        ) { accounts ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(accounts, key = { it.id }) { account ->
                    CreatorAccountCard(
                        account = account,
                        isSelected = account.id == selectedAccountId,
                        onSelect = { viewModel.selectAccount(account.id) },
                        onCompleteOnboarding = { viewModel.completeOnboarding(account.id) },
                        onReactivate = { viewModel.reactivate(account.id) }
                    )
                }
            }
        }
    }

    if (showActivateDialog) {
        ActivateCreatorAccountDialog(
            onDismiss = { showActivateDialog = false },
            onActivate = { creatorType ->
                viewModel.activate(creatorType)
                showActivateDialog = false
            }
        )
    }
}

@Composable
private fun CreatorAccountCard(
    account: CreatorAccountDto,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCompleteOnboarding: () -> Unit,
    onReactivate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("creator_account_card_${account.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    if (isSelected) SomulecoPurple else BorderSubtle,
                    if (isSelected) SomulecoPurple else BorderSubtle
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Creator Account #${account.id.take(8)}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = SomulecoPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                StatusBadge(status = account.status)
            }

            account.creatorType?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isSelected) {
                    TextButton(onClick = onSelect) { Text("Select") }
                }
                if (account.status == "draft") {
                    Button(
                        onClick = onCompleteOnboarding,
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoGreenDark),
                        modifier = Modifier.testTag("button_complete_onboarding_${account.id}")
                    ) {
                        Text("Complete Onboarding")
                    }
                }
                if (account.status == "suspended") {
                    Button(
                        onClick = onReactivate,
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                    ) {
                        Text("Reactivate")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status) {
        "active" -> SomulecoGreenDark
        "draft" -> SomulecoPurple
        "suspended", "restricted" -> ErrorRed
        else -> TextMuted
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Foundation Wave 09: `creatorType` is the ONLY field the real NestJS
// `CreateCreatorAccountDto` accepts (`@IsNotEmpty creatorType: string` — verified directly
// against `src/creator-account/dto/create-creator-account.dto.ts`; the ValidationPipe's
// `forbidNonWhitelisted: true` rejects anything else). A simple text field, matching Web's
// minimal form — no monetization/Stripe/tier fields.
@Composable
private fun ActivateCreatorAccountDialog(
    onDismiss: () -> Unit,
    onActivate: (creatorType: String) -> Unit
) {
    var creatorType by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Activate Creator Account", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = creatorType,
                    onValueChange = { creatorType = it },
                    label = { Text("Creator Type") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("field_creator_type"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (creatorType.isNotBlank()) {
                        onActivate(creatorType)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple),
                modifier = Modifier.testTag("button_confirm_activate")
            ) {
                Text("Activate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
