package com.somuleco.creator.feature.shell

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.somuleco.creator.core.navigation.Screen
import com.somuleco.creator.data.model.Channel
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorShell(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onSignOut: () -> Unit,
    useExpandedLayout: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isCreatorMode by CreatorRepository.isCreatorMode.collectAsState()
    val channels by CreatorRepository.channels.collectAsState()
    val selectedChannelId by CreatorRepository.selectedChannelId.collectAsState()
    val notifications by CreatorRepository.notifications.collectAsState()
    val unreadNotificationsCount = notifications.count { !it.isRead }
    val user by CreatorRepository.currentUser.collectAsState()
    val profile by CreatorRepository.creatorProfile.collectAsState()

    var showCreateBottomSheet by remember { mutableStateOf(false) }
    var showChannelSelectorDialog by remember { mutableStateOf(false) }
    var showEcosystemDialog by remember { mutableStateOf<String?>(null) }

    val selectedChannel = channels.find { it.id == selectedChannelId }

    // Back closes an open drawer before letting Back pop the underlying nav back stack
    // (v0.1-navigation-semantics.md §8).
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // Adaptive navigation (v0.1-navigation-semantics.md §8 / FND-Wave-04 §7.3.8): the same
    // drawer content renders inside a dismissible ModalNavigationDrawer at compact/medium
    // window widths, and inside an always-visible PermanentNavigationDrawer at expanded
    // widths (tablets), so it never has to be maintained twice.
    val drawerBody: @Composable ColumnScope.() -> Unit = {
        DrawerHeader(
                    displayName = profile.displayName,
                    handle = profile.handle,
                    isCreatorMode = isCreatorMode,
                    selectedChannel = selectedChannel,
                    onToggleMode = {
                        CreatorRepository.toggleCreatorMode()
                        scope.launch { drawerState.close() }
                    },
                    onOpenChannelSelector = {
                        showChannelSelectorDialog = true
                    }
                )

                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (isCreatorMode) {
                        DrawerSectionTitle("CREATOR WORKSPACE")

                        DrawerItem(
                            label = "Creator Dashboard",
                            icon = Icons.Outlined.Dashboard,
                            isSelected = currentScreen == Screen.CreatorDashboard,
                            accentColor = SomulecoBlue,
                            onClick = {
                                onNavigate(Screen.CreatorDashboard)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Channels",
                            icon = Icons.Outlined.Tv,
                            isSelected = currentScreen == Screen.Channels,
                            badge = "${channels.size}",
                            onClick = {
                                onNavigate(Screen.Channels)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Content",
                            icon = Icons.Outlined.Article,
                            isSelected = currentScreen == Screen.Content,
                            onClick = {
                                onNavigate(Screen.Content)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Audience",
                            icon = Icons.Outlined.People,
                            isSelected = currentScreen == Screen.Audience,
                            onClick = {
                                onNavigate(Screen.Audience)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Subscribers",
                            icon = Icons.Outlined.Star,
                            isSelected = currentScreen == Screen.Subscribers,
                            badge = "920",
                            onClick = {
                                onNavigate(Screen.Subscribers)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Store",
                            icon = Icons.Outlined.Storefront,
                            isSelected = currentScreen == Screen.Store,
                            onClick = {
                                onNavigate(Screen.Store)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Products",
                            icon = Icons.Outlined.ShoppingBag,
                            isSelected = currentScreen == Screen.Products,
                            onClick = {
                                onNavigate(Screen.Products)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Media Library",
                            icon = Icons.Outlined.PermMedia,
                            isSelected = currentScreen == Screen.Media,
                            onClick = {
                                onNavigate(Screen.Media)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Analytics",
                            icon = Icons.Outlined.BarChart,
                            isSelected = currentScreen == Screen.Analytics,
                            onClick = {
                                onNavigate(Screen.Analytics)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Revenue",
                            icon = Icons.Outlined.MonetizationOn,
                            isSelected = currentScreen == Screen.Revenue,
                            accentColor = SomulecoGreenText,
                            badge = "$18.4K",
                            onClick = {
                                onNavigate(Screen.Revenue)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Creator AI",
                            icon = Icons.Outlined.AutoAwesome,
                            isSelected = currentScreen == Screen.CreatorAI,
                            accentColor = SomulecoPurple,
                            badge = "AI Active",
                            onClick = {
                                onNavigate(Screen.CreatorAI)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Digital Product Rights",
                            icon = Icons.Outlined.Shield,
                            isSelected = currentScreen == Screen.Rights,
                            accentColor = SomulecoBlue,
                            onClick = {
                                onNavigate(Screen.Rights)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Integrations",
                            icon = Icons.Outlined.Extension,
                            isSelected = currentScreen == Screen.Integrations,
                            onClick = {
                                onNavigate(Screen.Integrations)
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerItem(
                            label = "Creator Settings",
                            icon = Icons.Outlined.Settings,
                            isSelected = currentScreen == Screen.CreatorSettings,
                            onClick = {
                                onNavigate(Screen.CreatorSettings)
                                scope.launch { drawerState.close() }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                    }

                    DrawerSectionTitle("CONSUMER DISCOVERY")

                    DrawerItem(
                        label = "Home Feed",
                        icon = Icons.Outlined.Home,
                        isSelected = currentScreen == Screen.Home,
                        onClick = {
                            onNavigate(Screen.Home)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Explore Creators",
                        icon = Icons.Outlined.Explore,
                        isSelected = currentScreen == Screen.Explore,
                        onClick = {
                            onNavigate(Screen.Explore)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Following",
                        icon = Icons.Outlined.FavoriteBorder,
                        isSelected = currentScreen == Screen.Following,
                        onClick = {
                            onNavigate(Screen.Following)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "My Subscriptions",
                        icon = Icons.Outlined.Subscriptions,
                        isSelected = currentScreen == Screen.Subscriptions,
                        onClick = {
                            onNavigate(Screen.Subscriptions)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Library",
                        icon = Icons.Outlined.CollectionsBookmark,
                        isSelected = currentScreen == Screen.Library,
                        onClick = {
                            onNavigate(Screen.Library)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Purchases",
                        icon = Icons.Outlined.Receipt,
                        isSelected = currentScreen == Screen.Purchases,
                        onClick = {
                            onNavigate(Screen.Purchases)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Saved",
                        icon = Icons.Outlined.BookmarkBorder,
                        isSelected = currentScreen == Screen.Saved,
                        onClick = {
                            onNavigate(Screen.Saved)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Notifications",
                        icon = Icons.Outlined.Notifications,
                        isSelected = currentScreen == Screen.Notifications,
                        badge = if (unreadNotificationsCount > 0) "$unreadNotificationsCount" else null,
                        onClick = {
                            onNavigate(Screen.Notifications)
                            scope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

                    DrawerSectionTitle("ECOSYSTEM & IDENTITY")

                    DrawerItem(
                        label = "My Public Creator Profile",
                        icon = Icons.Outlined.AccountCircle,
                        isSelected = currentScreen == Screen.CreatorProfileDetail,
                        onClick = {
                            onNavigate(Screen.CreatorProfileDetail)
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Main Somuleco",
                        icon = Icons.Outlined.Language,
                        isSelected = false,
                        onClick = {
                            showEcosystemDialog = "Somuleco Ecosystem"
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Passport Reputation",
                        icon = Icons.Outlined.VerifiedUser,
                        isSelected = false,
                        onClick = {
                            showEcosystemDialog = "Passport Reputation"
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        label = "Sign Out",
                        icon = Icons.Outlined.Logout,
                        isSelected = false,
                        accentColor = Color(0xFFEF4444),
                        onClick = {
                            scope.launch { drawerState.close() }
                            onSignOut()
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
    }

    val shellContent: @Composable () -> Unit = {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentScreen.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isCreatorMode) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { showChannelSelectorDialog = true },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedChannel?.name ?: "All Channels",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SomulecoBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = SomulecoBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        if (!useExpandedLayout) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("button_open_drawer")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Navigation Menu",
                                    tint = TextPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onNavigate(Screen.Search) },
                            modifier = Modifier.testTag("button_search")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = { onNavigate(Screen.Notifications) },
                            modifier = Modifier.testTag("button_notifications")
                        ) {
                            BadgedBox(badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = SomulecoPink) {
                                        Text("$unreadNotificationsCount")
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = TextPrimary
                                )
                            }
                        }

                        // Mode toggle chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCreatorMode) SomulecoPurpleLight else SomulecoBlueLight,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { CreatorRepository.toggleCreatorMode() }
                                .testTag("button_toggle_mode")
                        ) {
                            Text(
                                text = if (isCreatorMode) "Creator" else "Consumer",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCreatorMode) SomulecoPurpleDark else SomulecoBlueDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceWhite,
                        titleContentColor = TextPrimary
                    )
                )
            },
            floatingActionButton = {
                if (isCreatorMode) {
                    FloatingActionButton(
                        onClick = { showCreateBottomSheet = true },
                        containerColor = SomulecoPurple,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .testTag("fab_create")
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Create")
                            Text("Create", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }

    if (useExpandedLayout) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .testTag("drawer_sheet"),
                    drawerContainerColor = SurfaceWhite,
                    drawerTonalElevation = 2.dp,
                    content = drawerBody
                )
            }
        ) {
            shellContent()
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .testTag("drawer_sheet"),
                    drawerContainerColor = SurfaceWhite,
                    drawerTonalElevation = 2.dp,
                    content = drawerBody
                )
            }
        ) {
            shellContent()
        }
    }

    // Create Modal Bottom Sheet
    if (showCreateBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateBottomSheet = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "What would you like to create?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                CreateActionItem(
                    icon = Icons.Outlined.Edit,
                    iconBg = SomulecoBlueLight,
                    iconTint = SomulecoBlue,
                    title = "Post or Article",
                    subtitle = "Publish masterclass tips, galleries, or text updates",
                    onClick = {
                        showCreateBottomSheet = false
                        onNavigate(Screen.ContentEditor)
                    }
                )

                CreateActionItem(
                    icon = Icons.Outlined.ShoppingBag,
                    iconBg = SomulecoGreenSurface,
                    iconTint = SomulecoGreenText,
                    title = "Digital Product",
                    subtitle = "Package guides, presets, or assets protected by DPR",
                    onClick = {
                        showCreateBottomSheet = false
                        onNavigate(Screen.ProductWizard)
                    }
                )

                CreateActionItem(
                    icon = Icons.Outlined.Tv,
                    iconBg = SomulecoPinkLight,
                    iconTint = SomulecoPinkDark,
                    title = "New Channel",
                    subtitle = "Create a focused audience home for a new topic",
                    onClick = {
                        showCreateBottomSheet = false
                        onNavigate(Screen.Channels)
                    }
                )

                CreateActionItem(
                    icon = Icons.Outlined.AutoAwesome,
                    iconBg = SomulecoPurpleLight,
                    iconTint = SomulecoPurple,
                    title = "Ask Creator AI",
                    subtitle = "Get ideas, draft content, or analyze performance",
                    onClick = {
                        showCreateBottomSheet = false
                        onNavigate(Screen.CreatorAI)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Channel Selector Dialog
    if (showChannelSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showChannelSelectorDialog = false },
            title = {
                Text(text = "Select Channel Context", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChannelSelectItem(
                        name = "All Channels",
                        subtitle = "Aggregate view across all ${channels.size} channels",
                        isSelected = selectedChannelId == null,
                        onClick = {
                            CreatorRepository.selectChannelContext(null)
                            showChannelSelectorDialog = false
                        }
                    )
                    channels.forEach { ch ->
                        ChannelSelectItem(
                            name = ch.name,
                            subtitle = "${ch.followersCount} followers • ${ch.contentCount} posts",
                            isSelected = selectedChannelId == ch.id,
                            onClick = {
                                CreatorRepository.selectChannelContext(ch.id)
                                showChannelSelectorDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChannelSelectorDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Ecosystem Dialog
    if (showEcosystemDialog != null) {
        AlertDialog(
            onDismissRequest = { showEcosystemDialog = null },
            icon = { Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SomulecoBlue) },
            title = { Text(showEcosystemDialog ?: "") },
            text = {
                Text(
                    "You are connected to the shared Somuleco Ecosystem. Your Creator account uses unified Somuleco identity, Marketplace listings, and Digital Product Rights protection with durable cross-service trust."
                )
            },
            confirmButton = {
                TextButton(onClick = { showEcosystemDialog = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun DrawerHeader(
    displayName: String,
    handle: String,
    isCreatorMode: Boolean,
    selectedChannel: Channel?,
    onToggleMode: () -> Unit,
    onOpenChannelSelector: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CreatorGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📸", fontSize = 26.sp)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCreatorMode) SomulecoPurpleLight else SomulecoBlueLight,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(if (isCreatorMode) SomulecoPurpleBorder else SomulecoBlueBorder, BorderSubtle)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onToggleMode() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isCreatorMode) Icons.Default.SwitchAccount else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = if (isCreatorMode) SomulecoPurpleDark else SomulecoBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isCreatorMode) "Creator Mode" else "Consumer Mode",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCreatorMode) SomulecoPurpleDark else SomulecoBlueDark
                    )
                }
            }
        }

        Column {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "$handle • Active Creator",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (isCreatorMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenChannelSelector() },
                color = SurfaceWhite,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📺", fontSize = 14.sp)
                        Text(
                            text = selectedChannel?.name ?: "All Channels (Context)",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = SomulecoBlue,
    badge: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("drawer_item_${label.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.12f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) accentColor else TextPrimary
                )
            }

            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) accentColor else SurfaceMuted
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateActionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
private fun ChannelSelectItem(
    name: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SomulecoBlueLight else SurfaceMuted
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (isSelected) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SomulecoBlue)
            }
        }
    }
}
