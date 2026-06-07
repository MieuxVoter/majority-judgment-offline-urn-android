package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import com.illiouchine.jm.model.serializer.UUIDSerializer
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.math.max

@Stable
@Serializable
data class Ballot(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID? = null,
    val judgments: List<Judgment> = emptyList(),
) {

    fun withJudgment(judgment: Judgment): Ballot {
        return copy(
            judgments = (judgments + judgment).toPersistentList(),
        )
    }

    fun withoutLastJudgment(): Ballot {
        return copy(
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

    fun getNuance(): Int {
        return judgments.map { it.grade }.toSet().size
    }

    fun countProposalsWithGrade(grade: Int): Int {
        return judgments.sumOf {
            if (it.grade == grade) { 1 } else { 0 }
        }
    }
}
