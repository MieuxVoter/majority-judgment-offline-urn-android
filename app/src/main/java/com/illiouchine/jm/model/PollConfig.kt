package com.illiouchine.jm.model

import kotlinx.serialization.Serializable

@Serializable
data class PollConfig(
    val subject: String = "",
    val proposals: List<String> = emptyList(),
    val grading: Grading = Grading.Quality7Grading,
)
