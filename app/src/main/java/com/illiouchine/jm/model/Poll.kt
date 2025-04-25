package com.illiouchine.jm.model

import kotlinx.serialization.Serializable

@Serializable
data class Poll(
    val id: Int = 0,
    val pollConfig: PollConfig,
    val ballots: List<Ballot>,
) {
    val judgments: List<Judgment>
        get() = getAllJudgments()

    private val collectedJudgments: List<Judgment> = collectAllJudgments()

    private fun getAllJudgments(): List<Judgment> {
        return collectedJudgments
    }

    private fun collectAllJudgments(): List<Judgment> {
        val judgments: MutableList<Judgment> = mutableListOf()
        ballots.forEach { ballot -> judgments.addAll(ballot.judgments) }

        return judgments
    }
}
