package com.somuleco.creator

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.somuleco.creator.data.model.UserReference
import com.somuleco.creator.data.repository.CreatorRepository
import com.somuleco.creator.ui.theme.SomulecoTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Real navigation-behavior tests for the Navigation Compose migration
 * (FND-Wave-04 §7.3.9 / §10.1), replacing the previous route-string-only data assertions.
 * Drives the shell via the test tags already present in CreatorShell.kt
 * (`drawer_sheet`, `button_open_drawer`, `drawer_item_<label>`, `fab_create`) and the
 * per-screen `screen_*` test tags already present on each destination composable.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var restoreUser: UserReference

    @Before
    fun setUp() {
        // CreatorRepository is a process-wide singleton; snapshot + restore around each test so
        // mutations (logout, mode toggles) in one test never leak into the next.
        restoreUser = CreatorRepository.currentUser.value
        runBlocking { CreatorRepository.login(restoreUser.email, "password") }
        CreatorRepository.setCreatorMode(true)
    }

    @After
    fun tearDown() {
        runBlocking { CreatorRepository.login(restoreUser.email, "password") }
        CreatorRepository.setCreatorMode(true)
    }

    @Test
    fun drawerNavigation_switchesTopLevelDestination() {
        composeTestRule.setContent { SomulecoTheme { SomulecoApp() } }

        // Boots into Creator Dashboard per the mock authenticated+Creator-activated default
        // (v0.1-navigation-semantics.md §3.2).
        composeTestRule.onNodeWithTag("screen_creator_dashboard").assertExists()

        composeTestRule.onNodeWithTag("button_open_drawer").performClick()
        composeTestRule.onNodeWithTag("drawer_sheet").assertExists()

        composeTestRule.onNodeWithTag("drawer_item_channels").performClick()
        composeTestRule.waitForIdle()

        // popUpTo/launchSingleTop/restoreState top-level switch actually navigated, and closed
        // the drawer behind it.
        composeTestRule.onNodeWithTag("screen_channels").assertExists()
        composeTestRule.onNodeWithTag("drawer_sheet").assertIsNotDisplayed()
    }

    @Test
    fun backHandler_closesOpenDrawer_beforePoppingBackStack() {
        composeTestRule.setContent { SomulecoTheme { SomulecoApp() } }

        composeTestRule.onNodeWithTag("button_open_drawer").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drawer_sheet").assertExists()

        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        composeTestRule.waitForIdle()

        // Back closed the drawer rather than popping the underlying back stack or exiting —
        // still on Creator Dashboard, drawer no longer showing (v0.1-navigation-semantics.md §8).
        composeTestRule.onNodeWithTag("drawer_sheet").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("screen_creator_dashboard").assertExists()
    }

    @Test
    fun backFromChannelDetail_popsToChannelsList_notHardcodedJump() {
        composeTestRule.setContent { SomulecoTheme { SomulecoApp() } }

        composeTestRule.onNodeWithTag("button_open_drawer").performClick()
        composeTestRule.onNodeWithTag("drawer_item_channels").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_channels").assertExists()

        // Open the first channel's detail screen from the Channels list.
        val firstChannel = CreatorRepository.channels.value.first()
        composeTestRule.onNodeWithText(firstChannel.name).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_channel_detail").assertExists()

        // Back pops the real nav back stack (ChannelDetailScreen.onBackClick is wired to
        // navController.popBackStack(), no longer a hardcoded jump to Screen.Channels).
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_channel_detail").assertDoesNotExist()
        composeTestRule.onNodeWithTag("screen_channels").assertExists()
    }

    @Test
    fun unauthenticatedUser_cannotReachCreatorDestination() {
        runBlocking { CreatorRepository.logout() }

        composeTestRule.setContent { SomulecoTheme { SomulecoApp() } }

        // The route boundary lands an unauthenticated session in the auth graph (its start
        // destination, Landing) — never silently on a Creator destination
        // (v0.1-navigation-semantics.md §5).
        composeTestRule.onNodeWithTag("screen_landing").assertExists()
        composeTestRule.onNodeWithTag("screen_creator_dashboard").assertDoesNotExist()
    }

    @Test
    fun togglingToConsumerMode_landsOnHome_andHidesCreatorWorkspaceSection() {
        composeTestRule.setContent { SomulecoTheme { SomulecoApp() } }
        composeTestRule.onNodeWithTag("screen_creator_dashboard").assertExists()

        // Switching to consumer mode lands on Home (v0.1-navigation-semantics.md §3.3), and the
        // drawer's Creator Workspace section is presentation-gated on mode, not just on route.
        composeTestRule.onNodeWithTag("button_toggle_mode").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("screen_consumer_home").assertExists()
        composeTestRule.onNodeWithTag("button_open_drawer").performClick()
        composeTestRule.onNodeWithTag("drawer_item_creator_dashboard").assertDoesNotExist()
    }
}
