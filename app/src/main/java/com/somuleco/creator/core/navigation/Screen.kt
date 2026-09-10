package com.somuleco.creator.core.navigation

sealed class Screen(val route: String, val title: String) {
    // Public & Auth
    object Landing : Screen("landing", "Somuleco Creator")
    object Login : Screen("login", "Sign In")
    object Signup : Screen("signup", "Create Account")
    object VerifyEmail : Screen("verify_email", "Verify Email")
    object ForgotPassword : Screen("forgot_password", "Reset Password")

    // Consumer Experience
    object Home : Screen("home", "Home Feed")
    val ConsumerHome: Screen get() = Home
    object Explore : Screen("explore", "Explore")
    object Following : Screen("following", "Following")
    object Subscriptions : Screen("subscriptions", "My Subscriptions")
    object Library : Screen("library", "Consumer Library")
    object Purchases : Screen("purchases", "Purchases & Orders")
    object Saved : Screen("saved", "Saved Content")
    object Notifications : Screen("notifications", "Notifications")
    object Search : Screen("search", "Search")

    // Creator Experience
    object CreatorActivation : Screen("creator_activation", "Become a Creator")
    object CreatorOnboarding : Screen("creator_onboarding", "Creator Setup")
    object CreatorDashboard : Screen("creator_dashboard", "Creator Dashboard")
    object Channels : Screen("channels", "Channels")
    object Content : Screen("content", "Content Management")
    object Audience : Screen("audience", "Audience Overview")
    object Subscribers : Screen("subscribers", "Subscribers")
    object Store : Screen("store", "Creator Store")
    object Products : Screen("products", "Products")
    object Media : Screen("media", "Media Library")
    object Analytics : Screen("analytics", "Creator Analytics")
    object Revenue : Screen("revenue", "Revenue & Earnings")
    object CreatorAI : Screen("creator_ai", "Creator AI")
    object Rights : Screen("rights", "Digital Product Rights")
    object Integrations : Screen("integrations", "Integrations")
    object CreatorSettings : Screen("creator_settings", "Creator Settings")

    // Creator Creation Flows
    object ContentEditor : Screen("content_editor", "Create Content")
    object ProductWizard : Screen("product_wizard", "Create Digital Product")

    // Details & Deep Dive Views
    object CreatorProfileDetail : Screen("creator_profile_detail", "Creator Profile")

    object ChannelDetail : Screen("channel_detail", "Channel") {
        const val ARG_ID = "channelId"
        const val ROUTE_PATTERN = "channel_detail/{$ARG_ID}"
        const val DEFAULT_ID = "ch_1"
        fun routeFor(id: String) = "channel_detail/$id"
    }

    object ContentDetail : Screen("content_detail", "Content") {
        const val ARG_ID = "contentId"
        const val ROUTE_PATTERN = "content_detail/{$ARG_ID}"
        const val DEFAULT_ID = "content-1"
        fun routeFor(id: String) = "content_detail/$id"
    }

    object ProductDetail : Screen("product_detail", "Product") {
        const val ARG_ID = "productId"
        const val ROUTE_PATTERN = "product_detail/{$ARG_ID}"
        const val DEFAULT_ID = "prod-1"
        fun routeFor(id: String) = "product_detail/$id"
    }

    object SubscriptionPlansDetail : Screen("subscription_plans_detail", "Membership Plans")

    // Route boundary destinations (v0.1-navigation-semantics.md §5, §7)
    object AccessDenied : Screen("access_denied", "Access Restricted")

    companion object {
        /** Every canonical Screen singleton, used for route -> Screen lookups. */
        val all: List<Screen> by lazy {
            listOf(
                Landing, Login, Signup, VerifyEmail, ForgotPassword,
                Home, Explore, Following, Subscriptions, Library, Purchases, Saved, Notifications, Search,
                CreatorActivation, CreatorOnboarding, CreatorDashboard, Channels, Content, Audience,
                Subscribers, Store, Products, Media, Analytics, Revenue, CreatorAI, Rights, Integrations,
                CreatorSettings, ContentEditor, ProductWizard, CreatorProfileDetail, ChannelDetail,
                ContentDetail, ProductDetail, SubscriptionPlansDetail, AccessDenied
            )
        }

        /** Destinations reachable without authentication (the "auth" graph, no shell chrome). */
        val authGraphScreens: Set<Screen> = setOf(
            Landing, Login, Signup, VerifyEmail, ForgotPassword, CreatorActivation, CreatorOnboarding
        )

        /** Creator-only destinations. Reaching one of these while not Creator-activated triggers AccessDenied. */
        val creatorOnlyScreens: Set<Screen> = setOf(
            CreatorDashboard, Channels, ChannelDetail, Content, ContentEditor, Audience, Subscribers,
            Store, Products, ProductWizard, Media, Analytics, Revenue, CreatorAI, Rights, Integrations,
            CreatorSettings
        )

        /** Destinations reachable directly from the drawer/top bar — top-level switches, not pushes. */
        val topLevelShellScreens: Set<Screen> = setOf(
            CreatorDashboard, Channels, Content, Audience, Subscribers, Store, Products, Media,
            Analytics, Revenue, CreatorAI, Rights, Integrations, CreatorSettings,
            Home, Explore, Following, Subscriptions, Library, Purchases, Saved, Notifications
        )

        /** Resolve a Screen for a NavBackStackEntry's destination route (route *pattern*, not filled args). */
        fun forRoute(route: String?): Screen = when (route) {
            null -> CreatorDashboard
            ChannelDetail.ROUTE_PATTERN -> ChannelDetail
            ContentDetail.ROUTE_PATTERN -> ContentDetail
            ProductDetail.ROUTE_PATTERN -> ProductDetail
            else -> all.find { it.route == route } ?: CreatorDashboard
        }
    }
}
