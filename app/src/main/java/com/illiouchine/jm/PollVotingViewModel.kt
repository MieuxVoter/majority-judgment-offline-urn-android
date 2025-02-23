package com.illiouchine.jm

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DateFormat

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

    private val _pollVotingViewState = MutableStateFlow<PollVotingViewState>(PollVotingViewState())
    val pollVotingViewState: StateFlow<PollVotingViewState> = _pollVotingViewState

    fun initNewVotingSession(config: PollConfig) {
        // Rule: if the poll's subject was not provided, use a default.
        val subject = config.subject.ifEmpty {
            //FIXME: manage to get the context, or the translated string any other way
            // context.getString(R.string.poll_of) + " " + DateFormat.getDateInstance().format(Calendar.getInstance().time)
            "Poll of" + " " + DateFormat.getDateInstance().format(Calendar.getInstance().time)
        }
        val pollConfig = PollConfig(
            subject = subject,
            proposals = config.proposals,
            grading = config.grading,
        )
        _pollVotingViewState.update {
            it.copy(
                pollConfig = pollConfig,
                ballots = emptyList(),
                currentBallot = null
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