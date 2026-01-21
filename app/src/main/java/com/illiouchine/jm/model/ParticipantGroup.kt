package com.illiouchine.jm.model

import java.math.BigInteger
import fr.mieuxvoter.mj.ParticipantGroup as MJParticipantGroup

data class ParticipantGroup(
    val size: BigInteger,
    val grade: Int,
    val type: Type,
) {
    enum class Type {
        Median,
        Contestation,
        Adhesion,
    }
}

fun MJParticipantGroup.Type.toType(): ParticipantGroup.Type {
    return when (this) {
        MJParticipantGroup.Type.Median -> ParticipantGroup.Type.Median
        MJParticipantGroup.Type.Contestation -> ParticipantGroup.Type.Contestation
        MJParticipantGroup.Type.Adhesion -> ParticipantGroup.Type.Adhesion
    }
}

fun MJParticipantGroup.toParticipantGroup(): ParticipantGroup {
    return ParticipantGroup(
        size = this.size,
        grade = this.grade,
        type = this.type.toType()
    )
}

