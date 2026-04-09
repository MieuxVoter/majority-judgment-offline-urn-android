package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll
import kotlin.math.max
import kotlin.math.sqrt

data class ProximityAnalysis(
    val proposals: List<String>,
    val proximities: List<List<Double>>,
    val minima: List<Double>,
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
        )
    }
}

class ProximityAnalyzer {

    fun analyze(
        poll: Poll,
    ): ProximityAnalysis {
        val proposalsIndices = 0.rangeUntil(poll.pollConfig.proposals.size).toList()

        // We need the maximum squared deviation possible in order to normalize
        val maxDifference = poll.pollConfig.grading.getAmountOfGrades() - 1
        val maxDeviation = sqrt((maxDifference * maxDifference * poll.ballots.size).toDouble())

        val proposals = proposalsIndices.map {
            poll.pollConfig.proposals[it]
        }

        val proximities = proposalsIndices.map { someProposalIndex ->
            proposalsIndices.map { otherProposalIndex ->
                if (maxDeviation == 0.0) { // true iff there are no ballots or only one grade
                    if (someProposalIndex == otherProposalIndex) {
                        1.0
                    } else {
                        0.0
                    }
                } else {
                    // Distance between the two proposals in the ND orthogonal space of the ballots
                    val sqDeviation = sqrt(
                        poll.ballots.sumOf { ballot ->
                            // Assuming grades are linearly distributed, value-wise, here.
                            // MJ ranking does not use grade values, but this analysis does.
                            val someGradeValue = ballot.gradeOf(someProposalIndex)
                            val otherGradeValue = ballot.gradeOf(otherProposalIndex)
                            (someGradeValue - otherGradeValue) * (someGradeValue - otherGradeValue)
                        }.toDouble()
                    )
                    // Normalize over range [-1, +1] with:
                    // +1 being the exact same
                    // ±0 being indistinguishable from random
                    // -1 being the exact opposite
                    (0.5 - sqDeviation / maxDeviation) * 2.0
                }
            }
        }

        val minima = proposalsIndices.map { someProposalIndex ->
            val sqDeviation = sqrt(poll.ballots.sumOf {
                val someGradeValue = it.gradeOf(someProposalIndex)
                val localMaxDifference = max(someGradeValue, maxDifference - someGradeValue)
                localMaxDifference * localMaxDifference
            }.toDouble())
            (0.5 - sqDeviation / maxDeviation) * 2.0
        }

        return ProximityAnalysis(
            proposals = proposals,
            proximities = proximities,
            minima = minima,
        )
    }
}
