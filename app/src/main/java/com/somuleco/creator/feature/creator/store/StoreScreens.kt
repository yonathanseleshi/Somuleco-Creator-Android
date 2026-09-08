package com.somuleco.creator.feature.creator.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.design.RightsTrustBadge
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.CreatorProduct
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun StoreScreen(
    onNavigate: (Screen) -> Unit,
    onOpenProductDetail: (String) -> Unit
) {
    val products by CreatorRepository.products.collectAsState()
    val totalRevenue = products.sumOf { it.revenueTotal }
    val totalSales = products.sumOf { it.salesCount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_store"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Creator Store", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Digital merchandise protected by Somuleco DPR", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Button(
                onClick = { onNavigate(Screen.ProductWizard) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Product", fontWeight = FontWeight.Bold)
            }
        }

        // Store Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$${"%,.0f".format(totalRevenue)}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = SomulecoGreenText)
                    Text("Total Store Sales", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                VerticalDivider(modifier = Modifier.height(40.dp), color = BorderSubtle)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$totalSales", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = SomulecoPurple)
                    Text("Products Delivered", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                VerticalDivider(modifier = Modifier.height(40.dp), color = BorderSubtle)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${products.size}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = SomulecoBlue)
                    Text("Active Listings", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }

        Text("Store Products & Digital Assets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductManagementCard(
                    product = product,
                    onClick = { onOpenProductDetail(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductsScreen(
    onNavigate: (Screen) -> Unit,
    onOpenProductDetail: (String) -> Unit
) {
    StoreScreen(onNavigate = onNavigate, onOpenProductDetail = onOpenProductDetail)
}

@Composable
fun ProductManagementCard(
    product: CreatorProduct,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SomulecoBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(product.iconEmoji, fontSize = 22.sp)
                    }
                    Column {
                        Text(product.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${product.channelName} • ${product.fileFormat}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                Text(
                    text = "$${product.priceAmount.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    color = SomulecoBlueDark,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = product.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            RightsTrustBadge(licenseName = product.rightsRecord.licenseName)

            HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${product.salesCount} sold", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text("$${"%,.0f".format(product.revenueTotal)} earned", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (product.isMarketplaceListed) {
                        Surface(shape = RoundedCornerShape(6.dp), color = SomulecoPurpleLight) {
                            Text("Marketplace Listed", color = SomulecoPurpleDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
