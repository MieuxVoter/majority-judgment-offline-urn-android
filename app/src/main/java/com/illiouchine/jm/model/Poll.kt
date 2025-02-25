package com.illiouchine.jm.model

data class Poll(
    val id: Int = 0,
    val pollConfig: PollConfig,
    val ballots: List<Ballot>,
) {
    val judgments: List<Judgment>
        get() = getAllJudgments()

    // make this lazy instead, perhaps ?
    val _judgments: List<Judgment> = collectAllJudgments()

    private fun getAllJudgments(): List<Judgment> {
        return _judgments
    }

    private fun collectAllJudgments(): List<Judgment> {
        val judgments: MutableList<Judgment> = mutableListOf()
        ballots.forEach { ballot -> judgments.addAll(ballot.judgments) }

        return judgments
    }
}
