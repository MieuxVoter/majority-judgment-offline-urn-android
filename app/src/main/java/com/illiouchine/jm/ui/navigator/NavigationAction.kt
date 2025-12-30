package com.illiouchine.jm.ui.navigator

import androidx.navigation3.runtime.NavKey

/**
 *
 * This sealed interface defines all possible navigation events that the UI can trigger.
 *
 */
sealed interface NavigationAction {
    data class To(val destination: NavKey) : NavigationAction
    data object Back : NavigationAction
    data class Switch(val destination: NavKey) : NavigationAction
    data object Clear : NavigationAction
    data class ClearTo(val destination: NavKey?) : NavigationAction
}