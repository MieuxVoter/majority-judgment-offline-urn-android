package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.illiouchine.jm.R
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Grade(
    @StringRes val name: Int,
    @Serializable(with = ColorSerializer::class) val color: Color,
    @Serializable(with = ColorSerializer::class) val textColor: Color
)

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.illiouchine.jm.Color", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Color {
        val longValue = decoder.decodeLong()
        return Color(longValue.toULong())
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val longValue = value.value.toLong()
        encoder.encodeLong(longValue)
    }
}

val grade0BgColor = Color(0xff870714)
val grade0FgColor = Color(0xffffb4a1)

val grade1BgColor = Color(0xffce202c)
val grade1FgColor = Color(0xffffedb7)

val grade2BgColor = Color(0xffe5542c)
val grade2FgColor = Color(0xffffedb7)

val grade3BgColor = Color(0xffde9524)
val grade3FgColor = Color(0xfffbe9a6)

val grade4BgColor = Color(0xff7aa032)
val grade4FgColor = Color(0xffd9f28c)

val grade5BgColor = Color(0xff12894b)
val grade5FgColor = Color(0xff90f8b2)

val grade6BgColor = Color(0xff0a6043)
val grade6FgColor = Color(0xffadf8d7)

// QUALITY GRADES

val gradeToReject = Grade(
    name = R.string.grade_to_reject,
    color = grade0BgColor,
    textColor = grade0FgColor,
)
val gradeInsufficient = Grade(
    name = R.string.grade_insufficient,
    color = grade1BgColor,
    textColor = grade1FgColor,
)
val gradePassable = Grade(
    name = R.string.grade_passable,
    color = grade2BgColor,
    textColor = grade2FgColor,
)
val gradeSomewhatGood = Grade(
    name = R.string.grade_somewhat_good,
    color = grade3BgColor,
    textColor = grade3FgColor,
)
val gradeGood = Grade(
    name = R.string.grade_good,
    color = grade4BgColor,
    textColor = grade4FgColor,
)
val gradeVeryGood = Grade(
    name = R.string.grade_very_good,
    color = grade5BgColor,
    textColor = grade5FgColor,
)
val gradeExcellent = Grade(
    name = R.string.grade_excellent,
    color = grade6BgColor,
    textColor = grade6FgColor,
)

// URGENCY GRADES

val gradeNotUrgent = Grade(
    name = R.string.grade_not_urgent,
    color = grade6BgColor,
    textColor = grade6FgColor,
)
val gradeNeutral = Grade(
    name = R.string.grade_neutral,
    color = grade4BgColor,
    textColor = grade4FgColor,
)
val gradeUrgent = Grade(
    name = R.string.grade_urgent,
    color = grade2BgColor,
    textColor = grade2FgColor,
)
val gradeVeryUrgent = Grade(
    name = R.string.grade_very_urgent,
    color = grade1BgColor,
    textColor = grade1FgColor,
)
val gradeExtremelyUrgent = Grade(
    name = R.string.grade_extremely_urgent,
    color = grade0BgColor,
    textColor = grade0FgColor,
)

// PRIORITY GRADES

val gradeTopPriority = Grade(
    name = R.string.grade_top_priority,
    color = grade6BgColor,
    textColor = grade6FgColor,
)
val gradeHighPriority = Grade(
    name = R.string.grade_high_priority,
    color = grade5BgColor,
    textColor = grade5FgColor,
)
val gradePriority = Grade(
    name = R.string.grade_priority,
    color = grade3BgColor,
    textColor = grade3FgColor,
)
val gradeNeutralPriority = Grade(
    name = R.string.grade_neutral,
    color = grade2BgColor,
    textColor = grade2FgColor,
)
val gradeNoPriority = Grade(
    name = R.string.grade_no_priority,
    color = grade0BgColor,
    textColor = grade0FgColor,
)

// ENTHUSIASM GRADES

val gradeDisgust = Grade(
    name = R.string.grade_disgust,
    color = grade0BgColor,
    textColor = grade0FgColor,
)
val gradeReluctance = Grade(
    name = R.string.grade_reluctance,
    color = grade1BgColor,
    textColor = grade1FgColor,
)
val gradeAtaraxia = Grade(
    name = R.string.grade_ataraxia,
    color = grade2BgColor,
    textColor = grade2FgColor,
)
val gradeEnthusiasm = Grade(
    name = R.string.grade_enthusiasm,
    color = grade3BgColor,
    textColor = grade3FgColor,
)
val gradeGreatEnthusiasm = Grade(
    name = R.string.grade_great_enthusiasm,
    color = grade5BgColor,
    textColor = grade5FgColor,
)
val gradeElation = Grade(
    name = R.string.grade_elation,
    color = grade6BgColor,
    textColor = grade6FgColor,
)
