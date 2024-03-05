package dev.nikomaru.minestamp.stamp

import javax.imageio.ImageIO

class ImageStamp(shortCode: String): AbstractStamp(shortCode) {
    init {
        image = ImageIO.read(
            plugin.dataFolder.resolve(
                "/image/${
                    shortCode.removePrefix("!")
                }"
            )
        )
    }
}