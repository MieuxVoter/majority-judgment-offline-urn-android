package com.illiouchine.jm.logic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Destination
import com.illiouchine.jm.ui.Navigator2
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.mapType
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PollResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val navigator: Navigator2,
) : ViewModel() {

    private val pollResult = savedStateHandle.toRoute<Screens.PollResult>(Screens.PollResult.mapType())

    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: TallyInterface? = null,
        val result: ResultInterface? = null,
    )

    private val _pollResultViewState = MutableStateFlow<PollResultViewState>(initializeState(pollResult.poll))
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    private fun initializeState(poll: Poll) : PollResultViewState {
        val amountOfProposals = poll.pollConfig.proposals.size
        val amountOfGrades = poll.pollConfig.grading.getAmountOfGrades()
        val deliberation: DeliberatorInterface = MajorityJudgmentDeliberator()
        val tally = CollectedTally(amountOfProposals, amountOfGrades)

        poll.pollConfig.proposals.forEachIndexed { proposalIndex, _ ->
            val voteResult = poll.judgments.filter { it.proposal == proposalIndex }
            voteResult.forEach { judgment ->
                tally.collect(proposalIndex, judgment.grade)
            }
        }

        val result: ResultInterface = deliberation.deliberate(tally)

        return PollResultViewState(
            poll = poll,
            tally = tally,
            result = result
        )
    }

    fun returnHome(){
        viewModelScope.launch {
            navigator.navigate(Destination.Home)
        }
    }
}