package com.somuleco.creator.feature.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
fun SearchScreen(
    onNavigate: (Screen) -> Unit,
    onOpenContentDetail: (String) -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onOpenCreatorProfile: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Creators", "Channels", "Posts", "Products")

    val contentList by CreatorRepository.contentItems.collectAsState()
    val products by CreatorRepository.products.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()

    val filteredContent = contentList.filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) }
    val filteredProducts = products.filter { it.title.contains(query, ignoreCase = true) || it.shortDescription.contains(query, ignoreCase = true) }
    val filteredChannels = channels.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_search"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search creators, channels, posts, digital products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SomulecoBlue) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_query"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite
            )
        )

        // Segmented Tabs
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

        // Search Results List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (selectedTab == 0 || selectedTab == 1) {
                item {
                    Text("Creators", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCreatorProfile() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("📸", fontSize = 24.sp)
                            Column {
                                Text("Elena Rostova (@elenarostova)", fontWeight = FontWeight.Bold)
                                Text("Photography & Lighting Educator • 920 Subscribers", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0 || selectedTab == 2) {
                item {
                    Text("Channels (${filteredChannels.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                items(filteredChannels) { ch ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCreatorProfile() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(ch.iconEmoji, fontSize = 24.sp)
                            Column {
                                Text(ch.name, fontWeight = FontWeight.Bold)
                                Text(ch.handle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0 || selectedTab == 3) {
                item {
                    Text("Posts & Masterclasses (${filteredContent.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                items(filteredContent) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenContentDetail(post.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(post.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${post.channelName} • ${post.publishedDate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            if (selectedTab == 0 || selectedTab == 4) {
                item {
                    Text("Digital Products (${filteredProducts.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                items(filteredProducts) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProductDetail(prod.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(prod.iconEmoji, fontSize = 24.sp)
                                Column {
                                    Text(prod.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("DPR Protected • ${prod.fileFormat}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }
                            Text("$${prod.priceAmount.toInt()}", fontWeight = FontWeight.Bold, color = SomulecoBlueDark)
                        }
                    }
                }
            }
        }
    }
}
