package dev.nikomaru.minestamp.data

import dev.nikomaru.minestamp.stamp.Stamp
import dev.nikomaru.minestamp.stamp.StampManager
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
data class PlayerData(
    val emoji: List<@Serializable(with = AbstractStampSerializer::class) Stamp>
)

object AbstractStampSerializer: KSerializer<Stamp> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AbstractStamp", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Stamp {
        val str = decoder.decodeString()
        return StampManager.getStamp(str)!!
    }

    override fun serialize(encoder: Encoder, value: Stamp) {
        return encoder.encodeString(value.shortCode)
    }
}