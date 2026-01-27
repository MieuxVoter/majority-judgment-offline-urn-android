package com.illiouchine.jm.model

import fr.mieuxvoter.mj.ProposalTallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.math.BigInteger

data class ProposalTally(
    val tally: ImmutableList<BigInteger>,
    val amountOfJudgments: BigInteger,
)

fun ProposalTallyInterface.toProposalTally(): ProposalTally {
    return ProposalTally(
        tally = this.tally.toImmutableList(),
        amountOfJudgments = this.amountOfJudgments
    )
}
