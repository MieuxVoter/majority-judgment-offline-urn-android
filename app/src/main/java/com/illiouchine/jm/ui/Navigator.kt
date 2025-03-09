package com.illiouchine.jm.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class Navigator {

    private val _sharedFlow = MutableSharedFlow<Screens>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(navTarget: Screens) {
        _sharedFlow.tryEmit(navTarget)
    }
}

@Serializable
sealed class Screens {
    @Serializable data object Home: Screens()
    @Serializable data object Settings: Screens()
    @Serializable data object About: Screens()
    @Serializable data class PollSetup(val config: PollConfig? = null): Screens()
    @Serializable data class PollVote(val pollId: Int) :Screens()
    @Serializable data class PollResult(val poll: Poll) : Screens()
}

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


