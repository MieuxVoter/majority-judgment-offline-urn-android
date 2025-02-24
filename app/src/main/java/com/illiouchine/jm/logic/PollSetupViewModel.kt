package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.PollConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PollSetupViewModel : ViewModel() {

    data class PollSetupViewState(
        val pollSetup: PollConfig = PollConfig(),
        val feedback: String? = null,
    )

    private val _pollSetupViewState = MutableStateFlow<PollSetupViewState>(PollSetupViewState())
    val pollSetupViewState: StateFlow<PollSetupViewState> = _pollSetupViewState

    fun startPollSetup(
        pollConfig: PollConfig = PollConfig()
    ) {
        // FIXME: for now the navigation is done in the lambda in the Activity -- is it correct ?
        // WGU : yes, the viewModel should not know the navController because they have to separate lifecycle
        _pollSetupViewState.update {
            it.copy(pollSetup = pollConfig)
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
            val newProposals = _pollSetupViewState.value.pollSetup.proposals + proposal

            _pollSetupViewState.update {
                it.copy(pollSetup = it.pollSetup.copy(proposals = newProposals))
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
}