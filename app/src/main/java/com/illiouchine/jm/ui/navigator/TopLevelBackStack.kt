package com.illiouchine.jm.ui.navigator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

/**
 * Manages a multi-level navigation back stack, designed for applications featuring
 * top-level navigation, such as a Bottom Navigation Bar or a Navigation Rail.
 *
 * This class maintains a separate navigation stack for each top-level "tab" or section
 * of the application. When a user switches between these main sections, this class
 * swaps to the corresponding stack, thereby preserving the unique navigation history of each section.
 *
 * @see NavigationAction
 *
 * @param startKey The initial navigation key (`NavKey`) for the application's starting screen.
 *                 This key is used to initialize the very first navigation stack.
 */
class TopLevelBackStack(startKey: NavKey) {
    private var topLevelBackStacks: HashMap<NavKey, SnapshotStateList<NavKey>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set
    val backStack = mutableStateListOf<NavKey>(startKey)

    private fun updateBackStack() {
        backStack.clear()
        backStack.addAll(topLevelBackStacks[topLevelKey] ?: emptyList())
    }

    fun switchTopLevel(key: NavKey) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: NavKey) {
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        topLevelBackStacks[topLevelKey]?.removeLastOrNull()
        updateBackStack()
    }

    fun replaceStack(vararg keys: NavKey) {
        topLevelBackStacks[topLevelKey] = mutableStateListOf(*keys)
        updateBackStack()
    }

    fun clearCurrentStack(destination: NavKey?) {
        val firstEntry = topLevelBackStacks[topLevelKey]?.first()
        firstEntry?.let { entry ->
            topLevelBackStacks.clear()
            topLevelBackStacks[topLevelKey] = mutableStateListOf(topLevelKey)
            if (destination != null) {
                topLevelBackStacks[topLevelKey]?.add(destination)
            }
        }
        updateBackStack()
    }

    fun handle(navigationAction: NavigationAction) {
        when (navigationAction) {
            is NavigationAction.To -> add(navigationAction.destination)
            NavigationAction.Back -> removeLast()
            is NavigationAction.Switch -> switchTopLevel(navigationAction.destination)
            NavigationAction.Clear -> clearCurrentStack(null)
            is NavigationAction.ClearTo -> {
                clearCurrentStack(navigationAction.destination)
            }
        }
    }
}