package com.illiouchine.jm.service

import com.illiouchine.jm.extensions.bigSumOf
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.ProposalTally
import com.ionspin.kotlin.bignum.integer.BigInteger

class AsciiMeritProfile {
    fun generate(
        tally: ProposalTally,
        grading: Grading,
        width: Int = 70,
        highestGradeOnTheLeft: Boolean = false,
        medianCharacter: String = "|",
        charset: List<String> = listOf(
            "▁",
            "▂",
            "▃",
            "▄",
            "▅",
            "▆",
            "▇",
            "█",
        ),
    ): String {
        if (charset.size < grading.getAmountOfGrades()) {
            // Here we decided to silently fail and carry on without throwing.
            // That's not very nice, and in the future we might throw instead.
            return ""
        }

        if (width < 3) {
            return ""
        }

        val ascii = buildString {
            for (cursor in 0..<width) {
                val ratio = if (highestGradeOnTheLeft) {
                    (width - cursor - 1).toDouble() / width.toDouble()
                } else {
                    cursor.toDouble() / width.toDouble()
                }
                val offset = if (highestGradeOnTheLeft) { 0 } else { 1 }
                val isMedian = ((width - offset) / 2 == cursor)
                val gradeChar = if (isMedian) {
                    medianCharacter
                } else {
                    charset[getGradeIndexAtRatio(tally, ratio)]
                }
                append(gradeChar)
            }
        }

        return ascii
    }

    /**
     * Note: this method ignores BigInteger shenanigans, and so will choke on gargantuan ints.
     * > Those are used only in tally normalization via GCM, which this app does not do.
     */
    private fun getGradeIndexAtRatio(
        tally: ProposalTally,
        ratio: Double,
    ): Int {
        val totalSize = tally.tally.bigSumOf { it }.doubleValue(exactRequired = false)
        val targetIndex = (ratio * totalSize).toInt()
        var cursorStart: Int
        var cursor = 0
        tally.tally.forEachIndexed { gradeIndex, gradeTally ->
            if (gradeTally > BigInteger.ZERO) {
                cursorStart = cursor
                cursor += gradeTally.intValue(exactRequired = false)

                if (cursorStart <= targetIndex && targetIndex < cursor) {
                    return gradeIndex
                }
            }
        }
        // We should also probably raise an error here instead of returning silently.
        return 0
    }
}
