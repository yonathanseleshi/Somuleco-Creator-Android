package com.example.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun SubscriptionPlansScreen(
    onNavigate: (Screen) -> Unit
) {
    val plan by CreatorRepository.subscriptionPlan.collectAsState()
    var selectedTierId by remember { mutableStateOf(plan.tiers.firstOrNull()?.id ?: "") }
    var subscribedTierName by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_subscription_plans"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onNavigate(Screen.Home) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = "Membership Tiers",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "Support Elena Rostova directly and unlock exclusive subscriber posts, masterclass recordings, and member discounts.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        plan.tiers.forEach { tier ->
            val isSelected = selectedTierId == tier.id
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) SomulecoPurpleLight else SurfaceWhite),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(if (isSelected) SomulecoPurple else BorderSubtle, if (isSelected) SomulecoPurple else BorderSubtle)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tier.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (tier.monthlyPrice == 0.0) "Free" else "$${tier.monthlyPrice.toInt()}/mo",
                            fontWeight = FontWeight.ExtraBold,
                            color = SomulecoPurpleDark,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Text(tier.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    tier.perks.forEach { perk ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SomulecoPurple, modifier = Modifier.size(16.dp))
                            Text(perk, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            selectedTierId = tier.id
                            subscribedTierName = tier.name
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) SomulecoPurple else SomulecoBlue
                        )
                    ) {
                        Text(
                            text = if (subscribedTierName == tier.name) "Subscribed (Active)" else if (tier.monthlyPrice == 0.0) "Join Free" else "Join for $${tier.monthlyPrice.toInt()}/mo",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
