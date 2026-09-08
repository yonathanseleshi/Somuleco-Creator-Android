package com.somuleco.creator.feature.creator.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*

@Composable
fun CreatorActivationScreen(
    onNavigate: (Screen) -> Unit,
    onStartOnboarding: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CreatorGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("🚀", fontSize = 36.sp)
        }

        Text(
            text = "Become a Creator on Somuleco",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your existing Somuleco identity remains unified. Activating your Creator account unlocks dedicated publishing channels, audience subscriptions, digital products, and Creator AI.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActivationFeatureRow("📺", "Create Focused Channels", "Separate your content themes, courses, and BTS updates into distinct subscriber spaces.")
        ActivationFeatureRow("💰", "Monetize Subscriptions & Products", "Sell digital guides, presets, and memberships with automatic payment processing.")
        ActivationFeatureRow("🛡️", "Digital Product Rights (DPR)", "Protect your creative work with verifiable cryptographic rights and licensing terms.")
        ActivationFeatureRow("✨", "Creator AI Co-Intelligence", "Personalized recommendations for what to create next based on audience trends.")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartOnboarding,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("button_start_activation_onboarding"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
        ) {
            Text("Start Creator Onboarding", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun CreatorOnboardingScreen(
    onNavigate: (Screen) -> Unit,
    onFinish: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 9

    // State collected across steps
    var selectedGoal by remember { mutableStateOf("Build audience & earn recurring subscriptions") }
    var selectedType by remember { mutableStateOf("Visual Arts & Photography") }
    var selectedCategory by remember { mutableStateOf("Photography & Lighting") }
    var selectedAudience by remember { mutableStateOf("Intermediate Enthusiasts & Aspiring Pros") }
    var channelName by remember { mutableStateOf("Photography Masterclass") }
    var channelDesc by remember { mutableStateOf("Natural light portraits, color science, and client workflow systems.") }
    var displayName by remember { mutableStateOf("Elena Rostova") }
    var handle by remember { mutableStateOf("@elenarostova") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Step progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                IconButton(onClick = { currentStep-- }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = SomulecoPurple
            )

            TextButton(onClick = onFinish) {
                Text("Skip", color = TextMuted)
            }
        }

        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SomulecoPurple,
            trackColor = SomulecoPurpleLight
        )

        // Step Content Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                1 -> {
                    StepHeading("What is your primary creator goal?", "Select what you are hoping to build on Somuleco Creator.")
                    listOf(
                        "Build an audience & gain followers",
                        "Build a recurring paid subscriber membership",
                        "Sell digital products & creative assets",
                        "Publish masterclasses & structured education",
                        "Build a full sustainable creator business"
                    ).forEach { goal ->
                        SelectableCard(goal, isSelected = selectedGoal == goal) { selectedGoal = goal }
                    }
                }
                2 -> {
                    StepHeading("What kind of creator are you?", "Choose your primary creative or knowledge discipline.")
                    listOf(
                        "Visual Arts & Photography",
                        "Music & Audio Production",
                        "AI & Creative Technology",
                        "Education & Tutoring",
                        "Writing & Journalism",
                        "Design & Creative Strategy"
                    ).forEach { type ->
                        SelectableCard(type, isSelected = selectedType == type) { selectedType = type }
                    }
                }
                3 -> {
                    StepHeading("Select your topics & categories", "Help audiences and Creator AI understand your subject focus.")
                    listOf(
                        "Photography & Lighting",
                        "Color Grading & Post-Processing",
                        "Studio Gear & Equipment",
                        "Portrait & Editorial",
                        "Commercial Creative Business"
                    ).forEach { cat ->
                        SelectableCard(cat, isSelected = selectedCategory == cat) { selectedCategory = cat }
                    }
                }
                4 -> {
                    StepHeading("Who is your target audience?", "Understanding your audience helps tailor content and subscription pricing.")
                    listOf(
                        "Complete Beginners picking up their first camera",
                        "Intermediate Enthusiasts & Aspiring Pros",
                        "Working Commercial & Wedding Professionals",
                        "Broad General Public & Photography Lovers"
                    ).forEach { aud ->
                        SelectableCard(aud, isSelected = selectedAudience == aud) { selectedAudience = aud }
                    }
                }
                5 -> {
                    StepHeading("How do you plan to monetize?", "You can activate multiple revenue streams at any time.")
                    SelectableCard("Paid Monthly Memberships ($9 - $39/mo)", isSelected = true) {}
                    SelectableCard("Digital Product Store (Guides, Presets, Assets)", isSelected = true) {}
                    SelectableCard("Digital Product Rights Licensing (DPR)", isSelected = true) {}
                    SelectableCard("Live Masterclass Tickets", isSelected = false) {}
                }
                6 -> {
                    StepHeading("Set up your Creator Profile", "This is how you appear publicly across Somuleco.")
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = handle,
                        onValueChange = { handle = it },
                        label = { Text("Creator Handle") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
                7 -> {
                    StepHeading("Create your First Channel", "Channels give your content, audience, and subscriptions a focused home.")
                    OutlinedTextField(
                        value = channelName,
                        onValueChange = { channelName = it },
                        label = { Text("Channel Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = channelDesc,
                        onValueChange = { channelDesc = it },
                        label = { Text("Channel Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
                8 -> {
                    StepHeading("Meet Creator AI", "Your persistent co-intelligence assistant.")
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SomulecoPurpleLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("✨", fontSize = 24.sp)
                                Text("Personalized AI Recommendation", fontWeight = FontWeight.Bold, color = SomulecoPurpleDark)
                            }
                            Text(
                                text = "Based on your focus in '$selectedCategory' targeting '$selectedAudience', Creator AI recommends launching your first channel with 3 foundational posts followed by a high-value Digital Guide.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
                9 -> {
                    StepHeading("You're all set!", "Your Creator operating workspace is ready.")
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("✓ Creator Profile: $displayName ($handle)", fontWeight = FontWeight.SemiBold, color = SomulecoGreenText)
                            Text("✓ First Channel: $channelName", fontWeight = FontWeight.SemiBold, color = SomulecoGreenText)
                            Text("✓ Digital Product Rights: Connected", fontWeight = FontWeight.SemiBold, color = SomulecoGreenText)
                            Text("✓ Creator AI: Initialized & context ready", fontWeight = FontWeight.SemiBold, color = SomulecoGreenText)
                        }
                    }
                }
            }
        }

        // Next / Continue button
        Button(
            onClick = {
                if (currentStep < totalSteps) {
                    currentStep++
                } else {
                    CreatorRepository.setCreatorMode(true)
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("button_onboarding_continue"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (currentStep == totalSteps) SomulecoGreenDark else SomulecoPurple)
        ) {
            Text(
                text = if (currentStep == totalSteps) "Launch Creator Dashboard" else "Continue",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun StepHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun SelectableCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) SomulecoPurpleLight else SurfaceWhite,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(if (isSelected) SomulecoPurple else BorderSubtle, if (isSelected) SomulecoPurple else BorderSubtle)
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SomulecoPurpleDark else TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SomulecoPurple)
            }
        }
    }
}

@Composable
private fun ActivationFeatureRow(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SomulecoPurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
