package com.somuleco.creator.feature.creator.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.somuleco.creator.data.model.AccessType
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    channelId: String,
    onNavigate: (Screen) -> Unit,
    onBackClick: () -> Unit
) {
    val channels by CreatorRepository.channels.collectAsState()
    val channel = remember(channels, channelId) {
        channels.find { it.id == channelId } ?: channels.firstOrNull()
    }
    val contentItems by CreatorRepository.contentItems.collectAsState()
    val channelPosts = remember(contentItems, channel) {
        contentItems.filter { it.channelId == channel?.id }
    }

    if (channel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Channel not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(channel.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.ContentEditor) }) {
                        Icon(Icons.Default.Add, contentDescription = "New Post")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
                .padding(16.dp)
                .testTag("screen_channel_detail"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Channel Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(channel.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(channel.iconEmoji, fontSize = 32.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(channel.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(channel.handle, style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.SemiBold)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SomulecoBlueLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        channel.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SomulecoBlue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(channel.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    CreatorRepository.selectChannel(channel.id)
                                    onNavigate(Screen.ContentEditor)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("New Post in Channel")
                            }
                            OutlinedButton(
                                onClick = { CreatorRepository.toggleFollowChannel(channel.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (channel.isFollowed) "Following" else "Follow")
                            }
                        }
                    }
                }
            }

            // Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Followers",
                        value = "${channel.followersCount}",
                        trend = "+12%",
                        icon = Icons.Outlined.Group,
                        accentColor = SomulecoBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Subscribers",
                        value = "${channel.subscribersCount}",
                        trend = "+8%",
                        icon = Icons.Outlined.Star,
                        accentColor = SomulecoPurple,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Posts",
                        value = "${channelPosts.size}",
                        trend = "Active",
                        icon = Icons.Outlined.Article,
                        accentColor = SomulecoGreenText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Channel Content List
            item {
                Text(
                    text = "Channel Content (${channelPosts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (channelPosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📝", fontSize = 32.sp)
                            Text("No posts in this channel yet", fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { onNavigate(Screen.ContentEditor) },
                                colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                            ) {
                                Text("Create First Post")
                            }
                        }
                    }
                }
            } else {
                items(channelPosts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.ContentDetail) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(post.coverEmoji, fontSize = 28.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(post.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(post.summary, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (post.accessType == AccessType.PUBLIC) SomulecoBlueLight else SomulecoPurpleLight
                                    ) {
                                        Text(
                                            post.accessType.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (post.accessType == AccessType.PUBLIC) SomulecoBlue else SomulecoPurpleDark,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(post.publishedDate, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    Text("❤️ ${post.likesCount}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
