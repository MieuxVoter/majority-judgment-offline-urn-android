package com.illiouchine.jm.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import fr.mieuxvoter.kmj.analysis.ParticipantGroup as LibParticipantGroup

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

fun LibParticipantGroup.Type.toType(): ParticipantGroup.Type {
    return when (this) {
        LibParticipantGroup.Type.Median -> ParticipantGroup.Type.Median
        LibParticipantGroup.Type.Contestation -> ParticipantGroup.Type.Contestation
        LibParticipantGroup.Type.Adhesion -> ParticipantGroup.Type.Adhesion
    }
}

fun LibParticipantGroup.toParticipantGroup(): ParticipantGroup {
    return ParticipantGroup(
        size = this.size,
        grade = this.grade,
        type = this.type.toType(),
    )
}
