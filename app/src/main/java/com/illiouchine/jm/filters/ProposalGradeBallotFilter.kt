package com.illiouchine.jm.filters

import com.illiouchine.jm.model.Poll

class ProposalGradeBallotFilter(
    val proposalIndex: Int,
    val gradeIndex: Int,
) : BallotsFilterInterface {

    override fun filter(poll: Poll): Poll {
        return poll.copy(
            ballots = poll.ballots.filter {
                it.gradeOf(proposalIndex) == gradeIndex
            },
        )
    }

}
