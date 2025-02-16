package com.illiouchine.jm.model

data class SurveyResult(
    val asking: String,
    val proposals: List<String>,
    val judgments: List<Judgment>
)

