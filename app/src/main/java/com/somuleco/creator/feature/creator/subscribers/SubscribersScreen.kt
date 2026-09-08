package com.somuleco.creator.feature.creator.subscribers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.CreatorSubscriptionPlanItem
import com.somuleco.creator.data.model.MembershipTier
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribersScreen(
    onNavigate: (Screen) -> Unit
) {
    val subscribers by CreatorRepository.subscribers.collectAsState()
    val plan by CreatorRepository.subscriptionPlan.collectAsState()
    var showCreateTierDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var newTierName by remember { mutableStateOf("") }
    var newTierPrice by remember { mutableStateOf("19.00") }
    var newTierBenefits by remember { mutableStateOf("Private live Q&A, Discord access, Weekly tutorials") }
    var tierCreatedNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_subscribers"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Subscribers & Memberships", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("920 active paid supporters generating $8,190 MRR", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Button(
                onClick = { showCreateTierDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Tier", fontWeight = FontWeight.Bold)
            }
        }

        tierCreatedNotice?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SomulecoPurpleLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = SomulecoPurpleDark)
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = SomulecoPurpleDark, modifier = Modifier.weight(1f))
                    TextButton(onClick = { tierCreatedNotice = null }) {
                        Text("Dismiss", color = SomulecoPurpleDark)
                    }
                }
            }
        }

        // Tiers summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Membership Tiers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = { onNavigate(Screen.SubscriptionPlansDetail) }) {
                        Text("View Public Page", style = MaterialTheme.typography.labelSmall, color = SomulecoPurple)
                    }
                }

                plan.tiers.forEach { tier ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tier.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text("${tier.subscriberCount} subscribers", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = SomulecoPurpleLight) {
                            Text(
                                text = if (tier.monthlyPrice == 0.0) "Free" else "$${tier.monthlyPrice.toInt()}/mo",
                                fontWeight = FontWeight.Bold,
                                color = SomulecoPurpleDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        Text("Subscriber Roster (${subscribers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(subscribers) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SomulecoPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sub.avatarEmoji, fontSize = 20.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${sub.handle} • ${sub.joinedDate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(shape = RoundedCornerShape(6.dp), color = SomulecoPurpleLight) {
                                Text(
                                    sub.tierName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SomulecoPurpleDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text("$${sub.monthlyAmount.toInt()}/mo", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
                        }
                    }
                }
            }
        }
    }

    if (showCreateTierDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTierDialog = false },
            title = { Text("Create New Membership Tier", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTierName,
                        onValueChange = { newTierName = it },
                        label = { Text("Tier Name") },
                        placeholder = { Text("e.g. Masterclass VIP") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTierPrice,
                        onValueChange = { newTierPrice = it },
                        label = { Text("Monthly Price ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTierBenefits,
                        onValueChange = { newTierBenefits = it },
                        label = { Text("Benefits (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = newTierPrice.toDoubleOrNull() ?: 19.0
                        val tier = MembershipTier(
                            id = "tier_${System.currentTimeMillis()}",
                            name = newTierName.ifBlank { "Custom Creator Tier" },
                            monthlyPrice = price,
                            benefits = newTierBenefits.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            subscriberCount = 0
                        )
                        CreatorRepository.createSubscriptionPlan(
                            CreatorSubscriptionPlanItem(
                                id = tier.id,
                                name = tier.name,
                                monthlyPrice = tier.monthlyPrice,
                                benefits = tier.benefits
                            )
                        )
                        showCreateTierDialog = false
                        tierCreatedNotice = "Membership tier '${tier.name}' created successfully!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                ) {
                    Text("Create Tier")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTierDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
