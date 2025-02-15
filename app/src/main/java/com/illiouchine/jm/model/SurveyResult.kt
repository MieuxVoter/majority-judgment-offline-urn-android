package com.illiouchine.jm.model

data class SurveyResult(
    val asking: String,
    val props: List<String>,
    val vote: List<VoteResult>
)

