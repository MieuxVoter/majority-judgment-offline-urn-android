package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PollVotingViewModel : ViewModel() {

    data class PollVotingViewState(
        val pollConfig: PollConfig = PollConfig(),
        val ballots: List<Ballot> = emptyList(),
        val currentBallot: Ballot? = null,
    ) {
        fun isInStateReady(): Boolean {
            return null == currentBallot
        }
        fun isInStateVoting(): Boolean {
            return (null != currentBallot) && (currentBallot.judgments.size < pollConfig.proposals.size)
        }
    }

    private val _pollVotingViewState = MutableStateFlow(PollVotingViewState())
    val pollVotingViewState: StateFlow<PollVotingViewState> = _pollVotingViewState

    fun initNewVotingSession(config: PollConfig) {
        _pollVotingViewState.update {
            it.copy(
                pollConfig = config,
                ballots = emptyList(),
                currentBallot = null,
            )
        }
    }

    fun resumeVotingSession(poll: Poll) {
        _pollVotingViewState.update {
            it.copy(
                pollConfig = poll.pollConfig,
                ballots = poll.ballots,
                currentBallot = null,
            )
        }
    }

    fun initParticipantVotingSession() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = Ballot()
            )
        }
    }

    fun onJudgmentCast(judgment: Judgment) {
        // Rule : Prevent voting for the same proposal two time
        if (_pollVotingViewState.value.currentBallot?.isAlreadyCast(judgment) == true){
            return
        }
        // Add judgment to current ballot
        _pollVotingViewState.update {
            it.copy(
                currentBallot = it.currentBallot?.withJudgment(judgment) ?: Ballot(listOf(judgment))
            )
        }
    }

    fun onBallotConfirmed(ballot: Ballot) {
        // Add ballot to preview ballots & reset current ballot
        _pollVotingViewState.update {
            it.copy(
                currentBallot = null,
                ballots = it.ballots + ballot
            )
        }
    }

    fun onBallotCanceled() {
        _pollVotingViewState.update {
            it.copy(currentBallot = null)
        }
    }

    fun onCancelLastJudgment() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = it.currentBallot?.withoutLastJudgment()
            )
        }
    }
}