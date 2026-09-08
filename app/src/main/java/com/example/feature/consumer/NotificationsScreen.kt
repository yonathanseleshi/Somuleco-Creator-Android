package com.example.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    onNavigate: (Screen) -> Unit
) {
    val notifications by CreatorRepository.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_notifications"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Channel updates, subscriber activity, and product sales", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            IconButton(onClick = { CreatorRepository.markAllNotificationsRead() }) {
                Icon(Icons.Default.DoneAll, contentDescription = "Mark all read", tint = SomulecoBlue)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { CreatorRepository.markNotificationRead(notif.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notif.isRead) SurfaceWhite else SomulecoBlueLight
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(notif.iconEmoji, fontSize = 24.sp)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(notif.timestamp, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Text(notif.message, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }

                        if (!notif.isRead) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SomulecoBlue))
                        }
                    }
                }
            }
        }
    }
}
