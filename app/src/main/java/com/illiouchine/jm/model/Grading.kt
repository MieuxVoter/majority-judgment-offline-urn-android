package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Grading.Quality3Grading
import com.illiouchine.jm.model.Grading.Quality5Grading
import com.illiouchine.jm.model.Grading.Quality7Grading

val gradings: List<Grading> = listOf(Quality7Grading, Quality5Grading, Quality3Grading)

/**
 * The grades must be unambiguously sorted for MJ to work.
 */
sealed class Grading(
    @StringRes val name: Int,
    val grades: List<Grade>,
) {
    data object Quality7Grading : Grading(
        name = R.string.seven_grading,
        grades = listOf(
            Grade(
                name = R.string.grade_to_reject,
                color = Color(0xff870714),
                textColor = Color(0xffffb4a1),
            ),
            Grade(
                name = R.string.grade_insufficient,
                color = Color(0xffce202c),
                textColor = Color(0xffffc394),
            ),
            Grade(
                name = R.string.grade_passable,
                color = Color(0xffe5542c),
                textColor = Color(0xffffedb7),
            ),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                textColor = Color(0xfffbe9a6),
            ),
            Grade(
                name = R.string.grade_good,
                color = Color(0xff7aa032),
                textColor = Color(0xffd9f28c),
            ),
            Grade(
                name = R.string.grade_very_good,
                color = Color(0xff12894b),
                textColor = Color(0xff90f8b2),
            ),
            Grade(
                name = R.string.grade_excellent,
                color = Color(0xff0a6043),
                textColor = Color(0xffadf8d7),
            ),
        ),
    )

    data object Quality5Grading : Grading(
        name = R.string.five_grading,
        grades = listOf(
            Grade(
                name = R.string.grade_to_reject,
                color = Color(0xff870714),
                textColor = Color(0xffffb4a1),
            ),
            Grade(
                name = R.string.grade_passable,
                color = Color(0xffe5542c),
                textColor = Color(0xffffedb7),
            ),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                textColor = Color(0xfffbe9a6),
            ),
            Grade(
                name = R.string.grade_good,
                color = Color(0xff7aa032),
                textColor = Color(0xffd9f28c),
            ),
            Grade(
                name = R.string.grade_excellent,
                color = Color(0xff0a6043),
                textColor = Color(0xffadf8d7),
            ),
        ),
    )

    data object Quality3Grading : Grading(
        name = R.string.three_grading,
        grades = listOf(
            Grade(
                name = R.string.grade_to_reject,
                color = Color(0xff870714),
                textColor = Color(0xffffb4a1),
            ),
            Grade(
                name = R.string.grade_somewhat_good,
                color = Color(0xffde9524),
                textColor = Color(0xfffbe9a6),
            ),
            Grade(
                name = R.string.grade_excellent,
                color = Color(0xff0a6043),
                textColor = Color(0xffadf8d7),
            ),
        ),
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