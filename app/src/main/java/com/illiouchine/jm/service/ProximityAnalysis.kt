package com.illiouchine.jm.service

import androidx.compose.runtime.Stable

@Stable
data class ProximityAnalysis(
    val proposals: List<String>, // aka. candidates
    val proximities: List<List<Double>>, // for each proposal, proximities to every other proposal
    val minima: List<Double>, // minimum proximity possible for each merit profile
    val neutrals: List<Double>, // relative origins (indistinguishable from random)
) {
    fun filterByProposalsIndices(indicesToKeep: List<Int>): ProximityAnalysis {
        return ProximityAnalysis(
            proposals = indicesToKeep.map { proposals[it] },
            proximities = indicesToKeep.map { i ->
                indicesToKeep.map { j ->
                    proximities[i][j]
                }
            },
            minima = indicesToKeep.map { minima[it] },
            neutrals = indicesToKeep.map { neutrals[it] },
        )
    }
}
