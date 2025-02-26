package com.illiouchine.jm.model

import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R

/**
 * The grades must be unambiguously sorted for MJ to work.
 */
sealed class Grading(
    val grades: List<Grade>
) {
    data object Quality7Grading : Grading(
        listOf(
            Grade(name = R.string.grade_to_reject, color = Color(0xff870714), Color(0xffffb4a1)),
            Grade(name = R.string.grade_insufficient, color = Color(0xffce202c), Color(0xffffc394)),
            Grade(name = R.string.grade_passable, color = Color(0xffe5542c), Color(0xffffedb7)),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                Color(0xfffbe9a6)
            ),
            Grade(name = R.string.grade_good, color = Color(0xff7aa032), Color(0xffd9f28c)),
            Grade(name = R.string.grade_very_good, color = Color(0xff12894b), Color(0xff90f8b2)),
            Grade(name = R.string.grade_excellent, color = Color(0xff0a6043), Color(0xffadf8d7)),
        )
    )

    data object Quality5Grading : Grading(
        listOf(
            Grade(name = R.string.grade_to_reject, color = Color(0xff870714), Color(0xffffb4a1)),
            Grade(name = R.string.grade_passable, color = Color(0xffe5542c), Color(0xffffedb7)),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                Color(0xfffbe9a6)
            ),
            Grade(name = R.string.grade_good, color = Color(0xff7aa032), Color(0xffd9f28c)),
            Grade(name = R.string.grade_excellent, color = Color(0xff0a6043), Color(0xffadf8d7)),
        )
    )

    data object Quality3Grading : Grading(
        listOf(
            Grade(name = R.string.grade_to_reject, color = Color(0xff870714), Color(0xffffb4a1)),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                Color(0xfffbe9a6)
            ),
            Grade(name = R.string.grade_excellent, color = Color(0xff0a6043), Color(0xffadf8d7)),
        )
    )

    fun getAmountOfGrades(): Int = grades.size

    fun getGradeName(gradeIndex: Int): Int {
        assert(gradeIndex < grades.size)
        return grades[gradeIndex].name
    }

    fun getGradeColor(gradeIndex: Int): Color {
        assert(gradeIndex < grades.size)
        return grades[gradeIndex].color
    }

    fun getGradeTextColor(gradeIndex: Int): Color {
        assert(gradeIndex < grades.size)
        return grades[gradeIndex].textColor
    }

    companion object {
        fun byAmountOfGrades(amount: Int): Grading {
            return when (amount) {
                3 -> Quality3Grading
                5 -> Quality5Grading
                7 -> Quality7Grading
                else -> Quality7Grading
            }
        }
    }
}