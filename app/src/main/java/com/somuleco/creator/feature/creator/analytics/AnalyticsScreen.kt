package com.somuleco.creator.feature.creator.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.somuleco.creator.core.design.MetricCard
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun AnalyticsScreen(
    onNavigate: (Screen) -> Unit
) {
    val analytics by CreatorRepository.analytics.collectAsState()
    val topContent by CreatorRepository.topContent.collectAsState()
    var selectedTimeframe by remember { mutableStateOf("30D") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_analytics"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Creator Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Actionable insights into your audience and monetization", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Timeframe selector
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("7D", "30D", "90D").forEach { tf ->
                    FilterChip(
                        selected = selectedTimeframe == tf,
                        onClick = { selectedTimeframe = tf },
                        label = { Text(tf) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard("Gross Revenue", analytics.grossRevenue, analytics.revenueGrowth, Icons.Outlined.MonetizationOn, SomulecoGreenText, Modifier.weight(1f))
            MetricCard("Subscribers", analytics.subscribersTotal, analytics.subscribersGrowth, Icons.Outlined.Star, SomulecoPurple, Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard("Total Followers", analytics.followersTotal, analytics.followersGrowth, Icons.Outlined.People, SomulecoBlue, Modifier.weight(1f))
            MetricCard("Views", analytics.viewsTotal, analytics.viewsGrowth, Icons.Outlined.Visibility, SomulecoCyan, Modifier.weight(1f))
        }

        // Native Visual Trend Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weekly Revenue Trend", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("+22.5% vs last month", style = MaterialTheme.typography.labelSmall, color = SomulecoGreenText, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    WeeklyBar(day = "Mon", heightFraction = 0.45f, amount = "$2.4K")
                    WeeklyBar(day = "Tue", heightFraction = 0.60f, amount = "$3.1K")
                    WeeklyBar(day = "Wed", heightFraction = 0.75f, amount = "$4.2K")
                    WeeklyBar(day = "Thu", heightFraction = 0.50f, amount = "$2.8K")
                    WeeklyBar(day = "Fri", heightFraction = 0.90f, amount = "$4.9K", isHighlighted = true)
                    WeeklyBar(day = "Sat", heightFraction = 0.70f, amount = "$3.6K")
                    WeeklyBar(day = "Sun", heightFraction = 0.65f, amount = "$3.2K")
                }
            }
        }

        // Top Content Breakdown
        Text("Content Engagement & Conversions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        topContent.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Views: ${item.views}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("Follow conversion: ${item.conversionRate}", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WeeklyBar(
    day: String,
    heightFraction: Float,
    amount: String,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height((100 * heightFraction).dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(if (isHighlighted) SomulecoPurple else SomulecoBlue.copy(alpha = 0.6f))
        )
        Text(day, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}
