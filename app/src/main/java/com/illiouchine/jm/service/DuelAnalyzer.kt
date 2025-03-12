package com.illiouchine.jm.service

import android.content.Context
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import fr.mieuxvoter.mj.ParticipantGroup
import fr.mieuxvoter.mj.ParticipantGroup.Type
import fr.mieuxvoter.mj.ProposalResultInterface
import fr.mieuxvoter.mj.ResultInterface
import fr.mieuxvoter.mj.TallyInterface
import kotlin.math.max


data class ParticipantGroupAnalysis(
    val participant: Int,
    val group: ParticipantGroup,
    val decisive: Boolean = false,
)

class DuelAnalyzer(
    private val poll: Poll,
    private val tally: TallyInterface,
    private val result: ResultInterface,
    private val baseIndex: Int, // profile that was clicked
    private val otherIndex: Int, // neighbor profile
) {

    private val base: ProposalResultInterface = result.proposalResultsRanked[baseIndex]
    private val other: ProposalResultInterface = result.proposalResultsRanked[otherIndex]

    private val baseGroups: Array<ParticipantGroup> = base.analysis.computeResolution(tally.proposalsTallies[base.index])
    private val otherGroups: Array<ParticipantGroup> = other.analysis.computeResolution(tally.proposalsTallies[other.index])


    fun generateDuelExplanation(
        context: Context,
    ): String {

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

    // There's no logic to this.  Only madness.
    fun generateGroups(): List<ParticipantGroupAnalysis> {
        val groups: MutableList<ParticipantGroupAnalysis> = mutableListOf()

        if (base.rank == other.rank) {
            return groups
        }

        for (i in (0..<max(baseGroups.size, otherGroups.size))) {
            if (i >= baseGroups.size) {
                val otherGroup = otherGroups[i]
                // The %1$s group of other is the biggest, as base has no more groups.
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = otherIndex,
                        group = otherGroup,
                        decisive = true,
                    )
                )
                return groups
            }
            val baseGroup = baseGroups[i]
            if (i >= otherGroups.size) {
                // The %1$s group of base is the biggest, as other has no more groups.
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = baseIndex,
                        group = baseGroup,
                        decisive = true,
                    )
                )
                return groups
            }
            val otherGroup = otherGroups[i]

            if (baseGroup.type == Type.Median || otherGroup.type == Type.Median) {
                if (baseGroup.type != Type.Median || otherGroup.type != Type.Median) {
                    continue // we should raise here, as something is VERY WRONG
                }
                // The median grades are the same, go deeper
                if (baseGroup.grade == otherGroup.grade) {
                    continue
                }
                // Or the median grades are different, and so groups don't matter
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = baseIndex,
                        group = baseGroup,
                        decisive = (baseGroup.grade >= otherGroup.grade),
                    )
                )
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = otherIndex,
                        group = otherGroup,
                        decisive = (baseGroup.grade <= otherGroup.grade),
                    )
                )
                return groups

            } else if (baseGroup.size != otherGroup.size) {
                val biggestIndex = if (baseGroup.size > otherGroup.size) {
                    baseIndex
                } else {
                    otherIndex
                }
                val biggestGroup = if (baseGroup.size > otherGroup.size) {
                    baseGroup
                } else {
                    otherGroup
                }
                // %1$s and %2$s are both %3$s, but the %4$s group of %5$s is bigger.
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = biggestIndex,
                        group = biggestGroup,
                        decisive = true,
                    )
                )
                return groups

            } else if (baseGroup.grade != otherGroup.grade) {

                if (baseGroup.type != otherGroup.type) {
                    // The %1$s group of %2$s is just as big as the %3$s group of %4$s.
                    // Both mean that %5$s should be %6$s than %7$s.
                    groups.add(
                        ParticipantGroupAnalysis(
                            participant = baseIndex,
                            group = baseGroup,
                            decisive = true,
                        )
                    )
                    groups.add(
                        ParticipantGroupAnalysis(
                            participant = otherIndex,
                            group = otherGroup,
                            decisive = true,
                        )
                    )
                    return groups
                }

                // The %1$s group of %2$s is %3$s which is %4$s than the %5$s group of %6$s which is %7$s
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = baseIndex,
                        group = baseGroup,
                        decisive = (baseGroup.grade >= otherGroup.grade),
                    )
                )
                groups.add(
                    ParticipantGroupAnalysis(
                        participant = otherIndex,
                        group = otherGroup,
                        decisive = (baseGroup.grade <= otherGroup.grade),
                    )
                )
                return groups
            }
        }

        return groups
    }

}