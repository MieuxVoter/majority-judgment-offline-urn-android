package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll

/**
 * This code can be heavily optimized ; this is an initial draft.
 * The goal for now is to be as explicit and clear as possible.  Optimization will come later.
 *
 * - robust: disincentivizes polarized voting (ie. "cheating"), just like MJ
 * - holistic: uses all judgments, one way or another
 * - deterministic: no random
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

        // Not sure whether we should check this here ; the algo will already crash if unbalanced
//        ballots.forEach { ballot ->
//            if (ballot.judgments.size != amountOfProposals) {
//                throw UnbalancedBallotsException()
//            }
//        }

        // Step 1: For each proposal, count the positive judgments they received
        //         that were the highest judgments of their respective ballots.
        val favoritism: List<Int> = List(
            size = amountOfProposals,
            init = { proposalIndex ->
                var points = 0

                ballots.forEach { ballot ->
                    val proposalGrade = ballot.gradeOf(proposalIndex)
                    val highestGradeGiven = ballot.getHighestGrade()
                    if (highestGradeGiven >= acceptationGradeThreshold) {
                        if (proposalGrade == highestGradeGiven) {
                            points++
                        }
                    }
                }

                points
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
                    val shouldCount = (!isBallotFavoritism) || isBallotRejection

//                    Log.i(
//                        "MJ", """
//                        Ballot should count = ${shouldCount} ; favorite = ${isBallotFavoritism} ; rejection = ${isBallotRejection}
//                    """.trimIndent()
//                    )

                    if (!shouldCount) {
                        continue
                    }
                    if (proposalGradeA > proposalGradeB) {
                        preferenceForA++
                    }
                    if (proposalGradeA < proposalGradeB) {
                        preferenceForB++
                    }
                }

                val delta: Double = (
                        (preferenceForB - preferenceForA).toDouble()
                                /
                                (amountOfBallots * (amountOfProposals - 1))
                        )
                var seepingAmount = 0.0
                if (delta > 0) {
                    seepingAmount = initialProportions[proposalIndexA] * delta
                }
                if (delta < 0) {
                    seepingAmount = initialProportions[proposalIndexB] * delta
                }
                finalProportions[proposalIndexB] += seepingAmount
                finalProportions[proposalIndexA] -= seepingAmount

//                Log.i(
//                    "MJ", """
//                    A = ${proposalIndexA}    B = ${proposalIndexB}
//                    PA = ${preferenceForA}  PB = ${preferenceForB}
//                    delta = ${delta}
//                    seeping = ${seepingAmount}
//                """.trimIndent()
//                )
            }
        }

        // Step 4: Done.
        return finalProportions
    }
}
