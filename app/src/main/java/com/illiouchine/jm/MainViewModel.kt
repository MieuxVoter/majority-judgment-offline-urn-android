package com.illiouchine.jm

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.SetupSurvey
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DateFormat

class MainViewModel(
    private val sharedPreferences: SharedPrefsHelper
): ViewModel() {

    data class MainViewState(
        val feedback: String? = null,
        val showOnboarding: Boolean = true,
        val setupSurvey: SetupSurvey = SetupSurvey(),
        val currentSurvey: Survey? = null,
        val surveyResult: SurveyResult? = null
    )

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState

    init {
        loadShowOnboarding()
    }

    fun onDismissFeedback(){
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

    fun onFinishOnBoarding(){
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
     * Voting state : Subject, Proposals, judgement
     *             - finish voting (survey result)
     *
     *  result State : surveyResult + result Interface
     *              -finish
     */

    fun onAddSubject(subject: String){
        _viewState.update {
            it.copy(setupSurvey = it.setupSurvey.copy(subject = subject))
        }
    }
    fun onAddProposals(proposal : String){
        // Rule: If proposition already exist. do Nothing
        if (_viewState.value.setupSurvey.props.any { it == proposal }){
            _viewState.update {
                it.copy(
                    feedback = "The proposition : $proposal already exist"
                )
            }
        } else {
            // Rule: if the proposition name is not specified, use a default
            val newProposals = _viewState.value.setupSurvey.props + proposal

            _viewState.update {
                it.copy(setupSurvey = it.setupSurvey.copy(props = newProposals))
            }
        }
    }

    fun onRemoveProposal(proposal: String){
        val newProposals = _viewState.value.setupSurvey.props - proposal
        _viewState.update {
            it.copy(setupSurvey = it.setupSurvey.copy(props = newProposals))
        }
    }

    fun onFinishSetupSurvey(){
        // Rule: if the poll's subject was not provided, use a default.
        val setupSurvey = viewState.value.setupSurvey
        val subject = setupSurvey.subject.ifEmpty {
            "Scrutin du" + " " + DateFormat.getDateInstance()
                .format(Calendar.getInstance().time)
        }
        // Rule: if no proposals were added, abort and complain.
        // Note: since the button is now disabled in that case, this never happens anymore.
        if (setupSurvey.props.size < 2) {
            _viewState.update {
                it.copy(feedback = "A poll needs at least two proposals.")
            }
        } else {
            val survey = Survey(subject = subject, props = setupSurvey.props)
            _viewState.update {
                it.copy(
                    currentSurvey = survey,
                    feedback = "New Survey Created, start voting"
                )
            }
        }
    }

    fun onFinishVoting(result: SurveyResult){
        _viewState.update {
            it.copy(surveyResult = result)
        }
    }

    fun onResetState(){
        _viewState.update {
            it.copy(
                feedback = null,
                setupSurvey = SetupSurvey(),
                surveyResult = null,
                currentSurvey = null
            )
        }
    }
}

