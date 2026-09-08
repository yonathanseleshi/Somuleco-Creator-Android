package com.somuleco.creator.feature.creator.audience

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.design.MetricCard
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun AudienceScreen(
    onNavigate: (Screen) -> Unit
) {
    val analytics by CreatorRepository.analytics.collectAsState()
    val subscribers by CreatorRepository.subscribers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_audience"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Audience & Relationships", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Direct audience relationships powered by the Somuleco ecosystem", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Total Audience",
                value = analytics.followersTotal,
                trend = analytics.followersGrowth,
                icon = Icons.Outlined.Groups,
                accentColor = SomulecoBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Paid Subscribers",
                value = analytics.subscribersTotal,
                trend = analytics.subscribersGrowth,
                icon = Icons.Outlined.Star,
                accentColor = SomulecoPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Relationship Lifecycle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Relationship Conversion Funnel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                RelationshipStepRow("1. Free Follower", "14,280 users", "100%", SomulecoBlue)
                RelationshipStepRow("2. Subscriber (Insider)", "710 users", "5.0% conversion", SomulecoPurple)
                RelationshipStepRow("3. Pro Member", "210 users", "1.5% conversion", SomulecoPinkDark)
                RelationshipStepRow("4. Digital Store Buyer", "1,932 orders", "13.5% buyer rate", SomulecoGreenText)
            }
        }

        Text("Recent Audience Members", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(subscribers) { member ->
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
                                .background(CreatorGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(member.avatarEmoji, fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${member.handle} • ${member.joinedDate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = SomulecoPurpleLight) {
                            Text(
                                text = member.tierName,
                                style = MaterialTheme.typography.labelSmall,
                                color = SomulecoPurpleDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationshipStepRow(step: String, count: String, rate: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Text(step, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(count, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("($rate)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}
