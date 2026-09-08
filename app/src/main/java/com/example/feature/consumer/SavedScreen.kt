package com.example.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun SavedScreen(
    onNavigate: (Screen) -> Unit,
    onOpenContentDetail: (String) -> Unit
) {
    val contentItems by CreatorRepository.contentItems.collectAsState()
    val savedItems = remember(contentItems) { contentItems.filter { it.isSaved } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_saved"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Saved Content & Bookmarks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Masterclasses, breakdowns, and reference tutorials you've bookmarked",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (savedItems.isEmpty()) {
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
                    Text("🔖", fontSize = 42.sp)
                    Text("No Saved Content", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Bookmark tutorials, breakdowns, and guides from creators to access them here anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { onNavigate(Screen.Home) },
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                    ) {
                        Text("Browse Feed")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(savedItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenContentDetail(item.id) },
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
                            Text(item.coverEmoji, fontSize = 32.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(shape = RoundedCornerShape(6.dp), color = SomulecoBlueLight) {
                                    Text(
                                        item.channelName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SomulecoBlue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(item.summary, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Published ${item.publishedDate} • ${item.likesCount} likes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            IconButton(
                                onClick = { CreatorRepository.toggleSave(item.id) }
                            ) {
                                Icon(
                                    imageVector = if (item.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Remove bookmark",
                                    tint = SomulecoPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
