package com.illiouchine.jm.ui.composable.plot.utils

import org.junit.Assert.assertEquals
import org.junit.Test

data class StringTruncateTestData(
    val rule: String = "",
    val maxLength: Int,
    val maxLines: Int = -1,
    val ellipsis: String = "",
    val expected: List<Pair<String, String>>, // provided input -(to)→ expected output
)

class StringTruncateTest {
    @Test
    fun truncate() {
        val testData = listOf(
            StringTruncateTestData(
                rule = "Allow empty strings",
                maxLength = 5,
                ellipsis = "",
                expected = listOf(
                    "" to "",
                ),
            ),
            StringTruncateTestData(
                // Low stake rule, do remove perhaps ; mostly here to clarify the lack of feature :)
                rule = "Allow whitespace strings and do truncate them",
                maxLength = 3,
                ellipsis = "",
                expected = listOf(
                    "   " to "   ",
                    "    " to "   ",
                ),
            ),
            StringTruncateTestData(
                rule = "Any strictly negative maxLength disables truncating",
                maxLength = -3,
                ellipsis = "…",
                expected = listOf(
                    "" to "",
                    "The Lost" to "The Lost",
                ),
            ),
            StringTruncateTestData(
                rule = "Do not modify strings below the length limit",
                maxLength = 5,
                ellipsis = "",
                expected = listOf(
                    "a" to "a",
                    "abc" to "abc",
                    "abcd" to "abcd",
                    "abcde" to "abcde",
                    "abcdef" to "abcde",
                    "abcdefg" to "abcde",
                ),
            ),
            StringTruncateTestData(
                rule = "Truncate strings above the length limit",
                maxLength = 3,
                ellipsis = "",
                expected = listOf(
                    "abc" to "abc",
                    "abcd" to "abc",
                    "abcde" to "abc",
                    "abcdef" to "abc",
                    "abcde fg" to "abc",
                ),
            ),
            StringTruncateTestData(
                rule = "Truncate strings to zero",
                maxLength = 0,
                ellipsis = "",
                expected = listOf(
                    "abc" to "",
                ),
            ),
            StringTruncateTestData(
                rule = "Truncate multibyte strings adequately",
                maxLength = 2,
                ellipsis = "",
                expected = listOf(
                    "ÇÆN" to "ÇÆ",
                    "ᵐᵑᵆᴮ" to "ᵐᵑ",
                    // there's perhaps some interesting ways to break String.substring() :3
                ),
            ),
            StringTruncateTestData(
                rule = "Truncate strings using an ellipsis",
                maxLength = 5,
                ellipsis = "…",
                expected = listOf(
                    "abcdefg" to "abcd…",
                ),
            ),
            StringTruncateTestData(
                rule = "Truncate strings using a multi-character ellipsis",
                maxLength = 7,
                ellipsis = "...",
                expected = listOf(
                    "abcdefghij" to "abcd...",
                ),
            ),
            StringTruncateTestData(
                rule = "Ignore ellipsis if content has no room left",
                maxLength = 1,
                ellipsis = "…",
                expected = listOf(
                    "abcdefghij" to "a",
                ),
            ),
            StringTruncateTestData(
                rule = "Ignore large ellipsis if content has no room left",
                maxLength = 3,
                ellipsis = "...",
                expected = listOf(
                    "abcdefghij" to "abc",
                ),
            ),
            StringTruncateTestData(
                rule = "Do leave whitespaces in the truncated content if necessary",
                maxLength = 5,
                ellipsis = "…",
                expected = listOf(
                    "The Lost" to "The …",
                ),
            ),
            // Wip: looking if we really need this or if maybe we can leverage Compose's BasicText
//            StringTruncateTestData(
//                rule = "Overflow on additional available lines",
//                maxLength = 6,
//                maxLines = 2,
//                ellipsis = "…",
//                expected = listOf(
//                    //"Saperlipopette" to "Saperl\nipope…",
//                    //"Pizza Regina" to "Pizza\nRegina",
//                ),
//            ),
        )

        testData.forEachIndexed { testIndex, testDatum ->
            for (io in testDatum.expected) {
                val actual = io.first.truncate(
                    maxLength = testDatum.maxLength,
                    ellipsis = testDatum.ellipsis,
                )
                val expected = io.second
                assertEquals(
                    "Rule #$testIndex `${testDatum.rule}` fails:",
                    expected,
                    actual,
                )
            }
        }
    }
}
