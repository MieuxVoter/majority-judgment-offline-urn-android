package com.illiouchine.jm.model

import fr.mieuxvoter.mj.ProposalTallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.math.BigInteger

data class ProposalTally(
    val tally: ImmutableList<BigInteger>,
    val amountOfJudgments: BigInteger,
)

fun ProposalTallyInterface.toProposalTally(): ProposalTally {
    return ProposalTally(
        tally = this.tally.toPersistentList(),
        amountOfJudgments = this.amountOfJudgments
    )
}
