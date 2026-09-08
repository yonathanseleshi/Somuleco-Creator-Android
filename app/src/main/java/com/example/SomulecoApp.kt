package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.core.navigation.Screen
import com.example.data.model.AuthState
import com.example.data.repository.CreatorRepository
import com.example.feature.auth.*
import com.example.feature.consumer.*
import com.example.feature.creator.ai.CreatorAIScreen
import com.example.feature.creator.analytics.AnalyticsScreen
import com.example.feature.creator.audience.AudienceScreen
import com.example.feature.creator.channels.ChannelDetailScreen
import com.example.feature.creator.channels.ChannelsScreen
import com.example.feature.creator.content.ContentEditorScreen
import com.example.feature.creator.content.ContentManagementScreen
import com.example.feature.creator.dashboard.CreatorDashboardScreen
import com.example.feature.creator.integrations.IntegrationsScreen
import com.example.feature.creator.media.MediaLibraryScreen
import com.example.feature.creator.onboarding.CreatorActivationScreen
import com.example.feature.creator.onboarding.CreatorOnboardingScreen
import com.example.feature.creator.products.ProductWizardScreen
import com.example.feature.creator.revenue.RevenueScreen
import com.example.feature.creator.rights.RightsRegistryScreen
import com.example.feature.creator.settings.CreatorSettingsScreen
import com.example.feature.creator.store.ProductsScreen
import com.example.feature.creator.store.StoreScreen
import com.example.feature.creator.subscribers.SubscribersScreen
import com.example.feature.public.LandingScreen
import com.example.feature.shell.CreatorShell
import kotlinx.coroutines.launch

@Composable
fun SomulecoApp() {
    val coroutineScope = rememberCoroutineScope()
    val authState by CreatorRepository.authState.collectAsState()
    val isCreatorMode by CreatorRepository.isCreatorMode.collectAsState()
    val activeChannelId by CreatorRepository.selectedChannelId.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.CreatorDashboard) }
    var selectedContentId by remember { mutableStateOf("content-1") }
    var selectedProductId by remember { mutableStateOf("prod-1") }
    var selectedDetailChannelId by remember { mutableStateOf(activeChannelId ?: "ch_1") }

    // Full screen flows (without drawer shell)
    when (currentScreen) {
        Screen.Landing -> {
            LandingScreen(
                onNavigate = { currentScreen = it },
                onExploreClick = {
                    CreatorRepository.setCreatorMode(false)
                    currentScreen = Screen.Explore
                },
                onLoginClick = { currentScreen = Screen.Login },
                onStartCreatingClick = { currentScreen = Screen.CreatorActivation }
            )
        }
        Screen.Login -> {
            LoginScreen(
                onNavigate = { currentScreen = it },
                onLoginSuccess = {
                    CreatorRepository.setCreatorMode(true)
                    currentScreen = Screen.CreatorDashboard
                }
            )
        }
        Screen.Signup -> {
            SignupScreen(
                onNavigate = { currentScreen = it },
                onSignupSuccess = { currentScreen = Screen.VerifyEmail }
            )
        }
        Screen.VerifyEmail -> {
            VerifyEmailScreen(
                onNavigate = { currentScreen = it },
                onVerified = { currentScreen = Screen.CreatorActivation }
            )
        }
        Screen.ForgotPassword -> {
            ForgotPasswordScreen(onNavigate = { currentScreen = it })
        }
        Screen.CreatorActivation -> {
            CreatorActivationScreen(
                onNavigate = { currentScreen = it },
                onStartOnboarding = { currentScreen = Screen.CreatorOnboarding }
            )
        }
        Screen.CreatorOnboarding -> {
            CreatorOnboardingScreen(
                onNavigate = { currentScreen = it },
                onFinish = {
                    CreatorRepository.setCreatorMode(true)
                    currentScreen = Screen.CreatorDashboard
                }
            )
        }
        else -> {
            // Main workspace shell with Navigation Drawer, Top Bar, and FAB
            CreatorShell(
                currentScreen = currentScreen,
                onNavigate = { currentScreen = it },
                onSignOut = {
                    coroutineScope.launch {
                        CreatorRepository.logout()
                    }
                    currentScreen = Screen.Landing
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentScreen) {
                        Screen.CreatorDashboard -> {
                            CreatorDashboardScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Channels -> {
                            ChannelsScreen(
                                onNavigate = { currentScreen = it },
                                onOpenChannelDetail = { channelId ->
                                    CreatorRepository.selectChannel(channelId)
                                    selectedDetailChannelId = channelId
                                    currentScreen = Screen.ChannelDetail
                                }
                            )
                        }
                        Screen.ChannelDetail -> {
                            ChannelDetailScreen(
                                channelId = selectedDetailChannelId,
                                onNavigate = { currentScreen = it },
                                onBackClick = { currentScreen = Screen.Channels }
                            )
                        }
                        Screen.Content -> {
                            ContentManagementScreen(
                                onNavigate = { currentScreen = it },
                                onOpenContentDetail = { contentId ->
                                    selectedContentId = contentId
                                    currentScreen = Screen.ContentDetail
                                }
                            )
                        }
                        Screen.ContentEditor -> {
                            ContentEditorScreen(
                                onNavigate = { currentScreen = it },
                                onContentPublished = { currentScreen = Screen.Content }
                            )
                        }
                        Screen.Audience -> {
                            AudienceScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Subscribers -> {
                            SubscribersScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Store -> {
                            StoreScreen(
                                onNavigate = { currentScreen = it },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Products -> {
                            ProductsScreen(
                                onNavigate = { currentScreen = it },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.ProductWizard -> {
                            ProductWizardScreen(
                                onNavigate = { currentScreen = it },
                                onProductCreated = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Media -> {
                            MediaLibraryScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Analytics -> {
                            AnalyticsScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Revenue -> {
                            RevenueScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.CreatorAI -> {
                            CreatorAIScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Rights -> {
                            RightsRegistryScreen(
                                onNavigate = { currentScreen = it },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Integrations -> {
                            IntegrationsScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.CreatorSettings -> {
                            CreatorSettingsScreen(onNavigate = { currentScreen = it })
                        }
                        // Consumer views
                        Screen.Home -> {
                            ConsumerHomeScreen(
                                onNavigate = { currentScreen = it },
                                onOpenContentDetail = { contentId ->
                                    selectedContentId = contentId
                                    currentScreen = Screen.ContentDetail
                                },
                                onOpenCreatorProfile = { currentScreen = Screen.CreatorProfileDetail }
                            )
                        }
                        Screen.Following -> {
                            FollowingScreen(
                                onNavigate = { currentScreen = it },
                                onOpenChannelDetail = { channelId ->
                                    selectedDetailChannelId = channelId
                                    currentScreen = Screen.ChannelDetail
                                }
                            )
                        }
                        Screen.Explore -> {
                            ExploreScreen(
                                onNavigate = { currentScreen = it },
                                onOpenCreatorProfile = { currentScreen = Screen.CreatorProfileDetail },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Search -> {
                            SearchScreen(
                                onNavigate = { currentScreen = it },
                                onOpenContentDetail = { contentId ->
                                    selectedContentId = contentId
                                    currentScreen = Screen.ContentDetail
                                },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                },
                                onOpenCreatorProfile = { currentScreen = Screen.CreatorProfileDetail }
                            )
                        }
                        Screen.CreatorProfileDetail -> {
                            CreatorProfileScreen(
                                onNavigate = { currentScreen = it },
                                onOpenContentDetail = { contentId ->
                                    selectedContentId = contentId
                                    currentScreen = Screen.ContentDetail
                                },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                },
                                onOpenSubscriptionPlans = { currentScreen = Screen.SubscriptionPlansDetail }
                            )
                        }
                        Screen.ContentDetail -> {
                            ContentDetailScreen(
                                contentId = selectedContentId,
                                onNavigate = { currentScreen = it },
                                onOpenSubscriptionPlans = { currentScreen = Screen.SubscriptionPlansDetail }
                            )
                        }
                        Screen.ProductDetail -> {
                            ProductDetailScreen(
                                productId = selectedProductId,
                                onNavigate = { currentScreen = it },
                                onViewInLibrary = { currentScreen = Screen.Library }
                            )
                        }
                        Screen.SubscriptionPlansDetail -> {
                            SubscriptionPlansScreen(onNavigate = { currentScreen = it })
                        }
                        Screen.Library -> {
                            LibraryScreen(
                                onNavigate = { currentScreen = it },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Purchases -> {
                            PurchasesScreen(
                                onNavigate = { currentScreen = it },
                                onOpenProductDetail = { prodId ->
                                    selectedProductId = prodId
                                    currentScreen = Screen.ProductDetail
                                }
                            )
                        }
                        Screen.Subscriptions -> {
                            SubscriptionsScreen(
                                onNavigate = { currentScreen = it }
                            )
                        }
                        Screen.Saved -> {
                            SavedScreen(
                                onNavigate = { currentScreen = it },
                                onOpenContentDetail = { contentId ->
                                    selectedContentId = contentId
                                    currentScreen = Screen.ContentDetail
                                }
                            )
                        }
                        Screen.Notifications -> {
                            NotificationsScreen(onNavigate = { currentScreen = it })
                        }
                        else -> {
                            CreatorDashboardScreen(onNavigate = { currentScreen = it })
                        }
                    }
                }
            }
        }
    }
}
