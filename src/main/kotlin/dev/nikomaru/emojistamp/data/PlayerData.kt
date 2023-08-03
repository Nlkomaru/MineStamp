package dev.nikomaru.emojistamp.data

import kotlinx.serialization.Serializable


@Serializable
data class PlayerData (
    val emoji : List<String>,
)