package dev.nikomaru.minestamp.stamp

import dev.nikomaru.minestamp.MineStamp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object StampManager: KoinComponent {
    private val plugin: MineStamp by inject()
    private val emojiProperties: Properties by inject()
    fun getStamp(shortCode: String): AbstractStamp? {
        return when {
            shortCode.startsWith("!") -> getImageStamp(shortCode)
            shortCode.startsWith(":") -> getEmojiStamp(shortCode)
            shortCode.startsWith("&") -> TODO() //参照によるアクセス
            else -> null
        }
    }


    private fun getImageStamp(shortCode: String): ImageStamp? {
        val file = plugin.dataFolder.resolve("image").resolve(shortCode.removePrefix("!"))
        if (!file.exists()) return null
        return ImageStamp(shortCode)
    }

    private fun getEmojiStamp(shortCode: String): EmojiStamp? {
        val unicode = emojiProperties.getProperty(shortCode).lowercase().replace(" ", "_")
        val pictureName = "emoji_u${unicode}.png"
        plugin.javaClass.getResourceAsStream("/noto-emoji_128/$pictureName") ?: run {
            plugin.logger.warning("/noto-emoji_128/$pictureName is not found.")
            return null
        }
        return EmojiStamp(shortCode)
    }

    fun getRandomStamp(): AbstractStamp {

    }

}