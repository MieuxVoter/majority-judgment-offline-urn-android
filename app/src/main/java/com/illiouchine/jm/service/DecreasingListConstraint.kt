package com.illiouchine.jm.service

import com.illiouchine.jm.service.DecreasingListConstrictorStrategies.MEAN_DESCENDING

class DecreasingListConstraint(
    strategy: DecreasingListConstrictorStrategies = MEAN_DESCENDING,
) {
    fun apply(input: List<Double>): List<Double> {
        val output = input.toMutableList()
        var violationIndex: Int? = getFirstViolationIndex(output)
        //System.out.println("Applying constraint on ${output}")
        while (violationIndex != null) {
            //System.out.println("    violation at ${violationIndex} on ${output}")
            val mean = (output[violationIndex] + output[violationIndex - 1]) / 2.0
            output[violationIndex] = mean
            output[violationIndex - 1] = mean
            violationIndex = getFirstViolationIndex(output)
        }
        return output
    }

    private fun getFirstViolationIndex(input: List<Double>): Int? {
        if (input.isEmpty()) {
            return null
        }

        var previousValue = input.first()
        for (i in 1..<input.size) {
            val currentValue = input[i]
            if (currentValue > previousValue) {
                return i
            }
            previousValue = currentValue
        }

        return null
    }
}

enum class DecreasingListConstrictorStrategies {
    MEAN_DESCENDING,
}
