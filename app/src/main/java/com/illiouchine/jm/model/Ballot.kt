package com.illiouchine.jm.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class Ballot(
    val judgments: ImmutableList<Judgment> = emptyList<Judgment>().toImmutableList(),
) {

    fun withJudgment(judgment: Judgment): Ballot {
        return Ballot(
            judgments = (judgments + judgment).toImmutableList(),
        )
    }

    fun withoutLastJudgment(): Ballot {
        return Ballot(
            judgments = judgments.subList(0, judgments.size - 1),
        )
    }

    fun isAlreadyCast(judgment: Judgment): Boolean {
        return judgments.any { it.proposal == judgment.proposal }
    }

    fun gradeOf(proposalIndex: Int): Int {
        return judgments.find { judgment ->
            judgment.proposal == proposalIndex
        }!!.grade
    }

    fun getHighestGrade(): Int {
        return judgments.fold(0) { highestGrade, judgment ->
            max(judgment.grade, highestGrade)
        }
    }
}
