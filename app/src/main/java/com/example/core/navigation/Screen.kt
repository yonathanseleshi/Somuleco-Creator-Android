package com.example.core.navigation

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
    object ChannelDetail : Screen("channel_detail", "Channel")
    object ContentDetail : Screen("content_detail", "Content")
    object ProductDetail : Screen("product_detail", "Product")
    object SubscriptionPlansDetail : Screen("subscription_plans_detail", "Membership Plans")
}
