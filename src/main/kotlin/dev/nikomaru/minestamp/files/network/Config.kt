package dev.nikomaru.minestamp.files.network

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.network.EmojiConfig
import dev.nikomaru.minestamp.stamp.StampManager
import dev.nikomaru.minestamp.utils.Utils.json
import kotlinx.serialization.encodeToString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object Config: KoinComponent {
    val plugin: MineStamp by inject()
    lateinit var config: EmojiConfig

    fun loadConfig() {
        val configFile = plugin.dataFolder.resolve("config.json")

        if (!configFile.exists()) {
            val defaultEmojiConfig = EmojiConfig(
                version = "1.0.0",
                second = 1,
                accuracy = 32,
                size = 3,
                particleSize = 1.5,
                defaultEmoji = arrayListOf(
                    ":cucumber:", ":thinking-face:", ":angry-face:", ":sleeping-face:"
                ).mapNotNull { StampManager.getStamp(it) })
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
            configFile.writeText(json.encodeToString(defaultEmojiConfig))
        }
        config = json.decodeFromString<EmojiConfig>(configFile.readText())
        loadKoinModules(module {
            single { config }
        })
    }
}

