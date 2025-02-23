package com.illiouchine.jm.model

data class Poll(
    val subject: String = "",
    val proposals: List<String> = emptyList(),
    val grading: Grading = Quality7Grading(),
)
