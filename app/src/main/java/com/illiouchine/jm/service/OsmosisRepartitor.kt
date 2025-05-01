package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll

/**
 * Computes a proportional representation of the proposals/candidates.
 * The returned list of proportions is normalized, that is its sum will always be 1.
 *
 * This code can be heavily optimized ; this is an initial draft.
 * The goal for now is to be as explicit and clear as possible.  Optimization will come later.
 *
 * Qualities:
 * - robust: disincentivizes polarized voting (ie. "cheating"), just like MJ
 * - holistic: uses all cast judgments, one way or another
 * - deterministic: no random
 * - exact: no approximation
 *
 * Known Issues:
 * - not isomorphic with MJ ; can be mitigated using the DecreasingConstrictor
 * - requires access to the individual ballots and not just the merit profiles (/!\ coercion /!\)
 * - might be ever so slightly numerically unstable since we're handling floats
 */
class OsmosisRepartitor {

    fun computeProportionalRepresentation(poll: Poll): List<Double> {

        val ballots = poll.ballots

        if (ballots.isEmpty()) {
            return emptyList()
        }

        val amountOfProposals = ballots.first().judgments.size
        val amountOfBallots = ballots.size
        val acceptationGradeThreshold = poll.pollConfig.grading.acceptationThreshold

        // Step 1: For each proposal, count the positive judgments they received
        //         that were the highest judgments of their respective ballots.
        val favoritism: List<Int> = List(
            size = amountOfProposals,
            init = { proposalIndex ->
                ballots.fold(0) { points, ballot ->
                    val proposalGrade = ballot.gradeOf(proposalIndex)
                    val highestGradeGiven = ballot.getHighestGrade()
                    val isBallotRejection = highestGradeGiven < acceptationGradeThreshold
                    points + if (!isBallotRejection && proposalGrade == highestGradeGiven) {
                        1
                    } else {
                        0
                    }
                }
            },
        )

        // Step 2: Establish an initial proportional representation using only the favoritism data.
        val totalFavoritism = favoritism.fold(0) { total, value -> total + value }
        val initialProportions: List<Double> = List(
            size = amountOfProposals,
            init = {
                favoritism[it].toDouble() / totalFavoritism.toDouble()
            },
        )

        val finalProportions: MutableList<Double> = MutableList(
            size = amountOfProposals,
            init = {
                initialProportions[it]
            },
        )

        // Step 3: For each pair of proposals, seep representation by osmosis.
        for (proposalIndexA in (0..<amountOfProposals - 1)) {
            for (proposalIndexB in (proposalIndexA + 1..<amountOfProposals)) {

                var preferenceForA = 0
                var preferenceForB = 0

                for (ballot in ballots) {
                    val proposalGradeA = ballot.gradeOf(proposalIndexA)
                    val proposalGradeB = ballot.gradeOf(proposalIndexB)
                    val highestGrade = ballot.getHighestGrade()
                    val isBallotFavoritism = proposalGradeA == highestGrade
                            || proposalGradeB == highestGrade
                    val isBallotRejection = highestGrade < acceptationGradeThreshold

                    if (isBallotFavoritism && !isBallotRejection) {
                        continue
                    }
                    if (proposalGradeA > proposalGradeB) {
                        preferenceForA++
                    }
                    if (proposalGradeA < proposalGradeB) {
                        preferenceForB++
                    }
                }

                val seepingIntent: Double = (
                        (preferenceForB - preferenceForA).toDouble()
                                /
                                (amountOfBallots * (amountOfProposals - 1))
                        )
                var seepingAmount = 0.0
                if (seepingIntent > 0) {
                    seepingAmount = initialProportions[proposalIndexA] * seepingIntent
                }
                if (seepingIntent < 0) {
                    seepingAmount = initialProportions[proposalIndexB] * seepingIntent
                }
                finalProportions[proposalIndexB] += seepingAmount
                finalProportions[proposalIndexA] -= seepingAmount

//                Log.i(
//                    "MJ", """
//                    A = ${proposalIndexA}    B = ${proposalIndexB}
//                    PA = ${preferenceForA}  PB = ${preferenceForB}
//                    delta = ${seepingIntent}
//                    seeping = ${seepingAmount}
//                """.trimIndent()
//                )
            }
        }

        // Step 4: Done.
        return finalProportions
    }
}
