package com.illiouchine.jm.filters

import androidx.annotation.StringRes
import com.illiouchine.jm.R
import kotlinx.serialization.Serializable

@Serializable
sealed class GradeComparator(
    @get:StringRes val word: Int,
    val compare: (ballotGrade: Int, grade: Int) -> Boolean,
) {

    class ExactGradeComparator : GradeComparator(
        word = R.string.grade_comparator_exactly,
        compare = { ballotGrade, grade ->
            ballotGrade == grade
        },
    )

    class AtLeastGradeComparator : GradeComparator(
        word = R.string.grade_comparator_at_least,
        compare = { ballotGrade, grade ->
            ballotGrade >= grade
        },
    )

    class AtMostGradeComparator : GradeComparator(
        word = R.string.grade_comparator_at_most,
        compare = { ballotGrade, grade ->
            ballotGrade <= grade
        },
    )
}