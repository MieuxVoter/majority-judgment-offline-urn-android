package com.illiouchine.jm.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class Navigator {

    private val _sharedFlow = MutableSharedFlow<Screens>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(navTarget: Screens) {
        _sharedFlow.tryEmit(navTarget)
    }

    enum class Screens {
        Home,
        Settings,
        About,
        PollSetup,
        PollVote,
        PollResult,
    }
}