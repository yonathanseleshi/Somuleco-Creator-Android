package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun SubscriptionsScreen(
    onNavigate: (Screen) -> Unit
) {
    val subscriptions by CreatorRepository.userSubscriptions.collectAsState()
    var cancelNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_subscriptions"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "My Subscriptions & Memberships",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Direct recurring creator memberships and unlockable perks",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        cancelNotice?.let { msg ->
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
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SomulecoPurpleDark)
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = SomulecoPurpleDark,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { cancelNotice = null }) {
                        Text("Dismiss", color = SomulecoPurpleDark)
                    }
                }
            }
        }

        if (subscriptions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("⭐", fontSize = 42.sp)
                    Text("No Active Subscriptions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Join creator circles to unlock subscriber masterclasses, raw session files, and direct community access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { onNavigate(Screen.Explore) },
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                    ) {
                        Text("Discover Creators")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(subscriptions) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(CreatorGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(sub.avatarEmoji, fontSize = 24.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sub.creatorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(sub.creatorHandle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SomulecoPurpleLight,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            sub.tierName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SomulecoPurpleDark,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$${String.format("%.2f", sub.monthlyPrice.amount / 100.0)}/mo",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Renews ${sub.nextBillingDate}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }

                            Text(
                                text = sub.benefitsSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            HorizontalDivider(color = BorderSubtle)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Text("Visa ending in •••• 4921", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                OutlinedButton(
                                    onClick = {
                                        CreatorRepository.cancelUserSubscription(sub.id)
                                        cancelNotice = "Subscription to ${sub.creatorName} has been canceled."
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Cancel", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
