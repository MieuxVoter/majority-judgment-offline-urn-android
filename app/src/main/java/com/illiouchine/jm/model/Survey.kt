package com.illiouchine.jm.model

data class Survey(
    val subject: String,
    val proposals: List<String>,
    val grading: Grading,
)

data class SetupSurvey(
    val subject: String = "",
    val props: List<String> = emptyList()
)