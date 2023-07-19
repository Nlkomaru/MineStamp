package dev.nikomaru.emojistamp.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Range
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedParticle
import com.destroystokyo.paper.ParticleBuilder
import dev.nikomaru.emojistamp.EmojiStamp
import dev.nikomaru.emojistamp.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.trim.TrimMaterial.REDSTONE
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin

@CommandMethod("emost|emojistamp|es color")
class ColorEmojiCommand {


    @CommandMethod("<cldr> [time] [size] [particleSize]")
    suspend fun colorEmoji(
        sender: CommandSender, @Argument(value = "cldr", suggestions = "cldr") cldr: String,
        @javax.annotation.Nullable @Range(min = "1", max = "10") @Argument(value = "time") inputTime: Int?,
        @javax.annotation.Nullable @Range(min = "1", max = "100") @Argument(value = "size") inputSize: Int?,
        @javax.annotation.Nullable @Range(
            min = "1",
            max = "500"
        ) @Argument(value = "particleSize") inputParticleSize: Int?
    ) {
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        if (!EmojiStamp.emojiProperties.containsKey(cldr)) {
            sender.sendPlainMessage("その絵文字は存在しません")
            return
        }

        val time = inputTime ?: 1
        val size = inputSize ?: 1

        val location = sender.location

        val unicode = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").joinToString("_") { it.lowercase() }
        val pictureName = "emoji_u${unicode}.png"
        val image = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName"))

        val list = arrayListOf<Pair<Pair<Int, Int>, Int>>()
        repeat(image.width) { x ->
            repeat(image.height) { y ->
                if (image.getRGB(x, y) != 0) {
                    list.add(Pair(Pair(x, y), image.getRGB(x, y)))
                }
            }
        }

        val xMax = list.maxBy { it.first.first }.first.first // 180
        val xMin = list.minBy { it.first.first }.first.first // 20
        val yMax = list.maxBy { it.first.second }.first.second
        val yMin = list.minBy { it.first.second }.first.second

        val width = (xMax - xMin).toDouble() // 180 - 20 = 160
        val midWidth = width / 2 // 80
        val height = (yMax - yMin).toDouble()

        val pm = ProtocolLibrary.getProtocolManager()

        val players = withContext(Dispatchers.minecraft) {
            location.getNearbyPlayers(20.0)
        }

        repeat(20 * time) {
            list.forEach {
                val position = it.first
                val x = (position.first - (xMin + midWidth)) / width * 3 * size // 80 / 160 * 3 = 1.5
                val y = (yMax - position.second) / height * 3 * size
                val particleLocation = Location(
                    location.world,
                    location.x + (x * cos(-location.yaw.toDouble() / 180 * Math.PI)),
                    location.y + y + 2,
                    location.z + (x * sin(location.yaw.toDouble() / 180 * Math.PI))
                )
                val packet = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
                packet.newParticles.write(
                    0,
                    WrappedParticle.create(Particle.REDSTONE, Particle.DustOptions(Color.fromARGB(it.second), 0.5f))
                )
                packet.doubles.write(0, particleLocation.x)
                    .write(1, particleLocation.y)
                    .write(2, particleLocation.z)
                players.forEach { player ->
                    pm.sendServerPacket(player, packet)
                }
            }
            delay(50L)
        }
    }

    @Suggestions("cldr")
    fun suggestCldr(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return EmojiStamp.emojiProperties.keys.map { it.toString() }
    }
}