package com.example.feature.creator.subscribers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun SubscribersScreen(
    onNavigate: (Screen) -> Unit
) {
    val subscribers by CreatorRepository.subscribers.collectAsState()
    val plan by CreatorRepository.subscriptionPlan.collectAsState()

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
                onClick = { onNavigate(Screen.SubscriptionPlansDetail) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
            ) {
                Icon(Icons.Outlined.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Manage Plans", fontWeight = FontWeight.Bold)
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
                Text("Active Membership Tiers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
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

        Text("Subscriber Roster", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SomulecoPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sub.avatarEmoji, fontSize = 20.sp)
                            }
                            Column {
                                Text(sub.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("${sub.tierName} • ${sub.joinedDate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                        Text("+$${sub.monthlyAmount.toInt()}/mo", fontWeight = FontWeight.Bold, color = SomulecoGreenText)
                    }
                }
            }
        }
    }
}
