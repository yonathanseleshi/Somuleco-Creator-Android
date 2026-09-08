package com.somuleco.creator.feature.creator.rights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
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
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun RightsRegistryScreen(
    onNavigate: (Screen) -> Unit,
    onOpenProductDetail: (String) -> Unit
) {
    val products by CreatorRepository.products.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_rights_registry"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Digital Product Rights (DPR)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Verifiable ownership, licensing, and protected delivery registry", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        // DPR Trust Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SomulecoBlueLight),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SomulecoBlueBorder, SomulecoBlueBorder)))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SomulecoBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("All Creator Assets Protected", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SomulecoBlueDark)
                    Text(
                        text = "Your digital products carry cryptographic proof of ownership, license boundaries, and entitlement verification across the Somuleco ecosystem.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SomulecoBlueDark
                    )
                }
            }
        }

        Text("Registered Products (${products.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { prod ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProductDetail(prod.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prod.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            Surface(shape = RoundedCornerShape(6.dp), color = SomulecoGreenSurface) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SomulecoGreenText, modifier = Modifier.size(12.dp))
                                    Text("Verified", color = SomulecoGreenText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text("License: ${prod.rightsRecord.licenseName}", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark, fontWeight = FontWeight.SemiBold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RightsPill(label = "Downloads", enabled = prod.rightsRecord.allowDownload)
                            RightsPill(label = "Commercial", enabled = prod.rightsRecord.allowCommercialUse)
                            RightsPill(label = "Resale", enabled = prod.rightsRecord.allowRedistribution)
                            RightsPill(label = "AI Training", enabled = prod.rightsRecord.allowAiTraining)
                        }

                        HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Record ID: ${prod.rightsRecord.id} • ${prod.rightsRecord.registeredAt}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("View DPR Certificate", style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RightsPill(label: String, enabled: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (enabled) SomulecoBlueLight else SurfaceMuted
    ) {
        Text(
            text = "$label: ${if (enabled) "Yes" else "No"}",
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) SomulecoBlueDark else TextMuted,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
