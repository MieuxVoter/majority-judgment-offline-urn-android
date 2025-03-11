package com.illiouchine.jm.logic

import android.content.Context
import androidx.lifecycle.ViewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Navigator
import fr.mieuxvoter.mj.CollectedTally
import fr.mieuxvoter.mj.DeliberatorInterface
import fr.mieuxvoter.mj.MajorityJudgmentDeliberator
import fr.mieuxvoter.mj.ParticipantGroup.Type
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

class PollResultViewModel(
    private val navigator: Navigator,
) : ViewModel() {

    data class PollResultViewState(
        val poll: Poll? = null,
        val tally: TallyInterface? = null,
        val result: ResultInterface? = null,
        val explanations: List<String> = emptyList(),
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

        val explanations: MutableList<String> = mutableListOf()
        result.proposalResultsRanked.forEachIndexed { displayIndex, _ ->
            explanations.add(
                generateDuelExplanation(
                    context = context,
                    poll = poll,
                    tally = tally,
                    result = result,
                    baseIndex = displayIndex,
                    otherIndex = if (displayIndex < amountOfProposals - 1) {
                        displayIndex + 1
                    } else {
                        displayIndex - 1
                    },
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

    private fun generateDuelExplanation(
        context: Context,
        poll: Poll,
        tally: TallyInterface,
        result: ResultInterface,
        baseIndex: Int, // profile that was clicked
        otherIndex: Int, // neighbor profile
    ): String {

        val base = result.proposalResultsRanked[baseIndex]
        val other = result.proposalResultsRanked[otherIndex]

        val baseGroups = base.analysis.computeResolution(tally.proposalsTallies[base.index])
        val otherGroups = other.analysis.computeResolution(tally.proposalsTallies[other.index])

        if (base.rank == other.rank) {
            return context.getString(
                R.string.ranking_explain_perfectly_equal,
                poll.pollConfig.proposals[base.index],
                poll.pollConfig.proposals[other.index],
            )
        }

        for (i in (0..<max(baseGroups.size, otherGroups.size))) {
            if (i >= baseGroups.size) {
                val otherGroup = otherGroups[i]
                // The %1$s group of %2$s is the biggest, as %3$s has no more groups.
                return context.getString(
                    R.string.ranking_explain_no_more_groups,
                    if (otherGroup.type == Type.Adhesion) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[other.index],
                    poll.pollConfig.proposals[base.index],
                )
            }
            val baseGroup = baseGroups[i]
            if (i >= otherGroups.size) {
                // The %1$s group of %2$s is the biggest, as %3$s has no more groups.
                return context.getString(
                    R.string.ranking_explain_no_more_groups,
                    if (baseGroup.type == Type.Adhesion) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[base.index],
                    poll.pollConfig.proposals[other.index],
                )
            }
            val otherGroup = otherGroups[i]

            if (baseGroup.type == Type.Median && otherGroup.type == Type.Median) {
                // The median grades are the same, go deeper
                if (baseGroup.grade == otherGroup.grade) {
                    continue
                }
                // Or the median grades are different, and so we already found a winner
                return context.getString(
                    R.string.ranking_explain_different_median,
                    poll.pollConfig.proposals[base.index],
                    context.getString(poll.pollConfig.grading.getGradeName(base.analysis.medianGrade)),
                    if (baseGroup.grade > otherGroup.grade) {
                        context.getString(R.string.higher)
                    } else {
                        context.getString(R.string.lower)
                    },
                    poll.pollConfig.proposals[other.index],
                    context.getString(poll.pollConfig.grading.getGradeName(other.analysis.medianGrade)),
                )
            } else if (baseGroup.size != otherGroup.size) {
                val biggestGroup = if (baseGroup.size > otherGroup.size) {
                    baseGroup
                } else {
                    otherGroup
                }
                val biggest = if (baseGroup.size > otherGroup.size) {
                    base
                } else {
                    other
                }
                // %1$s and %2$s are both %3$s, but the %4$s group of %5$s is bigger.
                return context.getString(
                    R.string.ranking_explain_same_median,
                    poll.pollConfig.proposals[base.index],
                    poll.pollConfig.proposals[other.index],
                    context.getString(poll.pollConfig.grading.getGradeName(base.analysis.medianGrade)),
                    if (biggestGroup.type == Type.Adhesion) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[biggest.index],
                )
            } else if (baseGroup.grade != otherGroup.grade) {

                if (baseGroup.type != otherGroup.type) {
                    // The %1$s group of %2$s is just as big as the %3$s group of %4$s.
                    // Both mean that %5$s should be %6$s than %7$s.
                    return context.getString(
                        R.string.ranking_explain_double_majority,
                        if (baseGroup.type == Type.Adhesion) {
                            context.getString(R.string.adhesion)
                        } else {
                            context.getString(R.string.contestation)
                        },
                        poll.pollConfig.proposals[base.index],
                        if (otherGroup.type == Type.Adhesion) {
                            context.getString(R.string.adhesion)
                        } else {
                            context.getString(R.string.contestation)
                        },
                        poll.pollConfig.proposals[other.index],
                        poll.pollConfig.proposals[base.index],
                        if (base.rank < other.rank) {
                            context.getString(R.string.higher)
                        } else {
                            context.getString(R.string.lower)
                        },
                        poll.pollConfig.proposals[other.index],
                    )
                }

                val bestGroup = if (baseGroup.grade > otherGroup.grade) {
                    baseGroup
                } else {
                    otherGroup
                }

                // The %1$s group of %2$s is %3$s which is %4$s than the %5$s group of %6$s which is %7$s
                return context.getString(
                    R.string.ranking_explain_different_sub_groups,
                    if (baseGroup.type == Type.Adhesion) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[base.index],
                    context.getString(poll.pollConfig.grading.getGradeName(baseGroup.grade)),
                    if (baseGroup == bestGroup) {
                        context.getString(R.string.higher)
                    } else {
                        context.getString(R.string.lower)
                    },
                    if (otherGroup.type == Type.Adhesion) {
                        context.getString(R.string.adhesion)
                    } else {
                        context.getString(R.string.contestation)
                    },
                    poll.pollConfig.proposals[other.index],
                    context.getString(poll.pollConfig.grading.getGradeName(otherGroup.grade)),
                )
            }
        }

        return context.getString(R.string.wip_stay_tuned)
    }
}