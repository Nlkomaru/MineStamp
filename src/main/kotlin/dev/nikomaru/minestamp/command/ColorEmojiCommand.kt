package dev.nikomaru.minestamp.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.WrappedParticle
import dev.nikomaru.minestamp.data.PlayerDefaultEmojiConfigData
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.stamp.Stamp
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Default
import org.incendo.cloud.annotations.Permission
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

import java.util.*
import java.util.concurrent.Executors
import kotlin.math.cos
import kotlin.math.sin

class ColorEmojiCommand: KoinComponent {
    private val rejectSummon = hashMapOf<UUID, Boolean>()

    @Command("minestamp advance <stamp> [time] [size] [particleSize] [accuracy]")
    @CommandDescription("advanced command")
    @Permission("minestamp.command.advance")
    suspend fun colorEmoji(
        sender: CommandSender, @Argument("stamp") stamp: Stamp,
        @Argument("time") @Range(min = "1", max = "10") @Default("3") time: Int,
        @Argument("size") @Range(min = "1.0", max = "20.0") @Default("1.5") size: Double,
        @Argument("particleSize") @Range(min = "0.01", max = "4.0") @Default("1.0") particleSize: Double,
        @Argument("accuracy") @Range(min = "1", max = "128") @Default("32") accuracy: Int,
    ) {
        if (sender !is Player) {
            sender.sendI18nRichMessage("only-execute-from-player")
            return
        }
        val config = PlayerDefaultEmojiConfigData(time, size, particleSize, accuracy)
        summonEmoji(sender, stamp, config)
    }

    @Command("stamp|st <stamp>")
    suspend fun summonEmoji(
        sender: CommandSender, @Argument("stamp") stamp: Stamp,
    ) {
        if (sender !is Player) {
            sender.sendI18nRichMessage("only-execute-from-player")
            return
        }
        val playerStampManager = get<AbstractPlayerStampManager>()
        if (!playerStampManager.availableStamp(sender, stamp)) {
            sender.sendI18nRichMessage("not-have-the-emoji")
            return
        }
        val config = get<PlayerDefaultEmojiConfigData>()
        summonEmoji(sender, stamp, config)
    }

    private suspend fun summonEmoji(
        sender: Player, stamp: Stamp, config: PlayerDefaultEmojiConfigData
    ) {
        val waitSecond = config.waitSecond
        if (rejectSummon[sender.uniqueId] == true) {
            sender.sendI18nRichMessage("cannot-summon-in-a-row", waitSecond)
            return
        }
        rejectSummon[sender.uniqueId] = true

        val image = stamp.getStamp()
        val time = config.second
        val size = config.size
        val particleSize = config.particleSize.toFloat()
        val location = sender.location
        val locX = location.x
        val locY = location.y
        val locZ = location.z
        val locYaw = location.yaw
        val decrease = image.width / config.accuracy
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

        val executor = Executors.newVirtualThreadPerTaskExecutor()

        repeat(count * time) {
            val tasks = list.map { it ->
                Runnable {
                    val position = it.first
                    val x = (position.first - (xMin + midWidth)) / width * 3 * width / height * size
                    val y = (yMax - position.second) / height * 3 * size
                    val color = Color.fromARGB(it.second)
                    sendParticlePacket(color, particleSize, locX, x, locYaw, locY, y, locZ, pm)
                }
            }
            tasks.forEach { executor.execute(it) }
            delay(1000L / count)
        }
        executor.shutdown()

        delay((1000L * waitSecond).toLong())
        rejectSummon.remove(sender.uniqueId)

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
                Particle.DUST, Particle.DustOptions(color, particleSize)
            )
        )
        val absX = locX + (x * cos(-locYaw.toDouble() / 180 * Math.PI))
        val absZ = locZ + (x * sin(locYaw.toDouble() / 180 * Math.PI))
        val absY = locY + y + 2
        packet.doubles.write(0, absX).write(1, absY).write(2, absZ)
        pm.broadcastServerPacket(packet)
    }
}

