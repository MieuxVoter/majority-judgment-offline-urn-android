package com.illiouchine.jm.logic

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

class PollSetupViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val pollDataSource: PollDataSource,
    private val navigator: Navigator,
) : ViewModel() {

    data class PollSetupViewState(
        val pollSetup: PollConfig = PollConfig(),
        val subjectSuggestion: List<String> = emptyList(),
        val proposalSuggestion: List<String> = emptyList(),
        @StringRes val feedback: Int? = null,
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
        navigator.navigateTo(Navigator.Screens.PollSetup)
    }

    fun onAddSubject(context: Context, subject: String) {
        val newSubject = subject.ifEmpty { generateSubject(context = context) }
        _pollSetupViewState.update {
            it.copy(pollSetup = it.pollSetup.copy(subject = newSubject))
        }
    }

    fun onAddProposal(context: Context, proposal: String = "") {
        // Rule: if the proposal name is not specified, use a default
        val notEmptyProposal = proposal.ifEmpty { generateProposalName(context) }
        // Rule: proposals must have unique names
        if (proposalAlreadyExist(notEmptyProposal)) {
            _pollSetupViewState.update {
                it.copy(
                    feedback = R.string.toast_proposal_name_already_exists
                )
            }
        } else {
            val newProposals = buildList {
                addAll(_pollSetupViewState.value.pollSetup.proposals)
                add(notEmptyProposal)
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

    private fun generateProposalName(context: Context): String {
        return buildString {
            append(context.getString(R.string.proposal))
            append(" ")
            append((65 + _pollSetupViewState.value.pollSetup.proposals.size).toChar())
        }
    }

    private fun generateSubject(context: Context): String {
        return buildString {
            append(context.getString(R.string.poll_of))
            append(" ")
            append(DateFormat.getDateInstance().format(Calendar.getInstance().time))
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

    fun getSubjectSuggestion(subject: String = "") {
        viewModelScope.launch {
            val subjectSuggestion = if (subject.isNotEmpty()) {
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

    fun getProposalSuggestion(proposal: String = "") {
        viewModelScope.launch {
            val proposalSuggestion = if (proposal.isNotEmpty()) {
                pollDataSource.getAllPoll()
                    .map { it.pollConfig.proposals }
                    .flatten()
                    .distinct()
                    .filter { it.contains(other = proposal, ignoreCase = true) }
            } else {
                emptyList()
            }
            _pollSetupViewState.update {
                it.copy(proposalSuggestion = proposalSuggestion)
            }
        }
    }

    fun clearSubjectSuggestion() {
        _pollSetupViewState.update {
            it.copy(subjectSuggestion = emptyList())
        }
    }

    fun clearProposalSuggestion() {
        _pollSetupViewState.update {
            it.copy(proposalSuggestion = emptyList())
        }
    }
}