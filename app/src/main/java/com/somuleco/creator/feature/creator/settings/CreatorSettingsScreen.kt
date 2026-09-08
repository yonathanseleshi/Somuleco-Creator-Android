package com.somuleco.creator.feature.creator.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.CreatorSettingsData
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun CreatorSettingsScreen(
    onNavigate: (Screen) -> Unit
) {
    val profile by CreatorRepository.creatorProfile.collectAsState()
    val currentSettings by CreatorRepository.settings.collectAsState()

    var autoDprProtect by remember(currentSettings) { mutableStateOf(currentSettings.autoDprProtect) }
    var emailNotifications by remember(currentSettings) { mutableStateOf(currentSettings.weeklyDigestEmail) }
    var subscriberAlerts by remember(currentSettings) { mutableStateOf(currentSettings.instantSubscriberAlerts) }
    var publicProfileDiscoverable by remember(currentSettings) { mutableStateOf(currentSettings.publicDiscoverable) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_creator_settings"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Creator Workspace Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Configure publishing defaults, rights, and notifications", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        saveMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SomulecoGreenSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SomulecoGreenText)
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText, modifier = Modifier.weight(1f))
                    TextButton(onClick = { saveMessage = null }) { Text("Dismiss", color = SomulecoGreenText) }
                }
            }
        }

        // Creator Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Public Creator Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text("Display Name: ${profile.displayName}", style = MaterialTheme.typography.bodyMedium)
                Text("Creator Handle: ${profile.handle}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("Category: ${profile.category}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                Button(
                    onClick = { onNavigate(Screen.CreatorProfileDetail) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SomulecoBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Public Profile As Follower", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Rights & DPR Defaults
        SettingsToggleCard(
            title = "Digital Product Rights Defaults",
            items = listOf(
                ToggleItem("Auto-Register New Products into DPR", "Automatically generate cryptographic rights fingerprints for uploaded digital goods.", autoDprProtect) { autoDprProtect = it },
                ToggleItem("Public Marketplace Discoverability", "Allow products and channels to be indexed in Somuleco search.", publicProfileDiscoverable) { publicProfileDiscoverable = it }
            )
        )

        // Notification Preferences
        SettingsToggleCard(
            title = "Notification Preferences",
            items = listOf(
                ToggleItem("Instant Paid Subscriber Alerts", "Receive immediate push notifications when someone joins a paid tier.", subscriberAlerts) { subscriberAlerts = it },
                ToggleItem("Weekly Financial Digest", "Receive weekly revenue, sales, and analytics breakdown by email.", emailNotifications) { emailNotifications = it }
            )
        )

        // Payout Method Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Payout Bank Account", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text("Stripe Connected Account: Chase Bank (•••• 4921)", style = MaterialTheme.typography.bodyMedium)
                Text("Automatic payouts sent on the 1st and 15th of each month.", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText)
            }
        }

        Button(
            onClick = {
                CreatorRepository.updateSettings(
                    currentSettings.copy(
                        autoDprProtect = autoDprProtect,
                        weeklyDigestEmail = emailNotifications,
                        instantSubscriberAlerts = subscriberAlerts,
                        publicDiscoverable = publicProfileDiscoverable
                    )
                )
                saveMessage = "Workspace settings successfully updated."
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
        ) {
            Text("Save Settings", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class ToggleItem(
    val title: String,
    val subtitle: String,
    val isChecked: Boolean,
    val onToggle: (Boolean) -> Unit
)

@Composable
private fun SettingsToggleCard(
    title: String,
    items: List<ToggleItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            items.forEachIndexed { index, item ->
                if (index > 0) HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Switch(checked = item.isChecked, onCheckedChange = item.onToggle)
                }
            }
        }
    }
}
