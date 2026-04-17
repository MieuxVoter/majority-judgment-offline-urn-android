package com.illiouchine.jm.extensions

import org.junit.Assert
import org.junit.Test

class ShortenNamesTest {

    data class ShortenNamesTestDatum(
        val rule: String = "",
        val input: List<String>,
        val expected: List<String>,
    )

    @Test
    fun shortenNames() {
        val testData = listOf(
            ShortenNamesTestDatum(
                rule = "Allow empty lists",
                input = listOf(),
                expected = listOf()
            ),
            ShortenNamesTestDatum(
                rule = "Allow a single element",
                input = listOf(
                    "Dominique Merle",
                ),
                expected = listOf(
                    "DM",
                )
            ),
            ShortenNamesTestDatum(
                rule = "Use initials",
                input = listOf(
                    "Dominique Merle",
                    "Théo Sabattie",
                    "Victoria Muriel-Ravaud",
                    "Pizza (Fromage & Œufs)",
                    "123 456 789",
                    "u.u",
                    "^.^",
                ),
                expected = listOf(
                    "DM",
                    "TS",
                    "VMR",
                    "PFŒ",
                    "147",
                    "uu",
                    "^.^",
                )
            ),
            ShortenNamesTestDatum(
                rule = "Discriminate duplicate initials (1)",
                input = listOf(
                    "Majority Judgment",
                    "Jean-Luc Mélenchon",
                    "Jean-Luc Moudenc",
                ),
                expected = listOf(
                    "MJ",
                    "JLMé",
                    "JLMo",
                )
            ),
            ShortenNamesTestDatum(
                rule = "Discriminate duplicate initials (2)",
                input = listOf(
                    "Dominique Merle",
                    "Coline Serra",
                    "Charles Serra",
                    "Coline Souci",
                    "Charles Souci",
                ),
                expected = listOf(
                    "DM",
                    "CoSe",
                    "ChSe",
                    "CoSo",
                    "ChSo",
                )
            ),
        )

        testData.forEachIndexed { testIndex, testDatum ->
            val actual = testDatum.input.shortenNames()
            val expected = testDatum.expected
            Assert.assertEquals(
                "Rule #$testIndex `${testDatum.rule}` fails:",
                expected,
                actual,
            )
        }
    }
}
