package com.illiouchine.jm.service

import com.illiouchine.jm.model.Poll

/**
 * Computes a proportional representation of the proposals (a.k.a. candidates).
 * The returned list of proportions is normalized, meaning that its sum will always be 1.
 *
 * Qualities:
 * - backwards-compatible: identity with uninominal in case of extreme polarization
 * - robust: disincentivizes strategic voting, just like Majority Judgment
 * - holistic: uses all cast judgments, one way or another
 * - deterministic: no random
 * - exact: no approximation
 * - scalable: reasonably computable for billions of judges and hundreds of candidates
 * - simple: elementary arithmetic
 *
 * Known Issues:
 * - not isomorphic with Majority Judgment
 * - not extensively studied
 *
 * Notes:
 * This code can be heavily optimized ; this is an initial draft.
 * The goal for now is to be as explicit and clear as possible.  Optimization will come later.
 */
class OsmosisRepartitor { // PreferentialFavoritismRepartitor?  (still workshopping the name)

    fun computeProportionalRepresentation(poll: Poll): List<Double> {
        val ballots = poll.ballots
        val amountOfProposals = poll.pollConfig.proposals.size

        if (ballots.isEmpty()) {
            return List(size = amountOfProposals) { 0.0 }
        }

        val amountOfBallots = ballots.size
        val acceptationGradeThreshold = poll.pollConfig.grading.acceptationThreshold

        // Step 1: Establish an initial proportional representation using only the favorites,
        //         the proposals that received the highest acceptation grades of each ballot.
        //         This is done in the simple fashion of uninominal voting.
        val favoritism: List<Double> = List(size = amountOfProposals) { proposalIndex ->
            ballots.sumOf { ballot ->
                val proposalGrade = ballot.gradeOf(proposalIndex)
                val highestGradeGiven = ballot.getHighestGrade()
                val isBallotRejection = highestGradeGiven < acceptationGradeThreshold
                if (isBallotRejection) {
                    // Rule: Proposals that received rejection grades are not considered favorites,
                    //       even if they received the highest grade of the ballot.
                    0.0
                } else if (proposalGrade != highestGradeGiven) {
                    // Rule: We only care about the favorite proposals in this initial step.
                    //       It ensures backwards compatibility and resilience to polarized voting.
                    0.0
                } else {
                    // Rule: Multiple favorites of a ballot receive each a fraction of a vote.
                    //       This fosters resilience to strategic voting.
                    val amountOfFavorites = ballot.countProposalsWithGrade(proposalGrade)
                    1.0 / amountOfFavorites.toDouble()
                }
            }
        }

        val totalFavoritism = favoritism.sum()
        val initialProportions: List<Double> = List(size = amountOfProposals) { proposalIndex ->
            favoritism[proposalIndex] / totalFavoritism
        }

        // Step 2: For each pair of proposals, use the preference for one over the other in each
        //         ballot to transfer a fraction of the initial proportion from one proposal to
        //         another, if neither proposal was a favorite in that ballot.
        val finalProportions: MutableList<Double> = initialProportions.toMutableList()
        val seepingNormalization = amountOfBallots * (amountOfProposals - 1)
        for (proposalIndexA in (0..<amountOfProposals - 1)) {
            for (proposalIndexB in (proposalIndexA + 1..<amountOfProposals)) {
                var preferenceForA = 0
                var preferenceForB = 0

                for (ballot in ballots) {
                    val proposalGradeA = ballot.gradeOf(proposalIndexA)
                    val proposalGradeB = ballot.gradeOf(proposalIndexB)
                    val highestGrade = ballot.getHighestGrade()
                    val isBallotFavoritism = proposalGradeA == highestGrade ||
                        proposalGradeB == highestGrade
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

                val seepingIntent: Double = (preferenceForB - preferenceForA).toDouble() /
                    seepingNormalization
                var seepingAmount = 0.0
                if (seepingIntent > 0) {
                    seepingAmount = initialProportions[proposalIndexA] * seepingIntent
                }
                if (seepingIntent < 0) {
                    seepingAmount = initialProportions[proposalIndexB] * seepingIntent
                }

                finalProportions[proposalIndexB] += seepingAmount
                finalProportions[proposalIndexA] -= seepingAmount
            }
        }

        // Step 3: ???

        // Step 4: Profit!
        return finalProportions
    }
}
