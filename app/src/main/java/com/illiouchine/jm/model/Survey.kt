package com.illiouchine.jm.model

data class Survey(
    val subject: String,
    val proposals: List<String>,
    val grading: Grading,
)

// TODO: figure out the point of having both this and Survey above
data class SetupSurvey(
    val subject: String = "",
    val props: List<String> = emptyList()
)