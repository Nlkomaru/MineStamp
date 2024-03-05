package dev.nikomaru.minestamp.player

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.network.PlayerData
import dev.nikomaru.minestamp.files.network.Config
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.utils.Utils.json
import kotlinx.serialization.encodeToString
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object PlayerStampManager: KoinComponent {
    val plugin: MineStamp by inject()
    val playerEmoji = mutableMapOf<UUID, List<AbstractStamp>>()
    fun initialize(player: Player) {
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        if (!file.exists()) {
            val data = PlayerData(
                emoji = listOf()
            )
            file.parentFile.mkdirs()
            file.writeText(json.encodeToString(data))
        }
        val playerData = json.decodeFromString(PlayerData.serializer(), file.readText())
        playerEmoji[player.uniqueId] = playerData.emoji
    }

    fun getPlayerStamp(player: Player): ArrayList<AbstractStamp> {
        initialize(player)
        val defaultStamp = Config.config.defaultEmoji
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        return (playerStamp + defaultStamp).toCollection(arrayListOf())
    }

    fun addStamp(player: Player, stamp: AbstractStamp) {
        initialize(player)
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        val newStamp = (playerStamp + stamp).toCollection(arrayListOf())
        playerEmoji[player.uniqueId] = newStamp
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        val data = PlayerData(
            emoji = newStamp
        )
        file.writeText(json.encodeToString(data))
    }

    fun removeStamp(player: Player, stamp: AbstractStamp) {
        initialize(player)
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        val newStamp = (playerStamp - stamp).toCollection(arrayListOf())
        playerEmoji[player.uniqueId] = newStamp
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        val data = PlayerData(
            emoji = newStamp
        )
        file.writeText(json.encodeToString(data))
    }
}