package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Grading.Enthusiasm6Grading
import com.illiouchine.jm.model.Grading.PositiveQuality5Grading
import com.illiouchine.jm.model.Grading.Priority5Grading
import com.illiouchine.jm.model.Grading.Quality3Grading
import com.illiouchine.jm.model.Grading.Quality5Grading
import com.illiouchine.jm.model.Grading.Quality7Grading
import com.illiouchine.jm.model.Grading.Urgency5Grading
import kotlinx.serialization.Serializable

/**
 * The available gradings in the application.
 */
val gradings: List<Grading> = listOf(
    Quality7Grading,
    Quality5Grading,
    PositiveQuality5Grading,
    Quality3Grading,
    Priority5Grading,
    Urgency5Grading,
    Enthusiasm6Grading,
)

/**
 * The grades must be unambiguously ordered for MJ to work.
 */
@Stable
@Serializable
sealed class Grading(
    val uid: Int, // make sure those are really unique, and DON'T edit them afterwards
    @StringRes val name: Int,
    val grades: List<Grade>,
    // This grade and all the grades above it are considered acceptation grades.
    // This is usually the index of the "Passable" grade.
    val acceptationThreshold: Int,
) {

    @Serializable
    data object Quality7Grading : Grading(
        uid = 7,
        name = R.string.seven_quality_grades,
        grades = listOf(
            gradeToReject,
            gradeInsufficient,
            gradePassable,
            gradeSomewhatGood,
            gradeGood,
            gradeVeryGood,
            gradeExcellent,
        ),
        acceptationThreshold = 2,
    )

    @Serializable
    data object Quality5Grading : Grading(
        uid = 5,
        name = R.string.five_quality_grades,
        grades = listOf(
            gradeToReject,
            gradePassable,
            gradeSomewhatGood,
            gradeGood,
            gradeExcellent,
        ),
        acceptationThreshold = 1,
    )

    @Serializable
    data object PositiveQuality5Grading : Grading(
        uid = 55,
        name = R.string.five_positive_quality_grades,
        grades = listOf(
            gradePassable,
            gradeSomewhatGood,
            gradeGood,
            gradeVeryGood,
            gradeExcellent,
        ),
        acceptationThreshold = 0,
    )

    @Serializable
    data object Quality3Grading : Grading(
        uid = 3,
        name = R.string.three_quality_grades,
        grades = listOf(
            gradeToReject,
            gradeSomewhatGood,
            gradeExcellent,
        ),
        acceptationThreshold = 1,
    )

    @Serializable
    data object Urgency5Grading : Grading(
        uid = 105,
        name = R.string.five_urgency_grades,
        grades = listOf(
            gradeNotUrgent,
            gradeNeutral,
            gradeUrgent,
            gradeVeryUrgent,
            gradeExtremelyUrgent,
        ),
        acceptationThreshold = 1,
    )

    @Serializable
    data object Priority5Grading : Grading(
        uid = 205,
        name = R.string.five_priority_grades,
        grades = listOf(
            gradeNoPriority,
            gradeNeutralPriority,
            gradePriority,
            gradeHighPriority,
            gradeTopPriority,
        ),
        acceptationThreshold = 1,
    )

    @Serializable
    data object Enthusiasm6Grading : Grading(
        uid = 306,
        name = R.string.six_enthusiasm_grades,
        grades = listOf(
            gradeDisgust,
            gradeReluctance,
            gradeAtaraxia,
            gradeEnthusiasm,
            gradeGreatEnthusiasm,
            gradeElation,
        ),
        acceptationThreshold = 2,
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
        fun byUid(uid: Int): Grading {
            gradings.forEach {
                if (it.uid == uid) {
                    return it
                }
            }
            // We should probably throw here instead
            return DEFAULT_GRADING_QUALITY_VALUE
        }
    }
}
