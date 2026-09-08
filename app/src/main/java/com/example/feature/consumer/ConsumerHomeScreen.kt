package com.example.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.core.design.AccessBadge
import com.example.core.navigation.Screen
import com.example.data.model.ContentItem
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun ConsumerHomeScreen(
    onNavigate: (Screen) -> Unit,
    onOpenContentDetail: (String) -> Unit,
    onOpenCreatorProfile: () -> Unit
) {
    val contentList by CreatorRepository.contentItems.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()
    val profile by CreatorRepository.creatorProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("screen_consumer_home"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Channel Avatar Stories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onOpenCreatorProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CreatorGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(profile.avatarEmoji, fontSize = 26.sp)
                    }
                    Text("Elena", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            items(channels) { ch ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onOpenCreatorProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SomulecoBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ch.iconEmoji, fontSize = 24.sp)
                    }
                    Text(ch.name.take(8) + "..", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }

        // Creator Callout / Announcement Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SomulecoPurpleLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("⭐", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Supporting Elena Rostova", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SomulecoPurpleDark)
                    Text("You have unlocked full access to all Insider Subscriber posts & presets.", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                }
            }
        }

        Text("Following Feed", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(contentList) { item ->
                ConsumerFeedCard(
                    item = item,
                    onClick = { onOpenContentDetail(item.id) },
                    onLike = { CreatorRepository.toggleLike(item.id) }
                )
            }
        }
    }
}

@Composable
fun ConsumerFeedCard(
    item: ContentItem,
    onClick: () -> Unit,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("feed_card_${item.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CreatorGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.coverEmoji, fontSize = 18.sp)
                    }
                    Column {
                        Text(item.channelName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(item.publishedDate, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }

                AccessBadge(accessType = item.accessType)
            }

            Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(item.summary, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onLike() }
                    ) {
                        Icon(
                            imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (item.isLiked) SomulecoPink else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("${item.likesCount}", style = MaterialTheme.typography.bodySmall, color = if (item.isLiked) SomulecoPink else TextSecondary)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comments", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Text("${item.commentsCount}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }

                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
