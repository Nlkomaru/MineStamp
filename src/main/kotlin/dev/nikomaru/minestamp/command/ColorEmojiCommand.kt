package dev.nikomaru.minestamp.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.WrappedParticle
import dev.nikomaru.minestamp.files.network.Config
import dev.nikomaru.minestamp.stamp.AbstractStamp
import kotlinx.coroutines.delay
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.*
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin

@Command("stamp", "minestamp")
class ColorEmojiCommand {
    @Subcommand("advance")
    @Description("advanced command")
    suspend fun colorEmoji(
        sender: CommandSender, abstractStamp: AbstractStamp,
        @Range(min = 1.0, max = 10.0) @Default("3") time: Int,
        @Range(min = 1.0, max = 20.0) @Default("1.5") size: Float,
        @Range(min = 0.01, max = 4.0) @Default("1.0") particleSize: Float,
        @Range(min = 1.0, max = 128.0) @Default("32") accuracy: Int,
    ) {
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        val image: BufferedImage = abstractStamp.getStamp()
        val location = sender.location
        val locX = location.x
        val locY = location.y
        val locZ = location.z
        val locYaw = location.yaw
        val decrease = image.width / accuracy
        val list = arrayListOf<Pair<Pair<Int, Int>, Int>>()
        repeat(image.width) { x ->
            repeat(image.height) { y ->
                if (image.getRGB(x, y) != 0 && x % decrease == 0 && y % decrease == 0) {
                    list.add(Pair(Pair(x, y), image.getRGB(x, y)))
                }
            }
        }

        val (xMax, xMin, yMax, yMin) = getCorner(list) ?: return
        val width = (xMax - xMin).toDouble() // 180 - 20 = 160
        val midWidth = width / 2 // 80
        val height = (yMax - yMin).toDouble()
        val pm = ProtocolLibrary.getProtocolManager()
        val count = 8

        repeat(count * time) {
            list.parallelStream().forEach {
                val position = it.first
                val x = (position.first - (xMin + midWidth)) / width * 3 * size // 80 / 160 * 3 = 1.5
                val y = (yMax - position.second) / height * 3 * size
                val color = Color.fromARGB(it.second)
                sendParticlePacket(color, particleSize, locX, x, locYaw, locY, y, locZ, pm)
            }
            delay(1000L / count)
        }

    }

    private fun getCorner(list: ArrayList<Pair<Pair<Int, Int>, Int>>): List<Int>? = with(list) {
        val xValues = map { it.first.first }
        val yValues = map { it.first.second }
        val xMax = xValues.maxOrNull() ?: return null
        val xMin = xValues.minOrNull() ?: return null
        val yMax = yValues.maxOrNull() ?: return null
        val yMin = yValues.minOrNull() ?: return null
        listOf(xMax, xMin, yMax, yMin)
    }

    private fun sendParticlePacket(
        color: Color,
        particleSize: Float,
        locX: Double,
        x: Double,
        locYaw: Float,
        locY: Double,
        y: Double,
        locZ: Double,
        pm: ProtocolManager
    ) {
        val packet = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
        packet.newParticles.write(
            0, WrappedParticle.create(
                Particle.REDSTONE, Particle.DustOptions(color, particleSize)
            )
        )
        val absX = locX + (x * cos(-locYaw.toDouble() / 180 * Math.PI))
        val absZ = locZ + (x * sin(locYaw.toDouble() / 180 * Math.PI))
        val absY = locY + y + 2
        packet.doubles.write(0, absX).write(1, absY).write(2, absZ)
        pm.broadcastServerPacket(packet)
    }

    @Subcommand("stamp")
    suspend fun summonEmoji(
        sender: CommandSender, abstractStamp: AbstractStamp
    ) {
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        val image = abstractStamp.getStamp()
        val time = Config.config.second
        val size = Config.config.size
        val particleSize = Config.config.particleSize.toFloat()
        val location = sender.location
        val locX = location.x
        val locY = location.y
        val locZ = location.z
        val locYaw = location.yaw
        val decrease = image.width / Config.config.accuracy
        val list = arrayListOf<Pair<Pair<Int, Int>, Int>>()
        repeat(image.width) { x ->
            repeat(image.height) { y ->
                if (image.getRGB(x, y) != 0 && x % decrease == 0 && y % decrease == 0) {
                    list.add(Pair(Pair(x, y), image.getRGB(x, y)))
                }
            }
        }

        val (xMax, xMin, yMax, yMin) = getCorner(list) ?: return
        val width = (xMax - xMin).toDouble() // 180 - 20 = 160
        val midWidth = width / 2 // 80
        val height = (yMax - yMin).toDouble()
        val pm = ProtocolLibrary.getProtocolManager()
        val count = 8

        repeat(count * time) {
            list.parallelStream().forEach {
                val position = it.first
                val x = (position.first - (xMin + midWidth)) / width * 3 * width / height * size // 80 / 160 * 3 = 1.5
                val y = (yMax - position.second) / height * 3 * size
                val color = Color.fromARGB(it.second)
                sendParticlePacket(color, particleSize, locX, x, locYaw, locY, y, locZ, pm)
            }
            delay(1000L / count)
        }
    }
}

