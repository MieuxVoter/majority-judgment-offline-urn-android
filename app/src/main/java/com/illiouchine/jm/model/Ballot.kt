package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import kotlin.math.max

@Stable
@Serializable
data class Ballot(
    val judgments: ImmutableList<Judgment> = emptyList<Judgment>().toPersistentList(),
) {

    fun withJudgment(judgment: Judgment): Ballot {
        return Ballot(
            judgments = (judgments + judgment).toPersistentList(),
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
