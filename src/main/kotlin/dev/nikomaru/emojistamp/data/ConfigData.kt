package dev.nikomaru.emojistamp.data

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    val version: String,
    val second: Int,
    val accuracy: Int,
    val size: Int,
    val particleSize: Double,
    val defaultEmoji: List<String>,
)
