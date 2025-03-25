package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Grading.Quality3Grading
import com.illiouchine.jm.model.Grading.Quality5Grading
import com.illiouchine.jm.model.Grading.Quality7Grading
import kotlinx.serialization.Serializable

val gradings: List<Grading> = listOf(Quality7Grading, Quality5Grading, Quality3Grading)

/**
 * The grades must be unambiguously sorted for MJ to work.
 */
@Serializable
sealed class Grading(
    @StringRes val name: Int,
    val grades: List<Grade>,
) {
    @Serializable
    data object Quality7Grading : Grading(
        name = R.string.seven_grading,
        grades = listOf(
            gradeToReject,
            gradeInsufficient,
            gradePassable,
            gradeSomeWhatGood,
            gradeGood,
            gradeVeryGood,
            gradeExcellent,
        ),
    )

    @Serializable
    data object Quality5Grading : Grading(
        name = R.string.five_grading,
        grades = listOf(
            gradeToReject,
            gradePassable,
            gradeSomeWhatGood,
            gradeGood,
            gradeExcellent,
        ),
    )

    @Serializable
    data object Quality3Grading : Grading(
        name = R.string.three_grading,
        grades = listOf(
            gradeToReject,
            gradeSomeWhatGood,
            gradeExcellent,
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
                else -> DEFAULT_GRADING_QUALITY_VALUE
            }
        }
    }
}