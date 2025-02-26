package com.illiouchine.jm.model

data class PollConfig(
    val subject: String = "",
    val proposals: List<String> = emptyList(),
    val grading: Grading = Grading.Quality7Grading,
)
