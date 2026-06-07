package com.illiouchine.jm.model.serializer

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "com.illiouchine.jm.Color",
        kind = PrimitiveKind.LONG,
    )

    override fun deserialize(decoder: Decoder): Color {
        val longValue = decoder.decodeLong()
        return Color(longValue.toULong())
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val longValue = value.value.toLong()
        encoder.encodeLong(longValue)
    }
}