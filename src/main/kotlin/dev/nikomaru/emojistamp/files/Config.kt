package dev.nikomaru.emojistamp.files

import dev.nikomaru.emojistamp.EmojiStamp.Companion.plugin
import dev.nikomaru.emojistamp.data.ConfigData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Config {
    lateinit var config: ConfigData
    lateinit var json : Json

    fun loadConfig() {
        json = Json {
            prettyPrint = true
            isLenient = true
        }
        val configFile = plugin.dataFolder.resolve("config.json")

        if (!configFile.exists()){
            val defaultConfigData = ConfigData(
                version = "1.0.0",
                second = 1,
                accuracy = 32,
                size = 3,
                particleSize = 1.5,
                defaultEmoji = arrayListOf(
                    ":avocado:",
                    ":cucumber:",
                    ":potato:",
                    ":red-heart:",
                    ":thinking-face:",
                    ":carrot:",
                    ":angry-face:",
                    ":sleeping-face:",
                    ":star-struck:",
                    ":smiling-face-with-heart-eyes:",
                )

            )
            configFile.createNewFile()
            configFile.writeText(json.encodeToString(defaultConfigData))
        }
        config = json.decodeFromString<ConfigData>(configFile.readText())

    }
}

