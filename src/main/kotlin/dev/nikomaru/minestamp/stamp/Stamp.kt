package dev.nikomaru.minestamp.stamp

import dev.nikomaru.minestamp.MineStamp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.image.BufferedImage

abstract class Stamp(var shortCode: String): KoinComponent {
    val plugin: MineStamp by inject()
    lateinit var image: BufferedImage

    fun getStamp(): BufferedImage {
        return image
    }
}