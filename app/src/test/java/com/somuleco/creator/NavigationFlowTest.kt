package com.somuleco.creator

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.data.model.UserIdentity
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Real navigation-behavior tests for the Navigation Compose migration
 * (FND-Wave-04 §7.3.9 / §10.1), replacing the previous route-string-only data assertions.
 * Drives the shell via the test tags already present in CreatorShell.kt
 * (`drawer_sheet`, `button_open_drawer`, `drawer_item_<label>`, `fab_create`) and the
 * per-screen `screen_*` test tags already present on each destination composable.
 *
 * Updated for Foundation Wave 05: `MainActivity`/`CreatorShell`/migrated screens now
 * resolve their data through Hilt-supplied ViewModels and repository interfaces rather
 * than the deleted `CreatorRepository` singleton, so this test runs as a
 * `@HiltAndroidTest` against the real `MainActivity` (itself `@AndroidEntryPoint`)
 * instead of a bare `ComponentActivity`.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = dagger.hilt.android.testing.HiltTestApplication::class)
class NavigationFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var channelRepository: ChannelRepository

    @Inject
    lateinit var appSessionState: AppSessionState

    private lateinit var restoreUser: UserIdentity

    @Before
    fun setUp() {
        hiltRule.inject()
        // Repositories are Hilt @Singleton instances; snapshot + restore around each test so
        // mutations (logout, mode toggles) in one test never leak into the next.
        restoreUser = authRepository.currentUser.value
        runBlocking { authRepository.login(restoreUser.email, "password") }
        authRepository.setCreatorMode(true)
        // Foundation Wave 09 (`v0.1-creator-authorization.md` §7, plan §7.4 task 5): reaching a
        // Creator-only destination now requires a real, selected-account "active" status
        // (AppSessionState.isCreatorAccountActive) rather than the previously-hardcoded
        // AuthState.Authenticated.isCreator — this suite is exercising the shell/navigation
        // boundary, not Creator Account activation itself, so it simulates an
        // already-activated caller directly rather than driving a real CreatorAccountViewModel
        // through activate()/completeOnboarding().
        appSessionState.setCreatorAccountActive(true)
    }

    @After
    fun tearDown() {
        runBlocking { authRepository.login(restoreUser.email, "password") }
        authRepository.setCreatorMode(true)
        appSessionState.setCreatorAccountActive(true)
    }

    @Test
    fun drawerNavigation_switchesTopLevelDestination() {
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
        composeTestRule.onNodeWithTag("button_open_drawer").performClick()
        composeTestRule.onNodeWithTag("drawer_item_channels").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_channels").assertExists()

        // Open the first channel's detail screen from the Channels list.
        val firstChannel = channelRepository.channels.value.first()
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
        runBlocking { authRepository.logout() }
        composeTestRule.waitForIdle()

        // The route boundary lands an unauthenticated session in the auth graph (its start
        // destination, Landing) — never silently on a Creator destination
        // (v0.1-navigation-semantics.md §5).
        composeTestRule.onNodeWithTag("screen_landing").assertExists()
        composeTestRule.onNodeWithTag("screen_creator_dashboard").assertDoesNotExist()
    }

    @Test
    fun togglingToConsumerMode_landsOnHome_andHidesCreatorWorkspaceSection() {
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
