package com.illiouchine.jm

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    data class MainViewState(
        val feedback: String? = null,
        val pollResult: Poll? = null,
    )

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState

    /**
     * Voting state : Subject, Proposals, judgment
     *             - finish voting (survey result)
     *
     *  result State : surveyResult + result Interface
     *              -finish
     */

    fun onFinishVoting(result: Poll) {
        _viewState.update {
            it.copy(pollResult = result)
        }
    }

    fun onDismissFeedback() {
        _viewState.update {
            it.copy(feedback = null)
        }
    }

    fun onResetState() {
        _viewState.update {
            it.copy(
                feedback = null,
                pollResult = null,
            )
        }
    }
}

