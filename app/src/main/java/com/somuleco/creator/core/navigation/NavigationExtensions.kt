package com.somuleco.creator.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Navigation-graph route ids, per v0.1-navigation-semantics.md and FND-Wave-04 §7.3.2.
 */
object NavGraphs {
    const val AUTH = "auth"
    const val CONSUMER = "consumer"
    const val CREATOR = "creator"
}

/**
 * Standard Navigation Compose pattern for top-level (drawer/rail) destination switches:
 * pop back to the graph's start destination while saving state, avoid stacking duplicate
 * copies of the same top-level destination, and restore previously saved state.
 *
 * Used for every destination reachable directly from CreatorShell's drawer/top bar. Detail
 * and creation-flow destinations reached from *within* a screen (content/product/channel
 * detail, content editor, product wizard, search, subscription plans, creator profile) are
 * pushed with plain [NavController.navigate] instead, so Back returns to the caller.
 */
fun NavController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Route-boundary-aware navigation for anything reachable from the shell chrome
 * (drawer items, top bar Search/Notifications, the Create FAB sheet). Implements the
 * three-outcome boundary from v0.1-navigation-semantics.md §5:
 *  - unauthenticated -> auth graph (Login), preserving nothing further to restore in this
 *    wave's mock-identity world, per §5's "structurally real, pluggable identity" note.
 *  - authenticated but not Creator-activated, requesting a Creator-only destination ->
 *    AccessDenied.
 *  - otherwise -> the normal top-level/push navigation for that destination.
 */
fun NavController.navigateFromShell(
    screen: Screen,
    isAuthenticated: Boolean,
    isCreator: Boolean
) {
    when {
        !isAuthenticated -> navigate(Screen.Login.route) {
            popUpTo(graph.id) { inclusive = true }
        }
        screen in Screen.creatorOnlyScreens && !isCreator -> navigate(Screen.AccessDenied.route)
        screen in Screen.topLevelShellScreens -> navigateTopLevel(screen.route)
        else -> navigate(screen.route)
    }
}
