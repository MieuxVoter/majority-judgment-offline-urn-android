package com.illiouchine.jm

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PollResultViewModel : ViewModel() {

    data class PollResultViewState(
        val poll: Poll? = null,
    )

    private val _pollResultViewState = MutableStateFlow<PollResultViewState>(PollResultViewState())
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    fun finalizePoll(poll: Poll) {
        _pollResultViewState.update {
            it.copy(poll = poll)
        }
    }
}