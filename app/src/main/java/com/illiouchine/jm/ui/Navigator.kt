package com.illiouchine.jm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screens: NavKey {
    @Serializable
    data object Home : Screens

    @Serializable
    data object Settings : Screens

    @Serializable
    data object About : Screens

    // Let's not use objects as parameters in here for now, because we get:
    // > Route com.illiouchine.jm.ui.Screens.PollSetup could not find any NavType
    // > for argument pollTemplate of type com.illiouchine.jm.model.PollConfig?
    @Serializable
    data class PollSetup(
        val cloneablePollId: Int = 0,
        val pollTemplateSlug: String = "",
    ) : Screens

    @Serializable
    data class PollVote(val id: Int = 0) : Screens

    @Serializable
    data class PollResult(val id: Int = 0) : Screens

    @Serializable
    data object ProportionsHelp : Screens

    @Serializable
    data object Loader : Screens

    @Serializable
    data object OnBoarding : Screens
}

interface NavigationAction{
    data class To(val destination: NavKey) : NavigationAction
    data object Up: NavigationAction
    data class Switch(val destination: NavKey) : NavigationAction
    data object Clear : NavigationAction
}

class TopLevelBackStack<T: NavKey>(startKey: T) {
    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set
    val backStack = mutableStateListOf<T>(startKey)

    private fun updateBackStack(){
        backStack.clear()
        backStack.addAll(topLevelBackStacks[topLevelKey] ?: emptyList())
    }

    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null){
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T){
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast(){
        topLevelBackStacks[topLevelKey]?.removeLastOrNull()
        updateBackStack()
    }

    fun replaceStack(vararg keys: T){
        topLevelBackStacks[topLevelKey] = mutableStateListOf(*keys)
        updateBackStack()
    }

    fun clearCurrentStack(){
        val firstEntry = topLevelBackStacks[topLevelKey]?.first()
        firstEntry?.let { entry ->
            topLevelBackStacks.clear()
            topLevelBackStacks[topLevelKey] = mutableStateListOf(topLevelKey)
        }
        updateBackStack()
    }

    fun catch(navigationAction : NavigationAction){
        when(navigationAction){
            is NavigationAction.To -> add(navigationAction.destination as T)
            NavigationAction.Up -> removeLast()
            is NavigationAction.Switch -> switchTopLevel(navigationAction.destination as T)
            is NavigationAction.Clear -> {
                clearCurrentStack()
            }
        }
    }
}