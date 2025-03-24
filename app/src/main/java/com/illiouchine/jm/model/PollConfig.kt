package com.illiouchine.jm.model

import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import kotlinx.serialization.Serializable

@Serializable
data class PollConfig(
    val subject: String = "",
    val proposals: List<String> = emptyList(),
    val grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
)
