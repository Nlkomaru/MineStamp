package dev.nikomaru.minestamp.data

import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.stamp.StampManager
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDefaultEmojiConfigData(
    val second: Int = 2,
    val size: Double = 3.0,
    val particleSize: Double = 1.5,
    val accuracy: Int = 32,
    val defaultEmoji: List<@Serializable(with = AbstractStampSerializer::class) AbstractStamp> = arrayListOf(
        ":cucumber:", ":thinking-face:", ":angry-face:", ":sleeping-face:"
    ).mapNotNull { StampManager.getStamp(it) }
)
