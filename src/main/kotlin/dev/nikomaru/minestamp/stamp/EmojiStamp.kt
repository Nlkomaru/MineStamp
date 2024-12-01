package dev.nikomaru.minestamp.stamp

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import javax.imageio.ImageIO

class EmojiStamp(shortCode: String): Stamp(shortCode), KoinComponent {
    private val emojiProperties: Properties by inject()
    var char: String

    init {
        val original = emojiProperties.getProperty(shortCode)
        val unicodePoints = original.split(" ")
        val chars = unicodePoints.flatMap { Character.toChars(Integer.parseInt(it, 16)).toList() }.toCharArray()
        char = chars.joinToString("")
        val unicode = original.lowercase().replace(" ", "_")
        val pictureName = "emoji_u${unicode}.png"
        val inputStream = plugin.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName") ?: run {
            plugin.logger.warning("noto-emoji_128/$pictureName is not found.")
            null
        }
        image = ImageIO.read(inputStream)
    }
}