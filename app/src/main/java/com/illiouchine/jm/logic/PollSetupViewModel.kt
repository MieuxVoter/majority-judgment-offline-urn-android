package com.illiouchine.jm.logic

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.illiouchine.jm.R
import com.illiouchine.jm.data.PollDataSource
import com.illiouchine.jm.data.PollTemplateDataSource
import com.illiouchine.jm.data.SharedPrefsHelper
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.navigator.NavigationAction
import com.illiouchine.jm.ui.navigator.Screens
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

class PollSetupViewModel(
    private val sharedPrefs: SharedPrefsHelper,
    private val pollDataSource: PollDataSource,
    private val pollTemplateDataSource: PollTemplateDataSource,
    application: Application,
) : AndroidViewModel(application) {

    @Stable
    data class PollSetupViewState(
        val config: PollConfig = PollConfig(),
        val subjectSuggestion: List<String> = emptyList(),
        val proposalSuggestion: List<String> = emptyList(),
        @param:StringRes val feedback: Int? = null,
    )

    private val _pollSetupViewState = MutableStateFlow(PollSetupViewState())
    val pollSetupViewState: StateFlow<PollSetupViewState> = _pollSetupViewState
    private var lastId: Int? = null

    private val _navEvents = MutableSharedFlow<NavigationAction>()
    val navEvents = _navEvents.asSharedFlow()

    fun initialize(cloneablePollId: Int = 0, pollTemplateSlug: String = "") {
        viewModelScope.launch {
            when (cloneablePollId) {
                0 -> {
                    _pollSetupViewState.update {
                        var newConfig = PollConfig(grading = sharedPrefs.getDefaultGrading())
                        if ("" != pollTemplateSlug) {
                            newConfig = pollTemplateDataSource.getBySlug(
                                slug = pollTemplateSlug,
                                context = getApplication<Application>().applicationContext,
                            )
                        }
                        it.copy(config = newConfig)
                    }
                }
                lastId -> {
                    /* Do nothing : Reload from configuration change */
                }
                else -> {
                    val poll = pollDataSource.getPollById(pollId = cloneablePollId)
                    val initialPollConfig =
                        poll?.pollConfig ?: PollConfig(grading = sharedPrefs.getDefaultGrading())

                    _pollSetupViewState.update {
                        it.copy(config = initialPollConfig)
                    }
                }
            }
            lastId = cloneablePollId
        }
    }

    fun addSubject(@Suppress("unused") context: Context, subject: String) {
        _pollSetupViewState.update {
            it.copy(config = it.config.copy(subject = subject))
        }
    }

    fun addProposal(context: Context, proposalName: String = "") {
        // Rule: if the proposal name is not specified, use a default
        val notEmptyProposalName = proposalName.ifEmpty { generateProposalName(context) }
        // Rule: proposals must have unique names
        if (doesProposalAlreadyExist(notEmptyProposalName)) {
            _pollSetupViewState.update {
                it.copy(feedback = R.string.toast_proposal_name_already_exists)
            }
        } else {
            val newProposals = buildList {
                addAll(_pollSetupViewState.value.config.proposals)
                add(notEmptyProposalName)
            }
            _pollSetupViewState.update {
                it.copy(
                    config = it.config.copy(proposals = newProposals),
                    proposalSuggestion = emptyList(),
                    subjectSuggestion = emptyList(),
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
            _navEvents.emit(NavigationAction.ClearTo(Screens.PollVote(id = pollId)))
        }
        // Reset poll id
        lastId = null
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
        return copy(
            subject = subject.ifEmpty { generateSubject(context) }
        )
    }

    private fun doesProposalAlreadyExist(proposal: String): Boolean =
        _pollSetupViewState.value.config.proposals.any { it == proposal }
}
