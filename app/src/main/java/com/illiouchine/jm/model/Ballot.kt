package com.illiouchine.jm.model

data class Ballot(
    val judgments: List<Judgment> = emptyList(),
) {
    fun withJudgment(judgment: Judgment): Ballot {
        return Ballot(
            judgments = judgments + judgment,
        )
    }
}
