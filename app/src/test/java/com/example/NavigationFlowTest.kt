package com.example

import com.example.core.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NavigationFlowTest {

    @Test
    fun testScreenRoutesAndTitles() {
        val publicScreens = listOf(
            Screen.Landing to "landing",
            Screen.Login to "login",
            Screen.Signup to "signup",
            Screen.VerifyEmail to "verify_email",
            Screen.ForgotPassword to "forgot_password"
        )
        for ((screen, route) in publicScreens) {
            assertEquals(route, screen.route)
            assertNotNull(screen.title)
        }

        val creatorScreens = listOf(
            Screen.CreatorDashboard to "creator_dashboard",
            Screen.Channels to "channels",
            Screen.Content to "content",
            Screen.Audience to "audience",
            Screen.Subscribers to "subscribers",
            Screen.Store to "store",
            Screen.Products to "products",
            Screen.Media to "media",
            Screen.Analytics to "analytics",
            Screen.Revenue to "revenue",
            Screen.CreatorAI to "creator_ai",
            Screen.Rights to "rights",
            Screen.Integrations to "integrations",
            Screen.CreatorSettings to "creator_settings"
        )
        for ((screen, route) in creatorScreens) {
            assertEquals(route, screen.route)
            assertNotNull(screen.title)
        }

        val consumerScreens = listOf(
            Screen.Home to "home",
            Screen.Explore to "explore",
            Screen.Following to "following",
            Screen.Subscriptions to "subscriptions",
            Screen.Library to "library",
            Screen.Purchases to "purchases",
            Screen.Saved to "saved",
            Screen.Notifications to "notifications",
            Screen.Search to "search"
        )
        for ((screen, route) in consumerScreens) {
            assertEquals(route, screen.route)
            assertNotNull(screen.title)
        }
    }
}
