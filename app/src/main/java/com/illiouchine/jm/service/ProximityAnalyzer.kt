package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll
import kotlin.math.max
import kotlin.math.sqrt

//
// Computes how close (in the collective hearts & minds of the judges) pairs of proposals are.
// A proximity of +1 means that the two proposals received exactly the same grades in each ballot.
// A proximity of ~0 is indistinguishable from random (when both are random → not for a given merit).
// A proximity of -1 means that the two proposals received extreme and diametrically opposite grades in each ballot.
// Basically:    proximity = (0.5 - squaredDeviation / maximumDeviation) * 2.0
// This assumes that grades are somewhat linearly distributed, value-wise.
//
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
                    // Distance between the two proposals in the orthonormal N-space of the ballots
                    val sqDeviation = sqrt(
                        poll.ballots.sumOf { ballot ->
                            // Assuming grades are linearly distributed, value-wise, here.
                            // MJ ranking does not use grade values, but this analysis does.
                            val someGradeValue = ballot.gradeOf(someProposalIndex)
                            val otherGradeValue = ballot.gradeOf(otherProposalIndex)
                            (someGradeValue - otherGradeValue) * (someGradeValue - otherGradeValue)
                        }.toDouble()
                    )
                    // Normalize over range [-1, +1]
                    (0.5 - sqDeviation / maxDeviation) * 2.0
                }
            }
        }

        // Compute the minimum proximity achievable with a given merit profile at someProposalIndex
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

        // Precompute this since we're using it multiple times in the loops below.
        val meanOfSquaresPerGrade = List(poll.pollConfig.grading.grades.size) { index ->
            List(poll.pollConfig.grading.grades.size) { i ->
                (i - index) * (i - index)
            }.sum().toDouble() / poll.pollConfig.grading.grades.size
        }

        // Compute the proximity of "indistinguishable from random",
        // for a given merit profile at someProposalIndex.
        val neutrals = proposalsIndices.map { someProposalIndex ->
            val neutralDeviation = sqrt(
                poll.ballots.sumOf { ballot ->
                    meanOfSquaresPerGrade[ballot.gradeOf(someProposalIndex)]
                }
            )
            (0.5 - neutralDeviation / maxDeviation) * 2.0
        }

        return ProximityAnalysis(
            proposals = proposals,
            proximities = proximities,
            minima = minima,
            neutrals = neutrals,
        )
    }
}
