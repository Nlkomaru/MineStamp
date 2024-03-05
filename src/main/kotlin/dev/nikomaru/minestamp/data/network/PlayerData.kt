package dev.nikomaru.minestamp.data.network

import dev.nikomaru.minestamp.stamp.AbstractStamp
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
    val emoji: List<@Serializable(with = AbstractStampSerializer::class) AbstractStamp>
)

object AbstractStampSerializer: KSerializer<AbstractStamp> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AbstractStamp", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): AbstractStamp {
        val str = decoder.decodeString()
        return StampManager.getStamp(str)!!
    }

    override fun serialize(encoder: Encoder, value: AbstractStamp) {
        return encoder.encodeString(value.shortCode)
    }
}