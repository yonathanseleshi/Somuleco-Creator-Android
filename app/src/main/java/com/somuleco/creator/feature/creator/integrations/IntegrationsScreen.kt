package com.somuleco.creator.feature.creator.integrations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
fun IntegrationsScreen(
    onNavigate: (Screen) -> Unit
) {
    val integrations by CreatorRepository.integrations.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_integrations"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Integrations & Ecosystem", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Connect external channels and Somuleco platform services", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(integrations) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(item.iconEmoji, fontSize = 28.sp)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                if (item.category == "ECOSYSTEM") {
                                    Surface(shape = RoundedCornerShape(6.dp), color = SomulecoBlueLight) {
                                        Text("Somuleco", color = SomulecoBlueDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(item.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }

                        Switch(
                            checked = item.isConnected,
                            onCheckedChange = { CreatorRepository.toggleIntegration(item.id) }
                        )
                    }
                }
            }
        }
    }
}
