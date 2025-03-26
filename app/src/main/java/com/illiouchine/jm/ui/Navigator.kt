package com.illiouchine.jm.ui

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable


sealed interface Screens {
    @Serializable
    data object Home : Screens
    @Serializable
    data object Settings : Screens
    @Serializable
    data object About : Screens
    @Serializable
    data class PollSetup(val id: Int = 0) : Screens
    @Serializable
    data class PollVote(val id: Int = 0) : Screens
    @Serializable
    data class PollResult(val id: Int = 0) : Screens
}

sealed interface NavigationAction {
    data class Navigate(
        val destination: Screens,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationAction

    data object NavigateUp : NavigationAction
}

interface Navigator {
    val startDestination: Screens
    val navigationAction: Flow<NavigationAction>

    suspend fun navigateTo(
        destination: Screens,
        navOptions: NavOptionsBuilder.() -> Unit = {}
    )

    suspend fun navigateUp()
}

class DefaultNavigator(
    override val startDestination: Screens,
) : Navigator {
    private val _navigationAction = Channel<NavigationAction>()
    override val navigationAction: Flow<NavigationAction> = _navigationAction.receiveAsFlow()

    override suspend fun navigateTo(
        destination: Screens,
        navOptions: NavOptionsBuilder.() -> Unit
    ) {
        _navigationAction.send(
            NavigationAction.Navigate(
                destination = destination,
                navOptions = navOptions
            )
        )
    }

    override suspend fun navigateUp() {
        _navigationAction.send(NavigationAction.NavigateUp)
    }
}