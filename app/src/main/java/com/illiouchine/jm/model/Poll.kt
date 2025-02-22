package com.illiouchine.jm.model

data class Poll(
    val subject: String = "",
    val proposals: List<String> = emptyList(),
    val grading: Grading = Quality7Grading(),
)

// TODO: figure out the point of having both this and Survey above
//data class SetupSurvey(
//    val subject: String = "",
//    val props: List<String> = emptyList()
//)