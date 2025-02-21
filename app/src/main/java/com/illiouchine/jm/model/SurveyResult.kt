package com.illiouchine.jm.model

data class SurveyResult(
    // FIXME: remove these
    val subject: String,
    val proposals: List<String>,
    val grading: Grading,
    //////////////////////

    val survey: Survey,
    val judgments: List<Judgment>,
)
