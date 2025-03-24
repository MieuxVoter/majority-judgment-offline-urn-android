package com.illiouchine.jm.logic

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
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
        val config: PollConfig = PollConfig(),
        val subjectSuggestion: List<String> = emptyList(),
        val proposalSuggestion: List<String> = emptyList(),
        @StringRes val feedback: Int? = null,
    )

    private val _pollSetupViewState = MutableStateFlow(PollSetupViewState())
    val pollSetupViewState: StateFlow<PollSetupViewState> = _pollSetupViewState

    fun initialize(pollId: Int = 0) {
        viewModelScope.launch {
            val poll = pollDataSource.getPollById(pollId = pollId)
            val initialPoll =
                poll?.pollConfig ?: PollConfig(grading = sharedPrefsHelper.getDefaultGrading())
            _pollSetupViewState.update {
                it.copy(config = initialPoll)
            }
        }
    }

    fun addSubject(context: Context, subject: String) {
        val newSubject = subject.ifEmpty { generateSubject(context = context) }
        _pollSetupViewState.update {
            it.copy(config = it.config.copy(subject = newSubject))
        }
    }

    fun addProposal(context: Context, proposal: String = "") {
        // Rule: if the proposal name is not specified, use a default
        val notEmptyProposal = proposal.ifEmpty { generateProposalName(context) }
        // Rule: proposals must have unique names
        if (proposalAlreadyExist(notEmptyProposal)) {
            _pollSetupViewState.update {
                it.copy(feedback = R.string.toast_proposal_name_already_exists)
            }
        } else {
            val newProposals = buildList {
                addAll(_pollSetupViewState.value.config.proposals)
                add(notEmptyProposal)
            }
            _pollSetupViewState.update {
                it.copy(
                    config = it.config.copy(proposals = newProposals),
                    proposalSuggestion = emptyList(),
                    subjectSuggestion = emptyList()
                )
            }
        }
    }

    fun removeProposal(proposal: String) {
        val newProposals = _pollSetupViewState.value.config.proposals - proposal
        _pollSetupViewState.update {
            it.copy(config = it.config.copy(proposals = newProposals))
        }
    }

    fun clearFeedback() {
        _pollSetupViewState.update {
            it.copy(feedback = null)
        }
    }

    fun selectGrading(grading: Grading) {
        _pollSetupViewState.update {
            it.copy(config = it.config.copy(grading = grading))
        }
    }

    fun refreshSubjectSuggestion(subject: String = "") {
        viewModelScope.launch {
            val subjectSuggestion = if (subject.isNotEmpty()) {
                val polls = pollDataSource.getAllPolls()
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

    fun refreshProposalSuggestion(proposal: String = "") {
        viewModelScope.launch {
            val proposalSuggestion = if (proposal.isNotEmpty()) {
                pollDataSource.getAllPolls()
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

    fun finishSetup(context: Context) {
        val pollConfig = _pollSetupViewState.value.config.addSubjectIfEmpty(context = context)

        viewModelScope.launch {
            val poll = Poll(
                pollConfig = pollConfig,
                ballots = emptyList(),
            )
            val pollId = pollDataSource.savePoll(poll)
            navigator.navigateTo(Screens.PollVote(id = pollId)) {
                popUpTo(Screens.Home) { inclusive = false }
            }
        }
    }

    private fun generateProposalName(context: Context): String {
        return buildString {
            append(context.getString(R.string.proposal))
            append(" ")
            append((65 + _pollSetupViewState.value.config.proposals.size).toChar())
        }
    }

    private fun generateSubject(context: Context): String {
        return buildString {
            append(context.getString(R.string.poll_of))
            append(" ")
            append(DateFormat.getDateInstance().format(Calendar.getInstance().time))
        }
    }

    private fun PollConfig.addSubjectIfEmpty(context: Context): PollConfig {
        val newPoll = copy(
            subject = subject.ifEmpty { generateSubject(context) }
        )
        return newPoll
    }

    private fun proposalAlreadyExist(proposal: String): Boolean =
        _pollSetupViewState.value.config.proposals.any { it == proposal }

}