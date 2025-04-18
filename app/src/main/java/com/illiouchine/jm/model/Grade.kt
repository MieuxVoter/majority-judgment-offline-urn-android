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

val gradeToReject = Grade(
    name = R.string.grade_to_reject,
    color = Color(0xff870714),
    textColor = Color(0xffffb4a1),
)
val gradeInsufficient = Grade(
    name = R.string.grade_insufficient,
    color = Color(0xffce202c),
    textColor = Color(0xffffc394),
)
val gradePassable = Grade(
    name = R.string.grade_passable,
    color = Color(0xffe5542c),
    textColor = Color(0xffffedb7),
)
val gradeSomeWhatGood = Grade(
    name = R.string.grade_somewhat_good,
    color = Color(0xffde9524),
    textColor = Color(0xfffbe9a6),
)
val gradeGood = Grade(
    name = R.string.grade_good,
    color = Color(0xff7aa032),
    textColor = Color(0xffd9f28c),
)
val gradeVeryGood = Grade(
    name = R.string.grade_very_good,
    color = Color(0xff12894b),
    textColor = Color(0xff90f8b2),
)
val gradeExcellent = Grade(
    name = R.string.grade_excellent,
    color = Color(0xff0a6043),
    textColor = Color(0xffadf8d7),
)
