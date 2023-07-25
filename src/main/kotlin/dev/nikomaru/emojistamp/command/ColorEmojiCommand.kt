package dev.nikomaru.emojistamp.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Range
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.WrappedParticle
import dev.nikomaru.emojistamp.EmojiStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin

class ColorEmojiCommand {


    @CommandMethod("advancestamp <cldr> [time] [size] [particleSize]")
    suspend fun colorEmoji(
        sender: CommandSender, @Argument(value = "cldr", suggestions = "cldr") cldr: String,
        @javax.annotation.Nullable @Range(min = "1", max = "3") @Argument(value = "time") inputTime: Int?,
        @javax.annotation.Nullable @Range(min = "1", max = "20") @Argument(value = "size") inputSize: Float?,
        @javax.annotation.Nullable @Range(
            min = "0.01",
            max = "4"
        ) @Argument(value = "particleSize") inputParticleSize: Float?
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
        val size = inputSize ?: 1.5f
        val particleSize = inputParticleSize ?: 1.0f

        val location = sender.location

        val locX = location.x
        val locY = location.y
        val locZ = location.z
        val locYaw = location.yaw

        val unicode = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").joinToString("_") { it.lowercase() }
        val pictureName = "emoji_u${unicode}.png"
        val image = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName"))

        val decrease = 4

        val list = arrayListOf<Pair<Pair<Int, Int>, Int>>()
        repeat(image.width) { x ->
            repeat(image.height) { y ->
                if (image.getRGB(x, y) != 0 && x % decrease == 0 && y % decrease == 0) {
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

        val count = 8

        withContext(Dispatchers.Default) {
            repeat(count * time) {
                list.parallelStream().forEach {
                    val position = it.first
                    val x = (position.first - (xMin + midWidth)) / width * 3 * size // 80 / 160 * 3 = 1.5
                    val y = (yMax - position.second) / height * 3 * size
                    val packet = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
                    packet.newParticles.write(
                        0,
                        WrappedParticle.create(
                            Particle.REDSTONE,
                            Particle.DustOptions(Color.fromARGB(it.second), particleSize)
                        )
                    )
                    packet.doubles.write(0, locX + (x * cos(-locYaw.toDouble() / 180 * Math.PI)))
                        .write(1, locY + y + 2)
                        .write(2, locZ + (x * sin(locYaw.toDouble() / 180 * Math.PI)))

                    pm.broadcastServerPacket(packet)
                }
                delay(1000L / count)
            }
        }
    }

    @CommandMethod("ne|notoemoji <limitedCldr>")
    suspend fun summonEmoji(
        sender: CommandSender,
        @Argument(value = "limitedCldr", suggestions = "limitedCldr") cldr: String
    ) {
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        if (!EmojiStamp.emojiProperties.containsKey(cldr)) {
            sender.sendPlainMessage("その絵文字は存在しません")
            return
        }

        val time = 1
        val size = 1.5f
        val particleSize = 1.0f

        val location = sender.location

        val locX = location.x
        val locY = location.y
        val locZ = location.z
        val locYaw = location.yaw

        val unicode = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").joinToString("_") { it.lowercase() }
        val pictureName = "emoji_u${unicode}.png"
        val image = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName"))

        val decrease = 4

        val list = arrayListOf<Pair<Pair<Int, Int>, Int>>()
        repeat(image.width) { x ->
            repeat(image.height) { y ->
                if (image.getRGB(x, y) != 0 && x % decrease == 0 && y % decrease == 0) {
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


        val count = 8

        withContext(Dispatchers.Default) {
            repeat(count * time) {
                list.parallelStream().forEach {
                    val position = it.first
                    val x = (position.first - (xMin + midWidth)) / width * 3 * size // 80 / 160 * 3 = 1.5
                    val y = (yMax - position.second) / height * 3 * size
                    val packet = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
                    packet.newParticles.write(
                        0,
                        WrappedParticle.create(
                            Particle.REDSTONE,
                            Particle.DustOptions(Color.fromARGB(it.second), particleSize)
                        )
                    )
                    packet.doubles.write(0, locX + (x * cos(-locYaw.toDouble() / 180 * Math.PI)))
                        .write(1, locY + y + 2)
                        .write(2, locZ + (x * sin(locYaw.toDouble() / 180 * Math.PI)))

                    pm.broadcastServerPacket(packet)
                }
                delay(1000L / count)
            }
        }
    }


    @Suggestions("cldr")
    fun suggestCldr(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return EmojiStamp.emojiProperties.keys.map { it.toString() }
    }

    @Suggestions("limitedCldr")
    fun suggestLimitedCldr(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return EmojiStamp.limitedEmojiProperties.keys.map { it.toString() }
    }
}

