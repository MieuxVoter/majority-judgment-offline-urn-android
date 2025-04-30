package com.illiouchine.jm.model

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class Ballot(
    val judgments: List<Judgment> = emptyList(),
) {

    fun withJudgment(judgment: Judgment): Ballot {
        return Ballot(
            judgments = judgments + judgment,
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
        return judgments.indexOfFirst { judgment ->
            judgment.proposal == proposalIndex
        }
    }

    fun getHighestGrade(): Int {
        return judgments.fold(0) { highestGrade, judgment ->
            max(judgment.grade, highestGrade)
        }
    }
}
