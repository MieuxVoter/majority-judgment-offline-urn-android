package com.illiouchine.jm.logic

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
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
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val navigator: Navigator,
) : ViewModel() {

    data class PollVotingViewState(
        val pollId: Int = 0,
        val pollConfig: PollConfig = PollConfig(),
        val ballots: List<Ballot> = emptyList(),
        val currentBallot: Ballot? = null,
        val amountOfBallotsCastThisSession: Int = 0,
        val currentProposalsOrder: List<Int> = emptyList(),
        val pinScreens: Boolean = false,
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
    private var currentPollId: Int? = null

    fun initVotingSessionForPoll(
        pollId: Int,
    ) {
        viewModelScope.launch {
            if (currentPollId == pollId) {
                /* Do nothing : Reload from configuration change */
            } else {
                val pinScreens = sharedPrefsHelper.getPinScreen()
                val poll = pollDataSource.getPollById(pollId)

                _pollVotingViewState.update {
                    it.copy(
                        pollId = pollId,
                        pollConfig = poll?.pollConfig ?: PollConfig(),
                        ballots = poll?.ballots ?: emptyList(),
                        amountOfBallotsCastThisSession = 0,
                        currentBallot = null,
                        pinScreens = pinScreens,
                    )
                }
            }
            currentPollId = pollId
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

    fun castJudgment(judgment: Judgment) {
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

    fun confirmBallot(context: Context, ballot: Ballot) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.success)
        if (sharedPrefsHelper.getPlaySound()) {
            mediaPlayer.start()
        }

        // Add ballot to previous ballots & reset current ballot
        _pollVotingViewState.update {
            it.copy(
                currentBallot = null,
                ballots = it.ballots + ballot,
                amountOfBallotsCastThisSession = _pollVotingViewState.value.amountOfBallotsCastThisSession + 1,
            )
        }

        viewModelScope.launch {
            pollDataSource.saveBallot(ballot, _pollVotingViewState.value.pollId)
        }
    }

    fun cancelBallot() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = null,
            )
        }
    }

    fun cancelLastJudgment() {
        _pollVotingViewState.update {
            it.copy(
                currentBallot = it.currentBallot?.withoutLastJudgment(),
            )
        }
    }

    fun finalizePoll() {
        viewModelScope.launch {
            val poll = Poll(
                id = _pollVotingViewState.value.pollId,
                pollConfig = _pollVotingViewState.value.pollConfig,
                ballots = _pollVotingViewState.value.ballots,
            )
            val newPollId = pollDataSource.savePoll(poll)
            navigator.navigateTo(Screens.PollResult(id = newPollId))
            currentPollId = null
        }
    }

    fun tryToGoBack(context: Context) {
        viewModelScope.launch {
            if (_pollVotingViewState.value.ballots.isNotEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_you_cannot_go_back_from_here),
                    LENGTH_SHORT,
                ).show()
            }
        }
    }
}