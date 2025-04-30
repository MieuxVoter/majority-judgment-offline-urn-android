package com.illiouchine.jm.service

import android.util.Log
import com.illiouchine.jm.model.Ballot
import kotlin.math.max

/**
 * See https://github.com/MieuxVoter/majority-judgment-offline-urn-android/issues/93#issuecomment-2833389296
 *
 * This code can be heavily optimized ; this is an initial draft.
 * The goal for now is to be as explicit and clear as possible.  Optimization will come later.
 */
class GaugeHands {

    fun computeProportionalRepresentation(
        ballots: List<Ballot>,
//        amountOfGrades: Int,
        acceptationGradeThreshold: Int,
    ): List<Double> {

        if (ballots.isEmpty()) {
            return emptyList()
        }
        val amountOfProposals = ballots.first().judgments.size
        val amountOfBallots = ballots.size

        ballots.forEach { ballot ->
            if (ballot.judgments.size != amountOfProposals) {
                throw UnbalancedBallotsException()
            }
        }

        // Step 1: For each proposal, count the positive judgments they received
        //         that were the highest judgments of their respective ballots.
        val favoritism = List<Int>(
            size = amountOfProposals,
            init = { proposalIndex ->
                var points = 0

                ballots.forEach { ballot ->
                    var highestGradeGiven = 0
                    var proposalGrade = 0
                    ballot.judgments.forEach { judgment ->
                        highestGradeGiven = max(highestGradeGiven, judgment.grade)
                        if (judgment.proposal == proposalIndex) {
                            proposalGrade = judgment.grade
                        }
                    }
                    if (highestGradeGiven >= acceptationGradeThreshold) {
                        if (proposalGrade == highestGradeGiven) {
                            points++
                        }
                    }
                }

                points
            },
        )

        // Step 2: Establish an initial proportional representation using only that data.
        var totalFavoritism = 0
        favoritism.forEach {
            totalFavoritism += it
        }
        val proportions = List<Double>(
            size = amountOfProposals,
            init = {
                favoritism[it].toDouble() / totalFavoritism.toDouble()
            },
        )

        // Step 3: Sort the proposals by this initial representation (increasingly).
        val proposals = (0..<amountOfProposals)
        val sortedProposals = proposals.sortedBy {
            favoritism[it]
        }

        Log.i("MJ", String.format(" favoritism: %s", favoritism))
        Log.i("MJ", String.format(" proportions: %s", proportions))
        Log.i("MJ", String.format(" sortedProposals: %s", sortedProposals))

        // Step 4: For each pair of adjacent candidates, compute the new gauge hand.
        var gaugeHandCursor = 0.0
        val initialGaugeHands = List<Double>(
            size = amountOfProposals - 1,
            init = { gaugeIndex ->
                gaugeHandCursor += proportions[sortedProposals[gaugeIndex]]
                gaugeHandCursor
            },
        )

        Log.i("MJ", String.format(" initialGaugeHands: %s", initialGaugeHands))

        val adjustedGaugeHands = List<Double>(
            size = amountOfProposals - 1,
            init = { gaugeIndex ->
                val lowerProposalIndex = sortedProposals[gaugeIndex]
                val higherProposalIndex = sortedProposals[gaugeIndex + 1]

                var preferenceForLower = 0
                var preferenceForHigher = 0
                ballots.forEach { ballot ->
                    val lowerProposalGrade = ballot.gradeOf(lowerProposalIndex)
                    val higherProposalGrade = ballot.gradeOf(higherProposalIndex)
                    val highestGrade = ballot.getHighestGrade()
                    val shouldCount = (
                            lowerProposalGrade < highestGrade
                                    &&
                                    higherProposalGrade < highestGrade
                            ) || highestGrade < acceptationGradeThreshold

                    preferenceForLower += if (
                        shouldCount
                        &&
                        lowerProposalGrade > higherProposalGrade
                    ) {
                        1
                    } else {
                        0
                    }

                    preferenceForHigher += if (
                        shouldCount
                        &&
                        lowerProposalGrade < higherProposalGrade
                    ) {
                        1
                    } else {
                        0
                    }
                }

                val delta = (preferenceForLower - preferenceForHigher).toDouble() / amountOfBallots

                Log.i(
                    "MJ", String.format(
                        """
gaugeIndex: %s
    preferenceForLower: %s
    preferenceForHigher: %s
    delta: %s
                """.trimIndent(),
                        gaugeIndex,
                        preferenceForLower,
                        preferenceForHigher,
                        delta,
                    )
                )

                var adjustedGaugeHand = initialGaugeHands[gaugeIndex]
                if (delta < 0) {
                    adjustedGaugeHand += delta * proportions[lowerProposalIndex]
                } else {
                    adjustedGaugeHand += delta * proportions[higherProposalIndex]
                }

                adjustedGaugeHand
            },
        )

        Log.i("MJ", String.format(" adjustedGaugeHands: %s", adjustedGaugeHands))

        // Step 5: From the new positions of all the gauges hands G′, derive the new proportions P′.
        gaugeHandCursor = 0.0
        val finalGaugeHands = List<Double>(
            size = amountOfProposals - 1,
            init = {
                gaugeHandCursor = max(gaugeHandCursor, adjustedGaugeHands[it])
                gaugeHandCursor
            },
        )

        Log.i("MJ", String.format(" finalGaugeHands: %s", finalGaugeHands))

        val sortedProportions = List<Double>(
            size = amountOfProposals,
            init = { sortedProposalIndex ->
                if (sortedProposalIndex == 0) {
                    finalGaugeHands[sortedProposalIndex]
                } else if (sortedProposalIndex < amountOfProposals - 1) {
                    finalGaugeHands[sortedProposalIndex] - finalGaugeHands[sortedProposalIndex - 1]
                } else {
                    1.0 - finalGaugeHands[sortedProposalIndex - 1]
                }
            },
        )

        Log.i("MJ", String.format(" sortedProportions: %s", sortedProportions))

        val finalProportions = List<Double>(
            size = amountOfProposals,
            init = {
                sortedProportions[sortedProposals.indexOf(it)]
            },
        )

        // Step 5: Done.
        return finalProportions
    }
}

class UnbalancedBallotsException : Exception()

