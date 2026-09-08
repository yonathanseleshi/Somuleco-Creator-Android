package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Verified
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
fun ExploreScreen(
    onNavigate: (Screen) -> Unit,
    onOpenCreatorProfile: () -> Unit,
    onOpenProductDetail: (String) -> Unit
) {
    val products by CreatorRepository.products.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Photography", "Sound & Audio", "AI & Code", "Education", "Design")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_explore"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Explore Creators & Stores", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Discover verified creators, masterclass channels, and digital goods", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        // Category pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Featured Creators Section
            item {
                Text("Featured Creators", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            item {
                CreatorDiscoveryCard(
                    name = "Elena Rostova",
                    handle = "@elenarostova",
                    discipline = "Photography & Lighting",
                    avatar = "📸",
                    bio = "Natural light portraits, color science, and client workflow systems.",
                    onClick = onOpenCreatorProfile
                )
            }

            item {
                CreatorDiscoveryCard(
                    name = "Marcus Vance",
                    handle = "@marcusvance",
                    discipline = "Modular Sound Design",
                    avatar = "🎧",
                    bio = "Electronic music producer releasing sound design sample vaults and synth masterclasses.",
                    onClick = onOpenCreatorProfile
                )
            }

            // Featured Products Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Trending Digital Products", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(products) { prod ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProductDetail(prod.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(prod.iconEmoji, fontSize = 28.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(prod.shortDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                            Text("Protected by DPR", style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.Bold)
                        }
                        Text("$${prod.priceAmount.toInt()}", fontWeight = FontWeight.ExtraBold, color = SomulecoBlueDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorDiscoveryCard(
    name: String,
    handle: String,
    discipline: String,
    avatar: String,
    bio: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CreatorGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SomulecoBlue, modifier = Modifier.size(16.dp))
                }
                Text("$handle • $discipline", style = MaterialTheme.typography.bodySmall, color = SomulecoPurple, fontWeight = FontWeight.SemiBold)
                Text(bio, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
