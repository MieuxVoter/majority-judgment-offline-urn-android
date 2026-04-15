package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

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

        //val mean = (poll.pollConfig.grading.getAmountOfGrades() - 1.0) * 0.5
        val origins = proposalsIndices.map { someProposalIndex ->

//            val stdDeviation = sqrt(poll.ballots.sumOf {
//                val someGradeValue = it.gradeOf(someProposalIndex)
//                val stdDifference = mean - someGradeValue
//                stdDifference * stdDifference
//            })

//            val meanDeviation = sqrt(poll.ballots.sumOf {
//                val someGradeValue = it.gradeOf(someProposalIndex)
//                val total = sumTo(someGradeValue) + sumTo(poll.pollConfig.grading.getAmountOfGrades() - 1 - someGradeValue)
//                val meanDifference = total.toDouble() / poll.pollConfig.grading.getAmountOfGrades()
//                meanDifference * meanDifference
//            })

            // FIXME: this is not it ; improve
            val neutralDeviation = sqrt(poll.ballots.sumOf {
                val someGradeValue = it.gradeOf(someProposalIndex)
                val total = poll.pollConfig.grading.grades
                    .mapIndexed { i, _ ->
                        abs(i - someGradeValue)
                    }
                    .reduce { acc, e -> acc + (e * e) }
                // Mean of squares.
                total.toDouble() / poll.pollConfig.grading.getAmountOfGrades()
            })

            (0.5 - neutralDeviation / maxDeviation) * 2.0
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

