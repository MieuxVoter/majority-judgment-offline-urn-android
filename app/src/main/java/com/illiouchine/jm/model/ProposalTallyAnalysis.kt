package com.illiouchine.jm.model

import java.math.BigInteger
import fr.mieuxvoter.mj.ProposalTallyAnalysis as MJProposalTallyAnalysis

data class ProposalTallyAnalysis(
    val medianGrade: Int,
    val totalSize: BigInteger,
)

fun MJProposalTallyAnalysis.toAnalysis(): ProposalTallyAnalysis {
    return ProposalTallyAnalysis(
        medianGrade = this.medianGrade,
        totalSize = this.totalSize
    )
}
