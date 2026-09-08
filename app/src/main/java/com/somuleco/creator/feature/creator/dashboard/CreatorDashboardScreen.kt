package com.somuleco.creator.feature.creator.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.design.MetricCard
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.CreatorRecommendation
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun CreatorDashboardScreen(
    onNavigate: (Screen) -> Unit
) {
    val profile by CreatorRepository.creatorProfile.collectAsState()
    val analytics by CreatorRepository.analytics.collectAsState()
    val recommendations by CreatorRepository.recommendations.collectAsState()
    val topContent by CreatorRepository.topContent.collectAsState()
    val transactions by CreatorRepository.transactions.collectAsState()
    val goals by CreatorRepository.goals.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()
    val selectedChannelId by CreatorRepository.selectedChannelId.collectAsState()

    val selectedChannel = channels.find { it.id == selectedChannelId }
    val activeRecommendation = recommendations.firstOrNull { !it.isDismissed }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("screen_creator_dashboard"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Creator Context Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
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
                        Text(profile.avatarEmoji, fontSize = 24.sp)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(profile.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SomulecoBlue, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = if (selectedChannel != null) "Viewing: ${selectedChannel.name}" else "Viewing: All Channels Combined",
                            style = MaterialTheme.typography.bodySmall,
                            color = SomulecoPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = { onNavigate(Screen.ContentEditor) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Post", fontWeight = FontWeight.Bold)
                }
            }
        }

        // High-Priority Creator AI Recommendation (Placed High On Screen)
        if (activeRecommendation != null) {
            AIRecommendationHeroCard(
                recommendation = activeRecommendation,
                onActionClick = {
                    when (activeRecommendation.actionCategory) {
                        "PRODUCT" -> onNavigate(Screen.ProductWizard)
                        "COMMUNITY" -> onNavigate(Screen.ContentEditor)
                        else -> onNavigate(Screen.CreatorAI)
                    }
                },
                onDismiss = {
                    CreatorRepository.dismissRecommendation(activeRecommendation.id)
                }
            )
        }

        // Core Metrics Grid (2 columns on mobile)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Performance Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Gross Revenue",
                    value = analytics.grossRevenue,
                    trend = analytics.revenueGrowth,
                    icon = Icons.Outlined.MonetizationOn,
                    accentColor = SomulecoGreenText,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Followers",
                    value = analytics.followersTotal,
                    trend = analytics.followersGrowth,
                    icon = Icons.Outlined.People,
                    accentColor = SomulecoBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Views",
                    value = analytics.viewsTotal,
                    trend = analytics.viewsGrowth,
                    icon = Icons.Outlined.Visibility,
                    accentColor = SomulecoCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Row
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton("Post", Icons.Outlined.Edit, SomulecoBlue) { onNavigate(Screen.ContentEditor) }
                QuickActionButton("Product", Icons.Outlined.ShoppingBag, SomulecoGreenText) { onNavigate(Screen.ProductWizard) }
                QuickActionButton("Media", Icons.Outlined.Upload, SomulecoCyan) { onNavigate(Screen.Media) }
                QuickActionButton("Subscribers", Icons.Outlined.Group, SomulecoPinkDark) { onNavigate(Screen.Subscribers) }
                QuickActionButton("Ask AI", Icons.Outlined.AutoAwesome, SomulecoPurple) { onNavigate(Screen.CreatorAI) }
            }
        }

        // Top Content Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Performing Content",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate(Screen.Content) }) {
                    Text("View All", color = SomulecoBlue, fontWeight = FontWeight.Bold)
                }
            }

            topContent.forEach { item ->
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SomulecoBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (item.format == "Video") "🎥" else "📝", fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.channelName} • ${item.views}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = SomulecoGreenSurface) {
                            Text(
                                text = "+${item.newFollowers} followers",
                                style = MaterialTheme.typography.labelSmall,
                                color = SomulecoGreenText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Recent Activity & Sales
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions & Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate(Screen.Revenue) }) {
                    Text("Revenue Hub", color = SomulecoBlue, fontWeight = FontWeight.Bold)
                }
            }

            transactions.take(3).forEach { tx ->
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
                        Column {
                            Text(tx.itemTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${tx.customerName} • ${tx.date}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Text(
                            text = "+$${"%.2f".format(tx.netAmount)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = SomulecoGreenText,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }

        // Creator Progress & Goals
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Creator Milestones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            goals.forEach { goal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(goal.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(goal.targetValue, fontWeight = FontWeight.Bold, color = SomulecoPurple)
                        }
                        LinearProgressIndicator(
                            progress = { goal.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (goal.isAchieved) SomulecoGreenText else SomulecoPurple,
                            trackColor = SurfaceMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AIRecommendationHeroCard(
    recommendation: CreatorRecommendation,
    onActionClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_recommendation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SomulecoPurpleLight),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(SomulecoPurpleBorder, SomulecoPinkBorder))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SomulecoPurple, modifier = Modifier.size(18.dp))
                    Text(
                        text = "CREATOR AI INSIGHT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SomulecoPurpleDark,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceWhite
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SomulecoGreenText, modifier = Modifier.size(14.dp))
                    Text(
                        text = recommendation.supportingMetric,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SomulecoGreenText
                    )
                }
            }

            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(recommendation.actionLabel, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
