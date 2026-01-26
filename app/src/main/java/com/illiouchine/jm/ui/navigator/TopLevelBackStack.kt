package com.illiouchine.jm.ui.navigator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
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

    private var backStacks: SnapshotStateMap<NavKey, SnapshotStateList<NavKey>> = mutableStateMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var currentBackStackKey by mutableStateOf(startKey)
        private set

    val currentBackStack = mutableStateListOf(startKey)

    private fun updateBackStack() {
        currentBackStack.clear()
        currentBackStack.addAll(backStacks[currentBackStackKey] ?: emptyList())
    }

    fun switchTopLevel(key: NavKey) {
        if (backStacks[key] == null) {
            backStacks[key] = mutableStateListOf(key)
        }
        currentBackStackKey = key
        updateBackStack()
    }

    fun add(key: NavKey) {
        backStacks[currentBackStackKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        backStacks[currentBackStackKey]?.removeLastOrNull()
        updateBackStack()
    }

//    fun replaceStack(vararg keys: NavKey) {
//        backStacks[currentBackStackKey] = mutableStateListOf(*keys)
//        updateBackStack()
//    }

    fun clearCurrentStack(destination: NavKey?) {
        val firstEntry = backStacks[currentBackStackKey]?.first()
        firstEntry?.let { _ ->
            backStacks.clear()
            backStacks[currentBackStackKey] = mutableStateListOf(currentBackStackKey)
            if (destination != null) {
                backStacks[currentBackStackKey]?.add(destination)
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