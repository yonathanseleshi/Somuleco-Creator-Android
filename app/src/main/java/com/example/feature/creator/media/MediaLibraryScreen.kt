package com.example.feature.creator.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.core.navigation.Screen
import com.example.data.model.MediaType
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun MediaLibraryScreen(
    onNavigate: (Screen) -> Unit
) {
    val mediaItems by CreatorRepository.mediaItems.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Images", "Videos", "Audio", "Documents")

    var showUploadModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_media_library"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Media Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("High-res photos, videos, masterclass recordings & assets", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Button(
                onClick = { showUploadModal = true },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoCyan)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload", fontWeight = FontWeight.Bold)
            }
        }

        // Filter Bar
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mediaItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SomulecoBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (item.type) {
                                    MediaType.IMAGE -> "📸"
                                    MediaType.VIDEO -> "🎥"
                                    MediaType.AUDIO -> "🎧"
                                    MediaType.DOCUMENT -> "📁"
                                },
                                fontSize = 24.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${item.size} • ${item.uploadedAt}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Used in: ${item.channelName}", style = MaterialTheme.typography.labelSmall, color = SomulecoPurple, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            title = { Text("Upload Creative Media", fontWeight = FontWeight.Bold) },
            text = {
                Text("Select images, audio stems, video files, or document archives to upload directly to your Creator Cloud storage.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        CreatorRepository.uploadMediaItem("studio_session_raw_cut.mp4", "VIDEO", "142 MB", "Photography Masterclass")
                        showUploadModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                ) {
                    Text("Simulate Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) { Text("Cancel") }
            }
        )
    }
}
