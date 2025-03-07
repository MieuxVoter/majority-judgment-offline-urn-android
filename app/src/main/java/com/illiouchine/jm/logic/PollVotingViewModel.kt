package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PollVotingViewModel(
    private val pollDataSource: PollDataSource,
    private val navigator: Navigator
) : ViewModel() {

    data class PollVotingViewState(
        val pollConfig: PollConfig = PollConfig(),
        val ballots: List<Ballot> = emptyList(),
        val currentBallot: Ballot? = null,
        val currentProposalsOrder: List<Int> = emptyList(),
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

    private fun generateRandomOrder(size: Int): List<Int> = (0..<size).shuffled()

    fun initVotingSession(
        config: PollConfig,
        ballots: List<Ballot>,
    ) {
        _pollVotingViewState.update {
            it.copy(
                pollConfig = config,
                ballots = ballots,
                currentBallot = null,
            )
        }
    }

    fun initParticipantVotingSession() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = Ballot(),
                currentProposalsOrder = generateRandomOrder(it.pollConfig.proposals.size),
            )
        }
    }

    fun onJudgmentCast(judgment: Judgment) {
        // Rule: Voting for the same proposal two times is not allowed
        if (_pollVotingViewState.value.currentBallot?.isAlreadyCast(judgment) == true) {
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
        // Add ballot to previous ballots & reset current ballot
        _pollVotingViewState.update {
            it.copy(
                currentBallot = null,
                ballots = it.ballots + ballot,
            )
        }
    }

    fun onBallotCanceled() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = null,
            )
        }
    }

    fun onCancelLastJudgment() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = it.currentBallot?.withoutLastJudgment(),
            )
        }
    }

    fun finalizePoll() {
        viewModelScope.launch {
            val poll = Poll(
                pollConfig = _pollVotingViewState.value.pollConfig,
                ballots = _pollVotingViewState.value.ballots,
            )
            pollDataSource.savePolls(poll)
            navigator.navigateTo(Screens.PollResult(poll = poll))
        }
    }
}