package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.design.AccessBadge
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.AccessType
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun ContentDetailScreen(
    contentId: String,
    onNavigate: (Screen) -> Unit,
    onOpenSubscriptionPlans: () -> Unit
) {
    val contentList by CreatorRepository.contentItems.collectAsState()
    val comments by CreatorRepository.comments.collectAsState()
    val item = contentList.find { it.id == contentId } ?: contentList.first()

    var commentText by remember { mutableStateOf("") }
    var isUnlocked by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_content_detail"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate(Screen.Home) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* share */ }) {
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
                }
                IconButton(onClick = { /* bookmark */ }) {
                    Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark")
                }
            }
        }

        // Header info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CreatorGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.coverEmoji, fontSize = 22.sp)
                }
                Column {
                    Text(item.channelName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Published ${item.publishedDate}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
            AccessBadge(accessType = item.accessType)
        }

        // Title
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SomulecoBlueLight)
        ) {
            Text(
                text = "Summary: ${item.summary}",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = SomulecoBlueDark,
                fontWeight = FontWeight.Medium
            )
        }

        // Body Text
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    lineHeight = 24.sp
                )

                if (item.mediaDuration != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📎", fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Attached Creative Media (${item.mediaDuration})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text("High-resolution sample files included for subscribers", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Text("Download", style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Engagement Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { CreatorRepository.toggleLike(item.id) }
            ) {
                Icon(
                    imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (item.isLiked) SomulecoPink else TextSecondary
                )
                Text("${item.likesCount} Likes", fontWeight = FontWeight.Bold, color = if (item.isLiked) SomulecoPink else TextPrimary)
            }

            Text("${comments.size} Comments", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        HorizontalDivider(color = BorderSubtle)

        // Comments Section
        Text("Subscriber Discussion", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        comments.forEach { c ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(c.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text(c.timestamp, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Text(c.text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }
        }

        // Add Comment Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Write a comment or question...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
            )

            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        CreatorRepository.addComment(commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier.clip(CircleShape).background(SomulecoBlue),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
