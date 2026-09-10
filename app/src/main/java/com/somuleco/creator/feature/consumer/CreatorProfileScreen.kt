package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun CreatorProfileScreen(
    onNavigate: (Screen) -> Unit,
    onOpenContentDetail: (String) -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onOpenSubscriptionPlans: () -> Unit
) {
    val profile by CreatorRepository.creatorProfile.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()
    val contentList by CreatorRepository.contentItems.collectAsState()
    val products by CreatorRepository.products.collectAsState()

    var isFollowing by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Home", "Channels", "Posts", "Store")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("screen_creator_profile")
    ) {
        // Hero Cover Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(HeroSurfaceGradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onNavigate(Screen.Home) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                IconButton(onClick = { /* share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
                }
            }
        }

        // Profile Info Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(CreatorGradient)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Text(profile.avatarEmoji, fontSize = 36.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { isFollowing = !isFollowing },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Text(if (isFollowing) "Following" else "Follow", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onOpenSubscriptionPlans,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                    ) {
                        Text("Subscribe $9/mo", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.offset(y = (-20).dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(profile.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SomulecoBlue, modifier = Modifier.size(18.dp))
                }
                Text("${profile.handle} • ${profile.category}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(profile.bio, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("14.2K Followers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("920 Subscribers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SomulecoPurple)
                    Text("3 Products", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = SomulecoBlue)
                }
            }
        }

        // Profile Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceWhite,
            contentColor = SomulecoPurple,
            divider = { HorizontalDivider(color = BorderSubtle) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Home overview: channels preview + latest posts
                    item {
                        Text("Creator Channels", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }
                    items(channels) { ch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(ch.iconEmoji, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ch.name, fontWeight = FontWeight.Bold)
                                    Text(ch.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recent Publications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }
                    items(contentList.take(2)) { item ->
                        ConsumerFeedCard(item = item, onClick = { onOpenContentDetail(item.id) }, onLike = { CreatorRepository.toggleLike(item.id) })
                    }
                }
                1 -> {
                    // Channels list
                    items(channels) { ch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(ch.iconEmoji, fontSize = 24.sp)
                                    Text(ch.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                Text(ch.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                Text("${ch.followersCount} Followers • ${ch.contentCount} Posts", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
                2 -> {
                    // All Posts
                    items(contentList) { item ->
                        ConsumerFeedCard(item = item, onClick = { onOpenContentDetail(item.id) }, onLike = { CreatorRepository.toggleLike(item.id) })
                    }
                }
                3 -> {
                    // Store Products
                    items(products) { prod ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenProductDetail(prod.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(prod.iconEmoji, fontSize = 26.sp)
                                    Column {
                                        Text(prod.title, fontWeight = FontWeight.Bold)
                                        Text("${prod.fileFormat} • DPR Protected", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                                Text("$${prod.price.amount / 100}", fontWeight = FontWeight.Bold, color = SomulecoBlueDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
