package com.example.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Shield
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
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun LibraryScreen(
    onNavigate: (Screen) -> Unit,
    onOpenProductDetail: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Purchases", "Subscriptions", "Saved")

    val products by CreatorRepository.products.collectAsState()
    val plan by CreatorRepository.subscriptionPlan.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_library"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Personal Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Your purchased digital goods, active subscriptions, and DPR licenses", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceWhite,
            contentColor = SomulecoBlue,
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> {
                    // Purchases
                    items(products.take(2)) { prod ->
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
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(prod.iconEmoji, fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("License ID: ${prod.rightsRecord.id} • DPR Verified", style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.SemiBold)
                                    Text("Format: ${prod.fileFormat}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                IconButton(onClick = { /* simulated download */ }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download files", tint = SomulecoBlue)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Subscriptions
                    item {
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
                                Text("📸", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Elena Rostova • Insider Tier", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("$9.00 / month • Renews Oct 12, 2026", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text("Active & Entitled to Subscriber Content", style = MaterialTheme.typography.labelSmall, color = SomulecoGreenText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Saved
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Natural Light Portrait Fundamentals: Mastering Golden Hour", fontWeight = FontWeight.Bold)
                                Text("Bookmarked from Photography Masterclass", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
