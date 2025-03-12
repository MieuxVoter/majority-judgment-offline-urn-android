package com.illiouchine.jm.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


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

/**
 *
 *
 *
 *
 *
 *
 *
 */

object CustomNavType {

    val NullablePollConfigType = object : NavType<PollConfig?>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): PollConfig? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): PollConfig? {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: PollConfig?) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: PollConfig?): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }

    val PollConfigType = object : NavType<PollConfig>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): PollConfig {
            return Json.decodeFromString(bundle.getString(key) ?: return PollConfig())
        }

        override fun parseValue(value: String): PollConfig {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: PollConfig) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: PollConfig): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }

    val Ballots = object : NavType<List<Ballot>>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): List<Ballot> {
            return Json.decodeFromString(bundle.getString(key) ?: return emptyList())
        }

        override fun parseValue(value: String): List<Ballot> {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: List<Ballot>) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: List<Ballot>): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }

    val Poll = object : NavType<Poll>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): Poll {
            return Json.decodeFromString(bundle.getString(key) ?: return Poll(pollConfig = PollConfig(), ballots = emptyList()))
        }

        override fun parseValue(value: String): Poll {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: Poll) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: Poll): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
}


