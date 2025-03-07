package com.illiouchine.jm.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
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

object ColorSerializer: KSerializer<Color> {
    override val descriptor: SerialDescriptor
        = PrimitiveSerialDescriptor("com.illiouchine.jm.Color", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Color {
        val longValue = decoder.decodeLong()
        return Color(longValue.toULong())
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val longValue = value.value.toLong()
        encoder.encodeLong(longValue)
    }

}