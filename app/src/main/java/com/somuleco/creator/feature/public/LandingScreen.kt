package com.somuleco.creator.feature.public

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.somuleco.creator.ui.theme.*

@Composable
fun LandingScreen(
    onNavigate: (Screen) -> Unit,
    onExploreClick: () -> Unit,
    onLoginClick: () -> Unit,
    onStartCreatingClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(scrollState)
            .testTag("screen_landing")
    ) {
        // Top App Bar for Landing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(CreatorGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 18.sp)
                }
                Text(
                    text = "SOMULECO",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontSize = 16.sp,
                    color = SomulecoBlueDark
                )
                Text(
                    text = "CREATOR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SomulecoPurple
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onLoginClick) {
                    Text("Sign In", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Button(
                    onClick = onStartCreatingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Join", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroSurfaceGradient)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SomulecoPurpleLight,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(SomulecoPurpleBorder, SomulecoPinkBorder))
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🚀", fontSize = 14.sp)
                    Text(
                        text = "The Creator-Economy Operating Platform",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SomulecoPurpleDark
                    )
                }
            }

            Text(
                text = "Warm Intelligence for Creator Empowerment",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = TextPrimary,
                lineHeight = 36.sp
            )

            Text(
                text = "Transform your knowledge, creativity, skills, and influence into audiences, recurring subscriptions, protected digital products, and sustainable creator businesses.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartCreatingClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_start_creating"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoPurple)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Start Creating", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedButton(
                    onClick = onExploreClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_explore_creators"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderStrong, BorderStrong)))
                ) {
                    Text("Explore Creators", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        }

        // Core Creator Lifecycle Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "The Complete Creator Lifecycle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Everything you need to turn raw creative ideas into a full operating business, without stitching together 10 different tools.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            LifecycleCard(
                number = "01",
                title = "Create Channels & Publish",
                description = "Build specialized homes for your content types. Articles, masterclass videos, sample files, and announcements with tailored access rules.",
                badge = "Multi-Channel Architecture",
                iconEmoji = "📺"
            )

            LifecycleCard(
                number = "02",
                title = "Build Subscribers & Audience",
                description = "Offer free community tiers alongside paid memberships ($9-$39/mo). Direct subscriber relationships with zero algorithm middleman.",
                badge = "Recurring Revenue",
                iconEmoji = "⭐"
            )

            LifecycleCard(
                number = "03",
                title = "Digital Products & Digital Rights",
                description = "Package guides, presets, audio samples, and courses. Protected by Digital Product Rights (DPR) with verifiable licensing terms.",
                badge = "Protected IP",
                iconEmoji = "🛡️"
            )

            LifecycleCard(
                number = "04",
                title = "Creator AI Co-Intelligence",
                description = "Persistent AI that understands your channels, audience analytics, and product opportunities. Suggests your next best action and drafts content.",
                badge = "Empowerment AI",
                iconEmoji = "✨"
            )
        }

        // Featured Creators Preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Featured Creators on Somuleco",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            FeaturedCreatorCard(
                name = "Elena Rostova",
                handle = "@elenarostova",
                category = "Photography & Lighting",
                avatar = "📸",
                followers = "14.2K",
                subscribers = "920",
                description = "Natural light educator, portrait artist, and author of the Wedding & Portrait Business Guide.",
                onClick = { onNavigate(Screen.CreatorProfileDetail) }
            )

            FeaturedCreatorCard(
                name = "Marcus Vance",
                handle = "@marcusvance",
                category = "Modular Sound Design",
                avatar = "🎧",
                followers = "8.9K",
                subscribers = "440",
                description = "Electronic music producer releasing sound design sample vaults and synth masterclasses.",
                onClick = { onNavigate(Screen.CreatorProfileDetail) }
            )

            FeaturedCreatorCard(
                name = "Aria Chen",
                handle = "@ariachen",
                category = "AI & Creative Technology",
                avatar = "💻",
                followers = "19.5K",
                subscribers = "1.2K",
                description = "Teaching designers and coders how to master Generative Reality tools and creative AI pipelines.",
                onClick = { onNavigate(Screen.CreatorProfileDetail) }
            )
        }

        // Final Call to Action
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SomulecoBlueLight)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Ready to build your creator business?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Join thousands of creators using Somuleco Creator to publish, monetize, and protect their work.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Button(
                        onClick = onStartCreatingClick,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Started Free", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LifecycleCard(
    number: String,
    title: String,
    description: String,
    badge: String,
    iconEmoji: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = iconEmoji, fontSize = 22.sp)
                    Text(text = number, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = SomulecoPurple)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SomulecoBlueLight
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SomulecoBlueDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun FeaturedCreatorCard(
    name: String,
    handle: String,
    category: String,
    avatar: String,
    followers: String,
    subscribers: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CreatorGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SomulecoBlue, modifier = Modifier.size(16.dp))
                }
                Text("$handle • $category", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 2)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
