package com.illiouchine.jm.model

data class ParticipantGroupAnalysis(
    val participant: Int,
    val group: ParticipantGroup,
    val decisive: Boolean = false,
)