package com.illiouchine.jm.model

import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R

// DEPRECATED
enum class Grades {
    ARejeter,
    Insuffisant,
    Passable,
    AssezBien,
    Bien,
    TresBien,
    Excellent,
}

abstract class Grading {
    abstract fun getAmountOfGrades(): Int
    abstract fun getGradeName(gradeIndex: Int): Int
    abstract fun getGradeColor(gradeIndex: Int): Color
}

/**
 * Intermediary class that helps defining grading using lists.
 */
abstract class ListGrading : Grading() {
    abstract fun getGradesNames(): List<Int>
    abstract fun getGradesColors(): List<Color>

    override fun getAmountOfGrades(): Int {
        assert(getGradesNames().size == getGradesColors().size)
        return getGradesNames().size
    }

    override fun getGradeName(gradeIndex: Int): Int {
        assert(gradeIndex < getGradesNames().size)
        return getGradesNames()[gradeIndex]
    }

    override fun getGradeColor(gradeIndex: Int): Color {
        assert(gradeIndex < getGradesColors().size)
        return getGradesColors()[gradeIndex]
    }
}

class Quality7Grading : ListGrading() {

    protected var _gradesNames = listOf(
        R.string.grade_to_reject,
        R.string.grade_insufficient,
        R.string.grade_passable,
        R.string.grade_somewhat_good,
        R.string.grade_good,
        R.string.grade_very_good,
        R.string.grade_excellent,
    )

    protected var _gradesColors = listOf(
        Color(0xffdf3222),
        Color(0xffed6f01),
        Color(0xfffab001),
        Color(0xffc5d300),
        Color(0xff7bbd3e),
        Color(0xff00a249),
        Color(0xff017a36),
    )

    override fun getGradesNames(): List<Int> {
        return _gradesNames
    }

    override fun getGradesColors(): List<Color> {
        return _gradesColors
    }
}