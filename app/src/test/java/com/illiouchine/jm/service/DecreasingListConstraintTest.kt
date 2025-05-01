package com.illiouchine.jm.service

import org.junit.Assert.*
import org.junit.Test

class DecreasingListConstraintTest {

    @Test
    fun constrain() {
        val testData = listOf(
            DecreasingListConstrictorTestData(
                name = "nothing to do",
                input = listOf(0.5, 0.25, 0.2, 0.05),
                expected = listOf(0.5, 0.25, 0.2, 0.05),
            ),
            DecreasingListConstrictorTestData(
                name = "last element is bigger",
                input = listOf(0.5, 0.25, 0.10, 0.15),
                expected = listOf(0.5, 0.25, 0.125, 0.125),
            ),
            DecreasingListConstrictorTestData(
                name = "some element is smaller",
                input = listOf(0.5, 0.05, 0.20, 0.15, 0.10),
                expected = listOf(0.5, 0.13333333333333336, 0.13333333333333336, 0.13333333333333336, 0.1),
            ),
            DecreasingListConstrictorTestData(
                name = "some element is zero",
                input = listOf(0.5, 0.2, 0.2, 0.0, 0.1),
                expected = listOf(0.5, 0.2, 0.2, 0.05, 0.05),
            ),
        )

        for (testDatum in testData) {
            val constraint = DecreasingListConstraint()
            val actual = constraint.apply(testDatum.input)

            assertEquals(
                "The test case where `${testDatum.name}` fails:",
                testDatum.expected,
                actual,
            )
        }
    }
}

data class DecreasingListConstrictorTestData(
    val name: String?,
    val input: List<Double>,
    val expected: List<Double>,
)
