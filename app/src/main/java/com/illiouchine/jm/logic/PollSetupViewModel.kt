package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PollSetupViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val pollDataSource: PollDataSource,
) : ViewModel() {

    data class PollSetupViewState(
        val pollSetup: PollConfig = PollConfig(),
        val subjectSuggestion: List<String> = emptyList(),
        val proposalSuggestion: List<String> = emptyList(),
        val feedback: String? = null,
    )

    private val _pollSetupViewState = MutableStateFlow(PollSetupViewState())
    val pollSetupViewState: StateFlow<PollSetupViewState> = _pollSetupViewState

    fun startPollSetup(
        pollConfig: PollConfig? = null
    ) {
        val initialPollConfig =
            pollConfig ?: PollConfig(grading = sharedPrefsHelper.getDefaultGrading())
        _pollSetupViewState.update {
            it.copy(pollSetup = initialPollConfig)
        }
    }

    fun onAddSubject(subject: String) {
        _pollSetupViewState.update {
            it.copy(pollSetup = it.pollSetup.copy(subject = subject))
        }
    }

    fun onAddProposal(proposal: String) {
        // Rule: If the proposal already exists, do not add it and show a warning.
        if (proposalAlreadyExist(proposal)) {
            _pollSetupViewState.update {
                it.copy(
                    // TODO : Do not use string as feedback,
                    //  use custom error message with code that can be map to string resource in view
                    feedback = "The proposal `${proposal}` already exists."
                )
            }
        } else {
            val newProposals = buildList {
                addAll(_pollSetupViewState.value.pollSetup.proposals)
                add(proposal)
            }

            _pollSetupViewState.update {
                it.copy(
                    pollSetup = it.pollSetup.copy(proposals = newProposals),
                    proposalSuggestion = emptyList(),
                    subjectSuggestion = emptyList()
                )
            }
        }
    }

    private fun proposalAlreadyExist(proposal: String): Boolean {
        return _pollSetupViewState.value.pollSetup.proposals.any { it == proposal }
    }

    fun onRemoveProposal(proposal: String) {
        val newProposals = _pollSetupViewState.value.pollSetup.proposals - proposal
        _pollSetupViewState.update {
            it.copy(pollSetup = it.pollSetup.copy(proposals = newProposals))
        }
    }

    fun onDismissFeedback() {
        _pollSetupViewState.update {
            it.copy(feedback = null)
        }
    }

    fun onGradingSelected(grading: Grading) {
        _pollSetupViewState.update {
            it.copy(pollSetup = it.pollSetup.copy(grading = grading))
        }
    }

    fun getSubjectSuggestion(subject: String = ""){
        viewModelScope.launch {
            val subjectSuggestion = if (subject.isNotEmpty()){
                val polls = pollDataSource.getAllPoll()
                polls.filter { it.pollConfig.subject.contains(other = subject, ignoreCase = true) }
                    .map { it.pollConfig.subject }
            } else {
                emptyList()
            }
            _pollSetupViewState.update {
                it.copy(subjectSuggestion = subjectSuggestion)
            }
        }
    }

    fun getProposalSuggestion(proposal: String = ""){
        viewModelScope.launch {
            val proposalSuggestion = if (proposal.isNotEmpty()){
                pollDataSource.getAllPoll()
                    .map { it.pollConfig.proposals }
                    .flatten()
                    .filter { it.contains(other = proposal, ignoreCase = true) }
            } else {
                emptyList()
            }
            _pollSetupViewState.update {
                it.copy(proposalSuggestion = proposalSuggestion)
            }
        }
    }
}