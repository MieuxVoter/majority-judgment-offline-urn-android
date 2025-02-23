package com.illiouchine.jm.model

data class Poll(
    val pollConfig: PollConfig,
    val ballots: List<Ballot>,
) {
    val judgments: List<Judgment>
        get() = getAllJudgments()

    // TODO: memoize and invalidate on ballots change
    private fun getAllJudgments(): List<Judgment> {
        val judgments: MutableList<Judgment> = mutableListOf()
        ballots.forEach { ballot -> judgments.addAll(ballot.judgments) }

        return judgments
    }

    fun withBallot(ballot: Ballot): Poll {
        return Poll(
            pollConfig = pollConfig,
            ballots = ballots + ballot,
        )
//        ballots = ballots + ballot
    }

}
