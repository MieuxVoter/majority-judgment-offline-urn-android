package com.illiouchine.jm.service

import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.ProposalTally
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert
import org.junit.Test

class AsciiMeritProfileTest {

    data class AsciiMeritProfileGenerateTestDatum(
        val rule: String = "",
        val expected: String,
        val tally: ProposalTally,
        val grading: Grading,
        val width: Int = 70,
        val highestGradeOnTheLeft: Boolean = false,
        val medianCharacter: String = "|",
        val charset: List<String> = listOf(
            "▁",
            "▂",
            "▃",
            "▄",
            "▅",
            "▆",
            "▇",
            "█",
        ),
    )

    private fun makeTally(vararg amount: Long): ProposalTally {
        return ProposalTally(
            tally = amount.map { BigInteger.fromLong(it) }.toImmutableList(),
        )
    }

    @Test
    fun generate() {
        val testData = listOf(
            AsciiMeritProfileGenerateTestDatum(
                rule = "All Rejected (odd width)",
                expected = "▁▁▁▁▁|▁▁▁▁▁",
                tally = makeTally(10, 0, 0),
                grading = Grading.Quality7Grading,
                width = 11,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "All Rejected (even width)",
                expected = "▁▁▁▁|▁▁▁▁▁",
                tally = makeTally(10, 0, 0),
                grading = Grading.Quality7Grading,
                width = 10,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "All Rejected (even width, high on left)",
                expected = "▁▁▁▁▁|▁▁▁▁",
                tally = makeTally(100, 0, 0),
                grading = Grading.Quality7Grading,
                width = 10,
                highestGradeOnTheLeft = true,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Basic usage 1",
                expected = "▁▂▃▄▄▅|▆▆▆▇▇▇",
                tally = makeTally(1, 2, 3, 4, 5, 6, 7),
                grading = Grading.Quality7Grading,
                width = 13,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Basic usage 2 (high on left)",
                expected = "▇▇▇▆▆▆|▅▄▄▃▂▁",
                tally = makeTally(1, 2, 3, 4, 5, 6, 7),
                grading = Grading.Quality7Grading,
                width = 13,
                highestGradeOnTheLeft = true,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Basic usage 3",
                expected = "▁▁▁▁▁▁▂▂▂▂▂▂▃▃|▃▃▃▄▄▄▄▄▄▅▅▅▅▅▅",
                tally = makeTally(2, 2, 2, 2, 2),
                grading = Grading.Quality5Grading,
                width = 30,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Basic usage 4",
                expected = "▁▁▁▁▂▂▂▂▂▃▃▃▃▃|▄▄▄▄▄▄▄▅▅▅▅▅▅▅▅",
                tally = makeTally(4, 5, 6, 7, 8),
                grading = Grading.Quality5Grading,
                width = 30,
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Not enough characters in charset",
                expected = "",
                tally = makeTally(3, 1, 1, 1, 1, 1, 3),
                grading = Grading.Quality7Grading,
                width = 13,
                charset = listOf(
                    "▁",
                    "▄",
                    "▆",
                    "▇",
                )
            ),
            AsciiMeritProfileGenerateTestDatum(
                rule = "Width too small",
                expected = "",
                tally = makeTally(3, 1, 1, 2),
                grading = Grading.Quality7Grading,
                width = 2,
                charset = listOf(
                    "▁",
                    "▄",
                    "▆",
                    "▇",
                )
            ),
        )

        val amp = AsciiMeritProfile()

        testData.forEachIndexed { testIndex, testDatum ->
            val actual = amp.generate(
                tally = testDatum.tally,
                grading = testDatum.grading,
                width = testDatum.width,
                highestGradeOnTheLeft = testDatum.highestGradeOnTheLeft,
                medianCharacter = testDatum.medianCharacter,
                charset = testDatum.charset,
            )
            val expected = testDatum.expected
            Assert.assertEquals(
                "Rule #$testIndex `${testDatum.rule}` fails:",
                expected,
                actual,
            )
        }
    }
}
