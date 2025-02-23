package com.illiouchine.jm.model

import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R

/**
 * The grades must be unambiguously sorted for MJ to work.
 */
interface Grading {
    fun getAmountOfGrades(): Int
    fun getGradeName(gradeIndex: Int): Int
    fun getGradeColor(gradeIndex: Int): Color
    fun getGradeTextColor(gradeIndex: Int): Color
}

/**
 * Intermediary class that helps defining grading using lists.
 */
abstract class ListGrading : Grading {
    abstract fun getGradesNames(): List<Int>
    abstract fun getGradesColors(): List<Color>
    abstract fun getGradesTextColors(): List<Color>

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

    override fun getGradeTextColor(gradeIndex: Int): Color {
        assert(gradeIndex < getGradesTextColors().size)
        return getGradesTextColors()[gradeIndex]
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

    protected var _gradesTextColors = listOf(
        Color.White,
        Color.White,
        Color.Black,
        Color.Black,
        Color.Black,
        Color.White,
        Color.White,
    )

    override fun getGradesNames(): List<Int> {
        return _gradesNames
    }

    override fun getGradesColors(): List<Color> {
        return _gradesColors
    }

    override fun getGradesTextColors(): List<Color> {
        return _gradesTextColors
    }
}