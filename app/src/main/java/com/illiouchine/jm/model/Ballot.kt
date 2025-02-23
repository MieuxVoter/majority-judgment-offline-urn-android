package com.illiouchine.jm.model

data class Ballot(
    val judgments: List<Judgment> = emptyList(),
) {
    // hmmm…
    fun withJudgment(judgment: Judgment): Ballot {
        return Ballot(
            judgments = judgments + judgment,
        )
    }

    // HMMM………
    fun withoutLastJudgment(): Ballot {
        return Ballot(
            judgments = judgments.subList(0, judgments.size - 1),
        )
    }
}
