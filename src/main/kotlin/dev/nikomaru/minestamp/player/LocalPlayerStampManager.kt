package dev.nikomaru.minestamp.player

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.PlayerDefaultEmojiConfigData
import dev.nikomaru.minestamp.data.PlayerData
import dev.nikomaru.minestamp.stamp.Stamp
import dev.nikomaru.minestamp.utils.Utils.json
import kotlinx.serialization.encodeToString
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*

class LocalPlayerStampManager: AbstractPlayerStampManager(),  KoinComponent {
    val plugin: MineStamp by inject()
    override fun init(player: Player) {
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        if (!file.exists()) {
            val data = PlayerData(
                emoji = listOf()
            )
            file.parentFile.mkdirs()
            file.writeText(json.encodeToString(data))
        }
        load(player)
    }

    override fun load(player: Player) {
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        val playerData = json.decodeFromString(PlayerData.serializer(), file.readText())
        playerEmoji[player.uniqueId] = playerData.emoji
    }

    override fun getPlayerStamp(player: Player): ArrayList<Stamp> {
        val defaultStamp = get<PlayerDefaultEmojiConfigData>().defaultEmoji
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        return (playerStamp + defaultStamp).toCollection(arrayListOf())
    }

    override fun addStamp(player: Player, stamp: Stamp) {
        plugin.logger.info("addStamp: ${stamp.shortCode} to ${player.name}")
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        val newStamp = (playerStamp + stamp).toCollection(arrayListOf())
        playerEmoji[player.uniqueId] = newStamp
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        val data = PlayerData(
            emoji = newStamp
        )
        file.writeText(json.encodeToString(data))
    }

    override fun removeStamp(player: Player, stamp: Stamp) {
        plugin.logger.info("removeStamp: ${stamp.shortCode} from ${player.name}")
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        val newStamp = (playerStamp - stamp).toCollection(arrayListOf())
        playerEmoji[player.uniqueId] = newStamp
        val file = plugin.dataFolder.resolve("player").resolve("${player.uniqueId}.json")
        val data = PlayerData(
            emoji = newStamp
        )
        file.writeText(json.encodeToString(data))
    }

    override fun availableStamp(player: Player, stamp: Stamp): Boolean {
        if(player.hasPermission("minestamp.stamp.all")) return true
        val default = get<PlayerDefaultEmojiConfigData>().defaultEmoji
        val playerStamp = playerEmoji[player.uniqueId] ?: emptyList()
        return (playerStamp + default).map{it.shortCode}.contains(stamp.shortCode)
    }
}