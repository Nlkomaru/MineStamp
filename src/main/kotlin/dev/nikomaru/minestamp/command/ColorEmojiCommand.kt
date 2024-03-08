package dev.nikomaru.minestamp.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.WrappedParticle
import dev.nikomaru.minestamp.data.PlayerDefaultEmojiConfigData
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import kotlinx.coroutines.delay
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import revxrsal.commands.annotation.*
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Command("minestamp")
class ColorEmojiCommand: KoinComponent {
    val rejectSummon = hashMapOf<UUID, Boolean>()

    @Subcommand("advance")
    @Description("advanced command")
    @CommandPermission("minestamp.command.advance")
    suspend fun colorEmoji(
        sender: CommandSender, abstractStamp: AbstractStamp,
        @Range(min = 1.0, max = 10.0) @Default("3") time: Int,
        @Range(min = 1.0, max = 20.0) @Default("1.5") size: Double,
        @Range(min = 0.01, max = 4.0) @Default("1.0") particleSize: Double,
        @Range(min = 1.0, max = 128.0) @Default("32") accuracy: Int,
    ) {
        if (sender !is Player) {
            sender.sendI18nRichMessage("only-execute-from-player")
            return
        }
        val config = PlayerDefaultEmojiConfigData(time, size, particleSize, accuracy)
        summonEmoji(sender, abstractStamp, config)
    }

    @Command("stamp", "st", "minestamp stamp")
    suspend fun summonEmoji(
        sender: CommandSender, abstractStamp: AbstractStamp
    ) {
        if (sender !is Player) {
            sender.sendI18nRichMessage("only-execute-from-player")
            return
        }
        val playerStampManager = get<AbstractPlayerStampManager>()
        if (!playerStampManager.availableStamp(sender, abstractStamp)) {
            sender.sendI18nRichMessage("not-have-the-emoji")
            return
        }
        val config = get<PlayerDefaultEmojiConfigData>()
        summonEmoji(sender, abstractStamp, config)
    }

    private suspend fun summonEmoji(
        sender: Player, abstractStamp: AbstractStamp, config: PlayerDefaultEmojiConfigData
    ) {
        val waitSecond = config.waitSecond
        if (rejectSummon[sender.uniqueId] == true) {
            sender.sendI18nRichMessage("cannot-summon-in-a-row", waitSecond)
            return
        }
        rejectSummon[sender.uniqueId] = true

        val image = abstractStamp.getStamp()
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
                Particle.REDSTONE, Particle.DustOptions(color, particleSize)
            )
        )
        val absX = locX + (x * cos(-locYaw.toDouble() / 180 * Math.PI))
        val absZ = locZ + (x * sin(locYaw.toDouble() / 180 * Math.PI))
        val absY = locY + y + 2
        packet.doubles.write(0, absX).write(1, absY).write(2, absZ)
        pm.broadcastServerPacket(packet)
    }
}

