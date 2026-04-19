package com.illiouchine.jm.ui.composable.plot.utils

import com.illiouchine.jm.service.ProximityAnalysis
import java.lang.Integer.min

fun filterAnalysis(
    analysis: ProximityAnalysis,
    proposalsIndices: List<Int>? = null,
    maxAmountOfProposalsThatFit: Int = 16,
): ProximityAnalysis {
    val allProposalsIndices = 0.rangeUntil(analysis.proposals.size).toList()
    val lotsOfProposalsIndices = proposalsIndices ?: allProposalsIndices
    val maxAmountOfProposals = min(maxAmountOfProposalsThatFit, lotsOfProposalsIndices.size)
    val usedProposalsIndices = lotsOfProposalsIndices.take(maxAmountOfProposals)

    return analysis.filterByProposalsIndices(usedProposalsIndices)
}
