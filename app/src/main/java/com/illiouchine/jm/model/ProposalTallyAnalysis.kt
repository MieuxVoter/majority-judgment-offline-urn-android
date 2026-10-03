package com.illiouchine.jm.model

import androidx.compose.runtime.Stable
import com.ionspin.kotlin.bignum.integer.BigInteger
import fr.mieuxvoter.kmj.analysis.CandidateTallyAnalysis

@Stable
data class ProposalTallyAnalysis(
    val medianGrade: Int,
    val totalSize: BigInteger,
)

fun CandidateTallyAnalysis.toAnalysis(): ProposalTallyAnalysis {
    return ProposalTallyAnalysis(
        medianGrade = this.medianGrade,
        totalSize = this.totalSize,
    )
}
