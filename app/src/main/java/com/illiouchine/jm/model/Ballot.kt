package com.illiouchine.jm.model

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
}
