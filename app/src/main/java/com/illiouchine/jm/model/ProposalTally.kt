package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import com.ionspin.kotlin.bignum.integer.BigInteger
import fr.mieuxvoter.kmj.tally.CandidateTallyInterface
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class ProposalTally(
    val tally: ImmutableList<BigInteger>,
//    val amountOfJudgments: BigInteger,
)

fun CandidateTallyInterface.toProposalTally(): ProposalTally {
    return ProposalTally(
        tally = this.gradesTallies.toPersistentList(),
    )
}
