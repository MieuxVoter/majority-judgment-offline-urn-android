package com.illiouchine.jm.logic

import android.content.Context
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ProposalResultInterface
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
    )

    private val _pollResultViewState = MutableStateFlow<PollResultViewState>(PollResultViewState())
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

        val explanations: MutableList<String> = mutableListOf()
        result.proposalResultsRanked.forEachIndexed { displayIndex, proposalResult ->
            val neighborProposalResult = result.proposalResultsRanked[
                if (displayIndex < amountOfProposals - 1) {
                    displayIndex + 1
                } else {
                    displayIndex - 1
                }
            ]

            explanations.add(
                generateDuelExplanation(
                    context = context,
                    poll = poll,
                    base = proposalResult,
                    other = neighborProposalResult,
                )
            )
        }

        _pollResultViewState.update {
            it.copy(
                poll = poll,
                tally = tally,
                result = result,
                explanations = explanations,
            )
        }
    }

    fun generateDuelExplanation(
        context: Context,
        poll: Poll,
        base: ProposalResultInterface,
        other: ProposalResultInterface,
    ): String {

        if (base.rank == other.rank) {
            return context.getString(
                R.string.ranking_explain_perfectly_equal,
                poll.pollConfig.proposals[base.index],
                poll.pollConfig.proposals[other.index],
            )
        } else if (base.rank < other.rank && base.analysis.medianGrade > other.analysis.medianGrade) {
            return context.getString(
                R.string.ranking_explain_higher_median,
                poll.pollConfig.proposals[base.index],
                context.getString(poll.pollConfig.grading.getGradeName(base.analysis.medianGrade)),
                poll.pollConfig.proposals[other.index],
                context.getString(poll.pollConfig.grading.getGradeName(other.analysis.medianGrade)),
            )
        } else if (base.rank < other.rank && base.analysis.medianGrade == other.analysis.medianGrade) {
            if (base.analysis.secondMedianGroupSize > other.analysis.secondMedianGroupSize) {
                return context.getString(
                    R.string.ranking_explain_same_median,
                    poll.pollConfig.proposals[base.index],
                    poll.pollConfig.proposals[other.index],
                    context.getString(poll.pollConfig.grading.getGradeName(base.analysis.medianGrade)),
                    if (base.analysis.secondMedianGroupSign >= 0) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[base.index],
                )
            } else if (base.analysis.secondMedianGroupSize < other.analysis.secondMedianGroupSize) {
                return context.getString(
                    R.string.ranking_explain_same_median,
                    poll.pollConfig.proposals[base.index],
                    poll.pollConfig.proposals[other.index],
                    context.getString(poll.pollConfig.grading.getGradeName(base.analysis.medianGrade)),
                    if (other.analysis.secondMedianGroupSign >= 0) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[other.index],
                )
            }
        }

        return context.getString(R.string.wip_stay_tuned)
    }
}