package com.illiouchine.jm.ui.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screens : NavKey {
    @Serializable
    data object Onboarding : Screens

    @Serializable
    data object Home : Screens

    @Serializable
    data object Settings : Screens

    @Serializable
    data object About : Screens

    // Let's not use objects (like PollConfig) as parameters in here for now, because we get:
    // > Route com.illiouchine.jm.ui.Screens.PollSetup could not find any NavType
    // > for argument pollTemplate of type com.illiouchine.jm.model.PollConfig?
    // UPDATE: Perhaps worth trying again now that we've updated the navigation library?
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
}