package com.illiouchine.jm.service

import kotlin.random.Random

class InnocentHand(
    private val seed: Long = 666 + 999 + 666 * 999,
) {
    val rng: Random = Random(seed = seed)

    fun pickWinners(weights: List<Double>): List<Int> {
        return buildList {
            val weightsLeft = weights.toMutableList()
            repeat(times = weights.size) {
                val winner = pickWinner(weightsLeft)
                weightsLeft[winner] = 0.0
                add(winner)
            }
        }
    }

    fun pickWinner(weights: List<Double>): Int {
        if (weights.isEmpty()) {
            return 0
        }

        val sum = weights.sum()
        if (sum == 0.0) {
            return 0
        }

        val roll = rng.nextDouble(
            from = 0.0,  // inclusive
            until = sum, // exclusive
        )

        var cursor = 0.0
        weights.indices.forEach {
            cursor += weights[it]
            if (cursor > roll) {
                return it
            }
        }

        // In case of numerical instability — we're adding floats here
        return weights.size - 1
    }

}