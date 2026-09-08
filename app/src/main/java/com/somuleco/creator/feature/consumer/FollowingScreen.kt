package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun FollowingScreen(
    onNavigate: (Screen) -> Unit,
    onOpenChannelDetail: (String) -> Unit
) {
    val channels by CreatorRepository.channels.collectAsState()
    val followedChannels = remember(channels) { channels.filter { it.isFollowed } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_following"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Following",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Channels and creators you follow across the Somuleco network",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (followedChannels.isEmpty()) {
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
                    Text("👥", fontSize = 42.sp)
                    Text("Not Following Any Channels Yet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Follow creators to see their latest tutorials, video drops, and posts directly in your feed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { onNavigate(Screen.Explore) },
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                    ) {
                        Text("Explore Creators")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(followedChannels) { channel ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChannelDetail(channel.id) },
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
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(channel.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(channel.iconEmoji, fontSize = 24.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(channel.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(channel.handle, style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.SemiBold)
                                Text(channel.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${channel.followersCount} followers • ${channel.contentCount} posts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                            OutlinedButton(
                                onClick = { CreatorRepository.toggleFollowChannel(channel.id) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Unfollow", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
