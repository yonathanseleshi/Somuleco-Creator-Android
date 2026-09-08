package com.somuleco.creator.feature.creator.content

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.AccessType
import com.somuleco.creator.data.model.ContentType
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEditorScreen(
    onNavigate: (Screen) -> Unit,
    onContentPublished: () -> Unit
) {
    val channels by CreatorRepository.channels.collectAsState()

    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedChannelId by remember { mutableStateOf(channels.firstOrNull()?.id ?: "") }
    var selectedType by remember { mutableStateOf(ContentType.ARTICLE) }
    var selectedAccess by remember { mutableStateOf(AccessType.PUBLIC) }
    var attachedMediaName by remember { mutableStateOf<String?>(null) }
    var isPublishing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_content_editor"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top action bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate(Screen.Content) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Surface(shape = RoundedCornerShape(12.dp), color = SomulecoGreenSurface) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SomulecoGreenText))
                    Text("Autosaved to Drafts", style = MaterialTheme.typography.labelSmall, color = SomulecoGreenText, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        isPublishing = true
                        CreatorRepository.createContentItem(
                            title = title,
                            body = body.ifBlank { summary },
                            summary = summary.ifBlank { title },
                            channelId = selectedChannelId,
                            contentType = selectedType,
                            accessType = selectedAccess
                        )
                        onContentPublished()
                    }
                },
                enabled = title.isNotBlank() && !isPublishing,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue),
                modifier = Modifier.testTag("button_publish_post")
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("Publish", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Enter an engaging title...", style = MaterialTheme.typography.titleMedium) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_content_title"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
        )

        // Summary Input
        OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            placeholder = { Text("Short summary or key takeaway for the feed preview...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
        )

        // Body Content Input
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            placeholder = { Text("Write full masterclass content, lesson breakdown, tips, or announcement...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("input_content_body"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
        )

        // Channel Selector
        Text("Target Channel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            channels.forEach { ch ->
                FilterChip(
                    selected = selectedChannelId == ch.id,
                    onClick = { selectedChannelId = ch.id },
                    label = { Text(ch.name) }
                )
            }
        }

        // Access Level Selector
        Text("Who Can Access?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AccessType.values().forEach { access ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedAccess = access },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedAccess == access) SomulecoPurpleLight else SurfaceWhite
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(access.label, fontWeight = if (selectedAccess == access) FontWeight.Bold else FontWeight.Normal)
                        RadioButton(selected = selectedAccess == access, onClick = { selectedAccess = access })
                    }
                }
            }
        }

        // Media attachment simulator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { attachedMediaName = "golden_hour_session_raw.jpg" }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Outlined.AttachFile, contentDescription = null, tint = SomulecoBlue)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = attachedMediaName ?: "Attach Media (Photo, Video, Raw File)",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Simulate native file picker attachment", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                if (attachedMediaName != null) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SomulecoGreenText)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
