package dev.nikomaru.emojistamp.event

import dev.nikomaru.emojistamp.EmojiStamp
import dev.nikomaru.emojistamp.EmojiStamp.Companion.plugin
import dev.nikomaru.emojistamp.data.PlayerData
import dev.nikomaru.emojistamp.files.Config.json
import kotlinx.serialization.encodeToString
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class LoginEvent : Listener {
    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val file = plugin.dataFolder.resolve("player").resolve("$uuid.json")
        if (!file.exists()) {
            plugin.dataFolder.resolve("player").mkdir()
            file.createNewFile()
            val data = PlayerData(
                emoji = listOf()
            )
            file.writeText(json.encodeToString(data))
        }
        EmojiStamp.playerEmoji[uuid] = json.decodeFromString<PlayerData>(file.readText()).emoji
    }
}