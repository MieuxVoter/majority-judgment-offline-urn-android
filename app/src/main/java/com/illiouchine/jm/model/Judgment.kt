package com.illiouchine.jm.model

import kotlinx.serialization.Serializable

@Serializable
data class Judgment(
    val proposal: Int,
    val grade: Int,
)
