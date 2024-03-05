package dev.nikomaru.minestamp.data.network

import dev.nikomaru.minestamp.stamp.AbstractStamp
import kotlinx.serialization.Serializable

@Serializable
data class EmojiConfig(
    val version: String,
    val second: Int,
    val accuracy: Int,
    val size: Int,
    val particleSize: Double,
    val defaultEmoji: List<@Serializable(with = AbstractStampSerializer::class) AbstractStamp>,
)
