package com.somuleco.creator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.somuleco.creator.core.navigation.NavGraphs
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.core.navigation.navigateFromShell
import com.somuleco.creator.core.navigation.navigateTopLevel
import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.feature.auth.*
import com.somuleco.creator.feature.consumer.*
import com.somuleco.creator.feature.creator.ai.CreatorAIScreen
import com.somuleco.creator.feature.creator.analytics.AnalyticsScreen
import com.somuleco.creator.feature.creator.audience.AudienceScreen
import com.somuleco.creator.feature.creator.channels.ChannelDetailScreen
import com.somuleco.creator.feature.creator.channels.ChannelsScreen
import com.somuleco.creator.feature.creator.content.ContentEditorScreen
import com.somuleco.creator.feature.creator.content.ContentManagementScreen
import com.somuleco.creator.feature.creator.dashboard.CreatorDashboardScreen
import com.somuleco.creator.feature.creator.integrations.IntegrationsScreen
import com.somuleco.creator.feature.creator.media.MediaLibraryScreen
import com.somuleco.creator.feature.creator.onboarding.CreatorActivationScreen
import com.somuleco.creator.feature.creator.onboarding.CreatorOnboardingScreen
import com.somuleco.creator.feature.creator.products.ProductWizardScreen
import com.somuleco.creator.feature.creator.revenue.RevenueScreen
import com.somuleco.creator.feature.creator.rights.RightsRegistryScreen
import com.somuleco.creator.feature.creator.settings.CreatorSettingsScreen
import com.somuleco.creator.feature.creator.store.ProductsScreen
import com.somuleco.creator.feature.creator.store.StoreScreen
import com.somuleco.creator.feature.creator.subscribers.SubscribersScreen
import com.somuleco.creator.feature.public.LandingScreen
import com.somuleco.creator.feature.shell.AccessDeniedScreen
import com.somuleco.creator.feature.shell.CreatorShell
import kotlinx.coroutines.launch

/**
 * Root composable. Hosts a single [NavHost] with three nested graphs — auth, consumer,
 * creator — replacing the previous `when`/`mutableStateOf<Screen>` navigation mechanism.
 * See `GRD/Contracts/v0.1-navigation-semantics.md` for the semantics this implements.
 *
 * [windowSizeClass] drives adaptive shell chrome: a dismissible modal drawer at
 * compact/medium width, a permanent drawer at expanded width (tablets).
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SomulecoApp(
    windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(
        DpSize(360.dp, 800.dp)
    )
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val authState by CreatorRepository.authState.collectAsState()
    val useExpandedLayout = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    // Start destination per v0.1-navigation-semantics.md §3.2 / §5: unauthenticated lands in
    // the auth graph; authenticated + Creator-activated lands in the creator graph;
    // authenticated but not Creator-activated lands in the consumer graph. Read once — later
    // auth transitions (login/logout) are handled reactively below via LaunchedEffect, not by
    // mutating this start destination.
    val startGraph = remember {
        when (val state = CreatorRepository.authState.value) {
            is AuthState.Authenticated -> if (state.isCreator) NavGraphs.CREATOR else NavGraphs.CONSUMER
            else -> NavGraphs.AUTH
        }
    }

    // React to auth-state transitions that happen *after* first composition (login, logout,
    // signup) by moving the back stack to the graph that state now implies, clearing whatever
    // came before it so Back can't return into a since-invalidated session.
    var lastHandledAuthState by remember { mutableStateOf<AuthState?>(null) }
    LaunchedEffect(authState) {
        val previous = lastHandledAuthState
        lastHandledAuthState = authState
        if (previous == null) return@LaunchedEffect // first composition already handled by startGraph
        when (val state = authState) {
            is AuthState.Authenticated -> {
                val target = if (state.isCreator) Screen.CreatorDashboard.route else Screen.Home.route
                navController.navigate(target) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
            AuthState.Unauthenticated, AuthState.Unknown -> {
                navController.navigate(Screen.Landing.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }

    // Mode-switch landing rule (v0.1-navigation-semantics.md §3.3): switching to consumer mode
    // always lands on Home; switching to creator mode always lands on Creator Dashboard. Skips
    // the first composition (already handled by startGraph) so toggling mode from the drawer/top
    // bar/onboarding actually re-routes instead of leaving the user stranded on a screen the new
    // mode's drawer no longer highlights.
    val isCreatorMode by CreatorRepository.isCreatorMode.collectAsState()
    var lastHandledMode by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(isCreatorMode) {
        val previous = lastHandledMode
        lastHandledMode = isCreatorMode
        if (previous == null || previous == isCreatorMode) return@LaunchedEffect
        val target = if (isCreatorMode) Screen.CreatorDashboard.route else Screen.Home.route
        navController.navigate(target) {
            popUpTo(navController.graph.id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = startGraph) {
        authGraph(navController)
        consumerGraph(navController, coroutineScope, useExpandedLayout)
        creatorGraph(navController, coroutineScope, useExpandedLayout)
    }
}

// ---------------------------------------------------------------------------------------------
// Auth graph — public/onboarding flows, no shell chrome (v0.1-navigation-semantics.md §5).
// ---------------------------------------------------------------------------------------------
private fun androidx.navigation.NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Landing.route, route = NavGraphs.AUTH) {
        composable(Screen.Landing.route) {
            LandingScreen(
                onNavigate = { navController.navigate(it.route) },
                onExploreClick = {
                    CreatorRepository.setCreatorMode(false)
                    navController.navigate(Screen.Explore.route) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onStartCreatingClick = { navController.navigate(Screen.CreatorActivation.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigate = { navController.navigate(it.route) },
                onLoginSuccess = {
                    CreatorRepository.setCreatorMode(true)
                    navController.navigate(Screen.CreatorDashboard.route) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigate = { navController.navigate(it.route) },
                onSignupSuccess = { navController.navigate(Screen.VerifyEmail.route) }
            )
        }
        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(
                onNavigate = { navController.navigate(it.route) },
                onVerified = { navController.navigate(Screen.CreatorActivation.route) }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onNavigate = { navController.navigate(it.route) })
        }
        composable(Screen.CreatorActivation.route) {
            CreatorActivationScreen(
                onNavigate = { navController.navigate(it.route) },
                onStartOnboarding = { navController.navigate(Screen.CreatorOnboarding.route) }
            )
        }
        composable(Screen.CreatorOnboarding.route) {
            CreatorOnboardingScreen(
                onNavigate = { navController.navigate(it.route) },
                onFinish = {
                    CreatorRepository.setCreatorMode(true)
                    navController.navigate(Screen.CreatorDashboard.route) {
                        popUpTo(NavGraphs.AUTH) { inclusive = true }
                    }
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Consumer graph — shell-wrapped consumer destinations + shared detail screens.
// ---------------------------------------------------------------------------------------------
private fun androidx.navigation.NavGraphBuilder.consumerGraph(
    navController: NavHostController,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    useExpandedLayout: Boolean
) {
    navigation(startDestination = Screen.Home.route, route = NavGraphs.CONSUMER) {
        composable(Screen.Home.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ConsumerHomeScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenContentDetail = { contentId ->
                            navController.navigate(Screen.ContentDetail.routeFor(contentId))
                        },
                        onOpenCreatorProfile = { navController.navigate(Screen.CreatorProfileDetail.route) }
                    )
                }
            }
        }
        composable(Screen.Explore.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ExploreScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenCreatorProfile = { navController.navigate(Screen.CreatorProfileDetail.route) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.Following.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    FollowingScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenChannelDetail = { channelId ->
                            CreatorRepository.selectChannel(channelId)
                            navController.navigate(Screen.ChannelDetail.routeFor(channelId))
                        }
                    )
                }
            }
        }
        composable(Screen.Subscriptions.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SubscriptionsScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Library.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    LibraryScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.Purchases.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    PurchasesScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.Saved.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SavedScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenContentDetail = { contentId -> navController.navigate(Screen.ContentDetail.routeFor(contentId)) }
                    )
                }
            }
        }
        composable(Screen.Notifications.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    NotificationsScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Search.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SearchScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenContentDetail = { contentId -> navController.navigate(Screen.ContentDetail.routeFor(contentId)) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) },
                        onOpenCreatorProfile = { navController.navigate(Screen.CreatorProfileDetail.route) }
                    )
                }
            }
        }
        composable(Screen.CreatorProfileDetail.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    CreatorProfileScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenContentDetail = { contentId -> navController.navigate(Screen.ContentDetail.routeFor(contentId)) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) },
                        onOpenSubscriptionPlans = { navController.navigate(Screen.SubscriptionPlansDetail.route) }
                    )
                }
            }
        }
        composable(
            route = Screen.ContentDetail.ROUTE_PATTERN,
            arguments = listOf(navArgument(Screen.ContentDetail.ARG_ID) { type = NavType.StringType; defaultValue = Screen.ContentDetail.DEFAULT_ID })
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString(Screen.ContentDetail.ARG_ID) ?: Screen.ContentDetail.DEFAULT_ID
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ContentDetailScreen(
                        contentId = contentId,
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenSubscriptionPlans = { navController.navigate(Screen.SubscriptionPlansDetail.route) }
                    )
                }
            }
        }
        composable(
            route = Screen.ProductDetail.ROUTE_PATTERN,
            arguments = listOf(navArgument(Screen.ProductDetail.ARG_ID) { type = NavType.StringType; defaultValue = Screen.ProductDetail.DEFAULT_ID })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Screen.ProductDetail.ARG_ID) ?: Screen.ProductDetail.DEFAULT_ID
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ProductDetailScreen(
                        productId = productId,
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onViewInLibrary = { navController.navigateTopLevel(Screen.Library.route) }
                    )
                }
            }
        }
        composable(Screen.SubscriptionPlansDetail.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SubscriptionPlansScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Creator graph — shell-wrapped Creator workspace destinations + creation flows.
// ---------------------------------------------------------------------------------------------
private fun androidx.navigation.NavGraphBuilder.creatorGraph(
    navController: NavHostController,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    useExpandedLayout: Boolean
) {
    navigation(startDestination = Screen.CreatorDashboard.route, route = NavGraphs.CREATOR) {
        composable(Screen.CreatorDashboard.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    CreatorDashboardScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Channels.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ChannelsScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenChannelDetail = { channelId ->
                            CreatorRepository.selectChannel(channelId)
                            navController.navigate(Screen.ChannelDetail.routeFor(channelId))
                        }
                    )
                }
            }
        }
        composable(
            route = Screen.ChannelDetail.ROUTE_PATTERN,
            arguments = listOf(navArgument(Screen.ChannelDetail.ARG_ID) { type = NavType.StringType; defaultValue = Screen.ChannelDetail.DEFAULT_ID })
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString(Screen.ChannelDetail.ARG_ID) ?: Screen.ChannelDetail.DEFAULT_ID
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ChannelDetailScreen(
                        channelId = channelId,
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        // Real back-stack pop (v0.1-navigation-semantics.md §8) — this used to be
                        // a hardcoded jump to Screen.Channels regardless of how the screen was reached.
                        onBackClick = { navController.popBackStack() },
                        onOpenContentDetail = { contentId -> navController.navigate(Screen.ContentDetail.routeFor(contentId)) }
                    )
                }
            }
        }
        composable(Screen.Content.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ContentManagementScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenContentDetail = { contentId -> navController.navigate(Screen.ContentDetail.routeFor(contentId)) }
                    )
                }
            }
        }
        composable(Screen.ContentEditor.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ContentEditorScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onContentPublished = { navController.navigateTopLevel(Screen.Content.route) }
                    )
                }
            }
        }
        composable(Screen.Audience.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    AudienceScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Subscribers.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    SubscribersScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Store.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    StoreScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.Products.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ProductsScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.ProductWizard.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ProductWizardScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onProductCreated = { prodId ->
                            navController.navigate(Screen.ProductDetail.routeFor(prodId)) {
                                popUpTo(Screen.ProductWizard.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
        composable(Screen.Media.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    MediaLibraryScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Analytics.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    AnalyticsScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Revenue.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    RevenueScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.CreatorAI.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    CreatorAIScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.Rights.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    RightsRegistryScreen(
                        onNavigate = { navController.navigateFromShellSafe(it) },
                        onOpenProductDetail = { prodId -> navController.navigate(Screen.ProductDetail.routeFor(prodId)) }
                    )
                }
            }
        }
        composable(Screen.Integrations.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    IntegrationsScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.CreatorSettings.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    CreatorSettingsScreen(onNavigate = { navController.navigateFromShellSafe(it) })
                }
            }
        }
        composable(Screen.AccessDenied.route) {
            ShellScreen(navController, useExpandedLayout, coroutineScope) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    AccessDeniedScreen(
                        onGoToCreatorActivation = {
                            navController.navigate(Screen.CreatorActivation.route)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Shell chrome wrapper shared by every consumer/creator destination. Reads the current
 * back-stack entry to resolve [Screen] for the shell's title/drawer-highlight, and routes the
 * shell's onNavigate/onSignOut callbacks through the route-boundary-aware navigation helpers.
 */
@Composable
private fun ShellScreen(
    navController: NavHostController,
    useExpandedLayout: Boolean,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.forRoute(backStackEntry?.destination?.route)

    CreatorShell(
        currentScreen = currentScreen,
        onNavigate = { screen -> navController.navigateFromShellSafe(screen) },
        onSignOut = {
            coroutineScope.launch { CreatorRepository.logout() }
            navController.navigate(Screen.Landing.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        },
        useExpandedLayout = useExpandedLayout,
        content = content
    )
}

/**
 * Route-boundary-aware wrapper around [navigateFromShell], reading current auth/Creator state
 * from [CreatorRepository] at the moment of the navigation attempt (v0.1-navigation-semantics.md
 * §5). This is what an unauthenticated or non-Creator-activated user hits when attempting a
 * protected destination via the drawer, top bar, or Create FAB.
 */
private fun NavController.navigateFromShellSafe(screen: Screen) {
    val state = CreatorRepository.authState.value
    val isAuthenticated = state is AuthState.Authenticated
    val isCreator = (state as? AuthState.Authenticated)?.isCreator ?: false
    navigateFromShell(screen, isAuthenticated = isAuthenticated, isCreator = isCreator)
}
