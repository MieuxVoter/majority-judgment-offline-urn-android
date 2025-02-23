package com.illiouchine.jm

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.model.Quality7Grading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DateFormat

class MainViewModel : ViewModel() {

    data class MainViewState(
        val feedback: String? = null,
        val currentPollConfig: PollConfig? = null,
        val pollResult: Poll? = null,
        val judgmentsWereConfirmed: Boolean = false,
    )

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState

    /**
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

    fun onFinishPollSetup(pollSetup: PollConfig) {
        val setupSurvey = pollSetup
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
            val pollConfig = PollConfig(
                subject = subject,
                proposals = setupSurvey.proposals,
                grading = Quality7Grading(),
            )
            _viewState.update {
                it.copy(
                    currentPollConfig = pollConfig,
                    feedback = "New Survey Created, start voting"
                )
            }
        }
    }

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
                currentPollConfig = null
            )
        }
    }
}

