package com.illiouchine.jm.model

data class SurveyResult(
    val subject: String,
    val proposals: List<String>,
    val grading: Grading,
    val judgments: List<Judgment>,
)
