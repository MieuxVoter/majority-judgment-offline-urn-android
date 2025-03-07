package com.illiouchine.jm.logic

import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PollResultViewModel(
    private val navigator: Navigator,
) : ViewModel() {

    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: TallyInterface? = null,
        val result: ResultInterface? = null,
    )

    private val _pollResultViewState = MutableStateFlow<PollResultViewState>(PollResultViewState())
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    // Todo : Initial method
    fun initializePollResult(poll: Poll) {
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

        _pollResultViewState.update {
            it.copy(
                poll = poll,
                tally = tally,
                result = result,
            )
        }
    }
}