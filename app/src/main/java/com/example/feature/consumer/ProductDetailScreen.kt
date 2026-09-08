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
import com.example.data.model.MarketplaceListing
import com.example.data.model.ProductRightsConfig
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigate: (Screen) -> Unit,
    onViewInLibrary: () -> Unit
) {
    val products by CreatorRepository.products.collectAsState()
    val isCreatorMode by CreatorRepository.isCreatorMode.collectAsState()
    val product = products.find { it.id == productId } ?: products.first()

    var showPurchaseSuccess by remember { mutableStateOf(false) }
    var showDprConfigSheet by remember { mutableStateOf(false) }
    var showMarketplaceSheet by remember { mutableStateOf(false) }

    // DPR Config Form
    var allowCommercial by remember(product) { mutableStateOf(product.rightsRecord.allowCommercialUse) }
    var allowModifications by remember(product) { mutableStateOf(product.rightsRecord.allowModification) }
    var prohibitAiTraining by remember(product) { mutableStateOf(!product.rightsRecord.allowAiTraining) }
    var licenseType by remember(product) { mutableStateOf(product.rightsRecord.licenseName) }

    // Marketplace Form
    var isMarketplaceListed by remember(product) { mutableStateOf(product.isMarketplaceListed) }
    var channelStoreListed by remember { mutableStateOf(true) }
    var marketplacePrice by remember(product) { mutableStateOf(product.priceAmount.toString()) }

    var actionToast by remember { mutableStateOf<String?>(null) }

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
            IconButton(onClick = { onNavigate(if (isCreatorMode) Screen.Products else Screen.Explore) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Row {
                if (isCreatorMode) {
                    IconButton(onClick = { showDprConfigSheet = true }) {
                        Icon(Icons.Outlined.Shield, contentDescription = "Configure DPR Rights", tint = SomulecoBlue)
                    }
                    IconButton(onClick = { showMarketplaceSheet = true }) {
                        Icon(Icons.Outlined.Storefront, contentDescription = "Marketplace Publishing", tint = SomulecoPurple)
                    }
                }
                IconButton(onClick = { actionToast = "Product link copied to clipboard." }) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                }
            }
        }

        actionToast?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SomulecoBlueLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SomulecoBlueDark)
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark, modifier = Modifier.weight(1f))
                    TextButton(onClick = { actionToast = null }) { Text("Dismiss", color = SomulecoBlueDark) }
                }
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
                Text("Created by ${product.creatorName} • ${product.channelName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                text = "$${product.priceAmount.toInt()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = SomulecoBlueDark
            )
        }

        RightsTrustBadge(licenseName = licenseType)

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

        // Creator Management Controls (if creator mode)
        if (isCreatorMode) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SomulecoPurpleLight)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Creator Publishing & DPR Controls", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SomulecoPurpleDark)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showDprConfigSheet = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                        ) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DPR Rights", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = { showMarketplaceSheet = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                        ) {
                            Icon(Icons.Outlined.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Marketplace", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
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
                Text("• Commercial client projects: ${if (allowCommercial) "Permitted" else "Prohibited"}", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark)
                Text("• AI Model Training: ${if (prohibitAiTraining) "Protected (Prohibited)" else "Allowed"}", style = MaterialTheme.typography.bodySmall, color = SomulecoBlueDark)
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

    // DPR Configuration Bottom Sheet
    if (showDprConfigSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDprConfigSheet = false },
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Digital Product Rights (DPR) Setup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Configure smart licensing parameters recorded onto Somuleco's cryptographic rights registry.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Commercial Use License", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("Permit buyers to use in client work", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(checked = allowCommercial, onCheckedChange = { allowCommercial = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Derivative Modifications", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("Permit editing or modifying original files", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(checked = allowModifications, onCheckedChange = { allowModifications = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AI Scraping Protection", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("Cryptographically forbid model ingestion", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(checked = prohibitAiTraining, onCheckedChange = { prohibitAiTraining = it })
                }

                Card(colors = CardDefaults.cardColors(containerColor = BackgroundLight), shape = RoundedCornerShape(10.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Cryptographic Registry Hash", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("SHA256: 7f8a9e...e3b482a01", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Button(
                    onClick = {
                        val newLic = if (allowCommercial) "Commercial Creator License" else "Personal Non-Commercial License"
                        CreatorRepository.updateProductRights(
                            ProductRightsConfig(
                                productId = product.id,
                                productTitle = product.title,
                                certificateId = product.rightsRecord.id,
                                registeredOwner = product.creatorName,
                                allowCommercialUse = allowCommercial,
                                allowModification = allowModifications,
                                allowAiTraining = !prohibitAiTraining,
                                licenseType = newLic
                            )
                        )
                        licenseType = newLic
                        showDprConfigSheet = false
                        actionToast = "DPR Smart Rights configuration saved and verified!"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                ) {
                    Text("Save Rights Configuration")
                }
            }
        }
    }

    // Marketplace Publishing Bottom Sheet
    if (showMarketplaceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMarketplaceSheet = false },
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Marketplace & Distribution Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Control where your digital product is discoverable and purchased.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Global Somuleco Marketplace", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("Feature in search, explore, and category feeds", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(checked = isMarketplaceListed, onCheckedChange = { isMarketplaceListed = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Channel Storefront", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text("Display in your channel's product showcase", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(checked = channelStoreListed, onCheckedChange = { channelStoreListed = it })
                }

                OutlinedTextField(
                    value = marketplacePrice,
                    onValueChange = { marketplacePrice = it },
                    label = { Text("List Price (USD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(colors = CardDefaults.cardColors(containerColor = SomulecoGreenSurface), shape = RoundedCornerShape(10.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Creator Earnings Breakdown", style = MaterialTheme.typography.labelSmall, color = SomulecoGreenText, fontWeight = FontWeight.Bold)
                        val p = marketplacePrice.toDoubleOrNull() ?: product.priceAmount
                        Text("List Price: $${String.format("%.2f", p)} | Somuleco Fee (5%): $${String.format("%.2f", p * 0.05)} | Net to You: $${String.format("%.2f", p * 0.95)}", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText)
                    }
                }

                Button(
                    onClick = {
                        val p = marketplacePrice.toDoubleOrNull() ?: product.priceAmount
                        CreatorRepository.updateMarketplaceListing(
                            MarketplaceListing(
                                productId = product.id,
                                productTitle = product.title,
                                isPublished = isMarketplaceListed,
                                listingPrice = p,
                                discoverableInSomulecoGlobal = isMarketplaceListed
                            )
                        )
                        showMarketplaceSheet = false
                        actionToast = "Marketplace publishing status updated!"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                ) {
                    Text("Update Marketplace Listing")
                }
            }
        }
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
