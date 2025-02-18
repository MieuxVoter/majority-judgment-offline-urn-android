package com.illiouchine.jm.model

data class Survey(
    val subject: String,
    val props: List<String>
)

data class SetupSurvey(
    val subject: String = "",
    val props: List<String> = emptyList()
)