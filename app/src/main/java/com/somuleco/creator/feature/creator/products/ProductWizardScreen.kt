package com.somuleco.creator.feature.creator.products

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.ProductType
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun ProductWizardScreen(
    onNavigate: (Screen) -> Unit,
    onProductCreated: (String) -> Unit
) {
    val channels by CreatorRepository.channels.collectAsState()
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 6

    // Wizard Form State
    var title by remember { mutableStateOf("Autumn Moody Editorial Presets") }
    var shortDesc by remember { mutableStateOf("12 golden and overcast Lightroom profiles calibrated for rich skin tones.") }
    var fullDesc by remember { mutableStateOf("Complete color grading toolkit including 12 XMP & DNG presets, 3 grain overlays, and installation tutorial.") }
    var selectedChannelId by remember { mutableStateOf(channels.firstOrNull()?.id ?: "") }
    var selectedType by remember { mutableStateOf(ProductType.CREATIVE_ASSETS) }

    var fileName by remember { mutableStateOf("autumn_moody_presets_v1.zip") }
    var fileFormat by remember { mutableStateOf("ZIP (XMP, DNG, PDF)") }

    var priceText by remember { mutableStateOf("34") }

    // Digital Product Rights (DPR) settings
    var allowDownload by remember { mutableStateOf(true) }
    var allowCommercial by remember { mutableStateOf(true) }
    var allowRedistribution by remember { mutableStateOf(false) }
    var allowAiTraining by remember { mutableStateOf(false) }
    var licenseName by remember { mutableStateOf("Somuleco Commercial Pro License") }

    var publishToMarketplace by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("screen_product_wizard")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentStep > 1) currentStep-- else onNavigate(Screen.Store)
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Product Wizard: Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SomulecoBlue
            )

            TextButton(onClick = { onNavigate(Screen.Store) }) {
                Text("Cancel", color = TextMuted)
            }
        }

        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SomulecoBlue,
            trackColor = SomulecoBlueLight
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                1 -> {
                    // Step 1: Product Details
                    WizardStepHeader("Product Information", "Define what your customers and subscribers are buying.")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = shortDesc,
                        onValueChange = { shortDesc = it },
                        label = { Text("Short Summary (Card preview)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = fullDesc,
                        onValueChange = { fullDesc = it },
                        label = { Text("Full Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Product Type", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    ProductType.values().forEach { type ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedType = type },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedType == type) SomulecoBlueLight else SurfaceWhite
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(type.label, fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal)
                                RadioButton(selected = selectedType == type, onClick = { selectedType = type })
                            }
                        }
                    }
                }
                2 -> {
                    // Step 2: Digital Asset Files
                    WizardStepHeader("Digital Deliverables", "Specify the asset package delivered upon purchase.")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.FolderZip, contentDescription = null, tint = SomulecoPurple, modifier = Modifier.size(28.dp))
                                Column {
                                    Text(fileName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("Status: Validated & Fingerprinted for DPR", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText)
                                }
                            }
                            OutlinedTextField(
                                value = fileFormat,
                                onValueChange = { fileFormat = it },
                                label = { Text("File Format Details") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
                3 -> {
                    // Step 3: Pricing
                    WizardStepHeader("Set Product Price", "Enter the one-time purchase price in USD.")
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (USD)") },
                        leadingIcon = { Text("$", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("Recommendation: Creative presets priced between $25 - $45 convert 40% higher with active subscribers.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                4 -> {
                    // Step 4: Digital Product Rights (DPR)
                    WizardStepHeader("Digital Product Rights Configuration", "Plain-language copyright, license and usage terms registered into DPR.")
                    RightsSwitchRow("Allow Customer Downloads", "Buyers receive offline files to keep permanently.", allowDownload) { allowDownload = it }
                    RightsSwitchRow("Allow Commercial Use", "Buyers can use outputs in client and commercial projects.", allowCommercial) { allowCommercial = it }
                    RightsSwitchRow("Allow Redistribution", "Can buyers resell or publicly share your raw files? (Recommended: Off)", allowRedistribution) { allowRedistribution = it }
                    RightsSwitchRow("Allow AI Training", "Can third-party AI scrapers train on your digital assets? (Recommended: Off)", allowAiTraining) { allowAiTraining = it }
                }
                5 -> {
                    // Step 5: Marketplace Publishing
                    WizardStepHeader("Somuleco Marketplace Distribution", "Make your product discoverable across the broader Somuleco ecosystem.")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Publish to Somuleco Marketplace", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("Enables cross-platform discovery, search listing, and universal checkout.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Switch(checked = publishToMarketplace, onCheckedChange = { publishToMarketplace = it })
                            }
                        }
                    }
                }
                6 -> {
                    // Step 6: Review & Finalize
                    WizardStepHeader("Review & Publish Product", "Confirm details before registering rights and launching to your store.")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Price: $$priceText.00 USD", fontWeight = FontWeight.Bold, color = SomulecoBlueDark)
                            Text("Format: $fileFormat", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            HorizontalDivider(color = BorderSubtle)
                            Text("✓ DPR Rights: Registered with $licenseName", style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText, fontWeight = FontWeight.SemiBold)
                            Text("✓ Marketplace: ${if (publishToMarketplace) "Enabled" else "Store-Only"}", style = MaterialTheme.typography.bodySmall, color = SomulecoPurple, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Stepper Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (currentStep < totalSteps) {
                        currentStep++
                    } else {
                        val newProduct = CreatorRepository.createProduct(
                            title = title,
                            shortDescription = shortDesc,
                            fullDescription = fullDesc,
                            price = priceText.toDoubleOrNull() ?: 29.0,
                            channelId = selectedChannelId,
                            productType = selectedType,
                            fileFormat = fileFormat,
                            licenseName = licenseName,
                            allowDownload = allowDownload,
                            allowCommercial = allowCommercial
                        )
                        onProductCreated(newProduct.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("button_wizard_continue"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (currentStep == totalSteps) SomulecoGreenDark else SomulecoBlue)
            ) {
                Text(
                    text = if (currentStep == totalSteps) "Register Rights & Publish Product" else "Continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun WizardStepHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun RightsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
