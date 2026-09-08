package com.example.feature.consumer

import androidx.compose.foundation.background
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
import com.example.core.design.RightsTrustBadge
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigate: (Screen) -> Unit,
    onViewInLibrary: () -> Unit
) {
    val products by CreatorRepository.products.collectAsState()
    val product = products.find { it.id == productId } ?: products.first()

    var showPurchaseSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_product_detail"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate(Screen.Explore) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            IconButton(onClick = { /* share */ }) {
                Icon(Icons.Outlined.Share, contentDescription = "Share")
            }
        }

        // Product Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CreatorGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(product.iconEmoji, fontSize = 56.sp)
        }

        // Title and Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Created by Elena Rostova • ${product.channelName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                text = "$${product.priceAmount.toInt()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = SomulecoBlueDark
            )
        }

        RightsTrustBadge(licenseName = product.rightsRecord.licenseName)

        // Description Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Product Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(product.fullDescription, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }

        // Deliverables Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Included Digital Deliverables", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                DeliverableRow("📦 Complete Master Bundle", product.fileFormat)
                DeliverableRow("🛡️ DPR Ownership Certificate", "Digital Product Rights registration #${product.rightsRecord.id}")
                DeliverableRow("📖 Quick Installation & Workflow Guide", "PDF Document")
            }
        }

        // Rights details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SomulecoBlueLight)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Digital Product Rights Verification", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SomulecoBlueDark)
                Text("• Customer downloads: Allowed (unlimited redownloads from Library)", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark)
                Text("• Commercial client projects: Permitted", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark)
                Text("• Redistribution / Resale: Strictly prohibited", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark)
            }
        }

        // Purchase Button
        Button(
            onClick = {
                CreatorRepository.purchaseProduct(product.id)
                showPurchaseSuccess = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("button_buy_product"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
        ) {
            Text("Buy Now - $${product.priceAmount.toInt()}.00 USD", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showPurchaseSuccess) {
        AlertDialog(
            onDismissRequest = { showPurchaseSuccess = false },
            title = { Text("Purchase Successful! 🎉", fontWeight = FontWeight.Bold) },
            text = {
                Text("You now own '${product.title}'. Your DPR license certificate has been minted and files have been added to your personal Library.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPurchaseSuccess = false
                        onViewInLibrary()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoGreenDark)
                ) {
                    Text("View in Library")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseSuccess = false }) { Text("Done") }
            }
        )
    }
}

@Composable
private fun DeliverableRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SomulecoGreenText, modifier = Modifier.size(16.dp))
    }
}
