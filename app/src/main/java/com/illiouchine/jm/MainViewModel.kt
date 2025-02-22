package com.illiouchine.jm

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Quality7Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DateFormat

class MainViewModel(
    private val sharedPreferences: SharedPrefsHelper,
) : ViewModel() {

    data class MainViewState(
        val feedback: String? = null,
        val showOnboarding: Boolean = true,
        val pollSetup: Poll = Poll(),
        val currentPoll: Poll? = null,
        val pollResult: PollResult? = null,
        val judgmentsWereConfirmed: Boolean = false,
    )

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState

    init {
        loadShowOnboarding()
    }

    fun onDismissFeedback() {
        _viewState.update {
            it.copy(feedback = null)
        }
    }

    private fun loadShowOnboarding() {
        val showOnboarding = sharedPreferences.getShowOnboarding()
        _viewState.update {
            it.copy(showOnboarding = showOnboarding)
        }
    }

    fun onFinishOnBoarding() {
        sharedPreferences.editShowOnboarding(false)
        _viewState.update {
            it.copy(showOnboarding = false)
        }
    }

    /**
     * show onboarding
     * Setup survey State : Subject, Proposals,
     *          - finish Setup
     *          - add Proposals
     *
     * Voting state : Subject, Proposals, judgment
     *             - finish voting (survey result)
     *
     *  result State : surveyResult + result Interface
     *              -finish
     */

    fun onAddSubject(subject: String) {
        _viewState.update {
            it.copy(pollSetup = it.pollSetup.copy(subject = subject))
        }
    }

    fun onAddProposal(proposal: String) {
        // Rule: If the proposal already exists, do not add it and show a warning.
        if (_viewState.value.pollSetup.proposals.any { it == proposal }) {
            _viewState.update {
                it.copy(
                    feedback = "The proposal `${proposal}` already exists."
                )
            }
        } else {
            val newProposals = _viewState.value.pollSetup.proposals + proposal

            _viewState.update {
                it.copy(pollSetup = it.pollSetup.copy(proposals = newProposals))
            }
        }
    }

    fun onRemoveProposal(proposal: String) {
        val newProposals = _viewState.value.pollSetup.proposals - proposal
        _viewState.update {
            it.copy(pollSetup = it.pollSetup.copy(proposals = newProposals))
        }
    }

    fun onFinishSetupSurvey() {
        val setupSurvey = viewState.value.pollSetup
        // Rule: if the poll's subject was not provided, use a default.
        val subject = setupSurvey.subject.ifEmpty {
            // FIXME: manage to get the context, or the translated string any other way
//            context.getString(R.string.poll_of) + " " + DateFormat.getDateInstance().format(Calendar.getInstance().time)
            "Poll of" + " " + DateFormat.getDateInstance().format(Calendar.getInstance().time)
        }
        // Rule: if less than 2 proposals were added, abort and complain.
        // Note: since the button is now disabled in that case, this never happens anymore.
        if (setupSurvey.proposals.size < 2) {
            _viewState.update {
                it.copy(feedback = "A poll needs at least two proposals.")
            }
        } else {
            val poll = Poll(
                subject = subject,
                proposals = setupSurvey.proposals,
                grading = Quality7Grading(),
            )
            _viewState.update {
                it.copy(
                    currentPoll = poll,
                    feedback = "New Survey Created, start voting"
                )
            }
        }
    }

    fun onFinishVoting(result: PollResult) {
        _viewState.update {
            it.copy(pollResult = result)
        }
    }

//    fun onJudgmentsConfirmed() {
//        _viewState.update {
//            it.copy(judgmentsWereConfirmed = true)
//        }
//    }

//    fun onJudgmentsCancelled() {
//    }

    fun onResetState() {
        _viewState.update {
            it.copy(
                feedback = null,
                pollSetup = Poll(),
                pollResult = null,
                currentPoll = null
            )
        }
    }
}

