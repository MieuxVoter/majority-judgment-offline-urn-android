package com.illiouchine.jm.logic

import android.content.Context
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.service.DuelAnalyzer
import com.illiouchine.jm.service.ParticipantGroupAnalysis
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
        val explanations: List<String> = emptyList(),
        val groups: List<DuelGroups> = emptyList(),
    )

    data class DuelGroups(
        val groups: List<ParticipantGroupAnalysis>,
    )

    private val _pollResultViewState = MutableStateFlow(PollResultViewState())
    val pollResultViewState: StateFlow<PollResultViewState> = _pollResultViewState

    fun initializePollResult(context: Context, poll: Poll) {
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

        val groups: MutableList<DuelGroups> = mutableListOf()
        val explanations: MutableList<String> = mutableListOf()
        result.proposalResultsRanked.forEachIndexed { displayIndex, _ ->
            val otherIndex = if (displayIndex < amountOfProposals - 1) {
                displayIndex + 1
            } else {
                displayIndex - 1
            }
            val duelAnalyzer = DuelAnalyzer(
                poll = poll,
                tally = tally,
                result = result,
                baseIndex = displayIndex,
                otherIndex = otherIndex,
            )
            explanations.add(
                duelAnalyzer.generateDuelExplanation(
                    context = context,
                )
            )
            groups.add(
                DuelGroups(
                    groups = duelAnalyzer.generateGroups(),
                )
            )
        }

        _pollResultViewState.update {
            it.copy(
                poll = poll,
                tally = tally,
                result = result,
                explanations = explanations,
                groups = groups,
            )
        }
    }
}