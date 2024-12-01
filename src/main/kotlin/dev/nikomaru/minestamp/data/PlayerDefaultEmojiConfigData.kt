package dev.nikomaru.minestamp.data

import dev.nikomaru.minestamp.stamp.Stamp
import dev.nikomaru.minestamp.stamp.StampManager
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDefaultEmojiConfigData(
    val second: Int = 3,
    val size: Double = 1.8,
    val particleSize: Double = 1.0,
    val accuracy: Int = 32,
    val defaultEmoji: List<@Serializable(with = AbstractStampSerializer::class) Stamp> = arrayListOf(
        ":cucumber:", ":thinking-face:", ":angry-face:", ":sleeping-face:"
    ).mapNotNull { StampManager.getStamp(it) },
    val waitSecond : Double = 5.0
)
