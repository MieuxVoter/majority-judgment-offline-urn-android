package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R
import com.illiouchine.jm.config.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.config.gradings
import kotlinx.serialization.Serializable

/**
 * Rule: The grades must be unambiguously ordered for MJ to work.
 * This is why we do not let users choose their own grades for now.
 */
@Stable
@Serializable
sealed class Grading(
    val uid: Int, // make sure those are unique, and DO NOT edit them afterwards
    @get:StringRes val name: Int,
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
    data object Oral3Grading : Grading(
        uid = 33,
        name = R.string.three_oral_grades,
        grades = listOf(
            gradeNay,
            gradeMeh,
            gradeAye,
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
    data object Love5Grading : Grading(
        uid = 405,
        name = R.string.five_love_grades,
        grades = listOf(
            gradeLoveNotAtAll,
            gradeLoveLittle,
            gradeLoveAtLot,
            gradeLovePassionately,
            gradeLoveToMadness,
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
