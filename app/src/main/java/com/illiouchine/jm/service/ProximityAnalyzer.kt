package com.illiouchine.jm.service

import androidx.compose.runtime.Stable
import com.illiouchine.jm.model.Poll
import kotlin.math.max
import kotlin.math.sqrt

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

class ProximityAnalyzer {

    fun analyze(
        poll: Poll,
    ): ProximityAnalysis {
        val allProposalsIndices = 0.rangeUntil(poll.pollConfig.proposals.size).toList()
        val proposalsIndices = allProposalsIndices // we're going to have an override parameter

        // We compute the maximum possible deviation in order to normalize later on.
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
                    // ±0 being indistinguishable from random (when both merit profiles are random)
                    // -1 being the exact opposite
                    (0.5 - sqDeviation / maxDeviation) * 2.0
                }
            }
        }

        val minima = proposalsIndices.map { someProposalIndex ->
            val sqDeviation = sqrt(
                poll.ballots.sumOf {
                    val someGradeValue = it.gradeOf(someProposalIndex)
                    val localMaxDifference = max(someGradeValue, maxDifference - someGradeValue)
                    localMaxDifference * localMaxDifference
                }.toDouble()
            )
            (0.5 - sqDeviation / maxDeviation) * 2.0
        }

        // We're supposed here to compute the proximity of "indistinguishable from random",
        // for a given merit profile at someProposalIndex.
        // 1. Brute force scales exponentially with the ballots, so we can't do that
        // 2. We can probably approximate somewhat OK using the scalar merit and some fitting
        // 3. We need to be cleverer, approximation is a last resort
        val origins = proposalsIndices.map { someProposalIndex ->

//            val mean = (poll.pollConfig.grading.getAmountOfGrades() - 1.0) * 0.5
//            val stdDeviation = sqrt(poll.ballots.sumOf {
//                val someGradeValue = it.gradeOf(someProposalIndex)
//                val stdDifference = mean - someGradeValue
//                stdDifference * stdDifference
//            })
//            (0.5 - stdDeviation / maxDeviation) * 2.0

//            val meanDeviation = sqrt(poll.ballots.sumOf {
//                val someGradeValue = it.gradeOf(someProposalIndex)
//                val total = sumTo(someGradeValue) +
//                        sumTo(poll.pollConfig.grading.getAmountOfGrades() - 1 - someGradeValue)
//                val meanDifference = total.toDouble() / poll.pollConfig.grading.getAmountOfGrades()
//                meanDifference * meanDifference
//            })
//            (0.5 - meanDeviation / maxDeviation) * 2.0

//            val neutralDeviation = sqrt(poll.ballots.sumOf {
//                val someGradeValue = it.gradeOf(someProposalIndex)
//                val total = poll.pollConfig.grading.grades
//                    .mapIndexed { i, _ ->
//                        abs(i - someGradeValue)
//                    }
//                    .reduce { acc, e -> acc + (e * e) }
//                total.toDouble() / poll.pollConfig.grading.getAmountOfGrades()
//            })
//            (0.5 - neutralDeviation / maxDeviation) * 2.0

            0.0 // dummy value 'til we git gud
        }

        return ProximityAnalysis(
            proposals = proposals,
            proximities = proximities,
            minima = minima,
            neutrals = origins,
        )
    }
}

/**
 * Sums integers from 0 to n.
 */
fun sumTo(n: Int): Int {
    if (n < 0) return -sumTo(-n)
    return ((n + 1) * n) / 2
}
