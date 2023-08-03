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
import dev.nikomaru.emojistamp.EmojiStamp.Companion.plugin
import dev.nikomaru.emojistamp.files.Config
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


    @CommandMethod("advancestamp <cldr> [time] [size] [particleSize] [accuracy]")
    suspend fun colorEmoji(
        sender: CommandSender, @Argument(value = "cldr", suggestions = "cldr") cldr: String,
        @javax.annotation.Nullable @Range(min = "1", max = "10") @Argument(value = "time") inputTime: Int?,
        @javax.annotation.Nullable @Range(min = "1", max = "20") @Argument(value = "size") inputSize: Float?,
        @javax.annotation.Nullable @Range(
            min = "0.01",
            max = "4"
        ) @Argument(value = "particleSize") inputParticleSize: Float?,
        @javax.annotation.Nullable @Range(min = "1", max = "128") @Argument(value = "accuracy") inputAccuracy: Int?,
    ) {
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        lateinit var image: java.awt.image.BufferedImage
        if(cldr.contains(":")){
            if (!EmojiStamp.emojiProperties.containsKey(cldr)) {
                sender.sendPlainMessage("その絵文字は存在しません")
                return
            }
            val unicode = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").joinToString("_") { it.lowercase() }
            val pictureName = "emoji_u${unicode}.png"
            image = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName"))
        }else{
            val imageId = cldr.removePrefix("!")
            plugin.dataFolder.resolve("image").resolve(imageId).inputStream().use {
                image = ImageIO.read(it)
            }
        }

        val time = inputTime ?: 1
        val size = inputSize ?: 1.5f
        val particleSize = inputParticleSize ?: 1.0f
        val accuracy = inputAccuracy ?: 32

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

        val xMax = list.maxByOrNull { it.first.first }?.first?.first ?: return // 180
        val xMin = list.minByOrNull { it.first.first }?.first?.first ?: return // 20
        val yMax = list.maxByOrNull { it.first.second }?.first?.second ?: return
        val yMin = list.minByOrNull { it.first.second }?.first?.second ?: return

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
        lateinit var image: java.awt.image.BufferedImage

        if (!(Config.config.defaultEmoji + EmojiStamp.playerEmoji[sender.uniqueId]).contains(cldr)) {
            sender.sendPlainMessage("その絵文字を持っていません")
            return
        }
        if(cldr.contains(":")){
            val unicode = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").joinToString("_") { it.lowercase() }
            val pictureName = "emoji_u${unicode}.png"
            image = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("noto-emoji_128/$pictureName"))
        }else{
            val imageId = cldr.removePrefix("!")
            plugin.dataFolder.resolve("image").resolve("$imageId.png").inputStream().use {
                image = ImageIO.read(it)
            }
        }

        val time = Config.config.second
        val size = Config.config.size
        val particleSize = Config.config.particleSize

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

        val xMax = list.maxByOrNull { it.first.first }?.first?.first ?: return // 180
        val xMin = list.minByOrNull { it.first.first }?.first?.first ?: return // 20
        val yMax = list.maxByOrNull { it.first.second }?.first?.second ?: return
        val yMin = list.minByOrNull { it.first.second }?.first?.second ?: return

        val width = (xMax - xMin).toDouble() // 180 - 20 = 160
        val midWidth = width / 2 // 80
        val height = (yMax - yMin).toDouble()

        val pm = ProtocolLibrary.getProtocolManager()


        val count = 8

        withContext(Dispatchers.Default) {
            repeat(count * time) {
                list.parallelStream().forEach {
                    val position = it.first
                    val x = (position.first - (xMin + midWidth)) / width * 3 * width /height * size // 80 / 160 * 3 = 1.5
                    val y = (yMax - position.second) / height * 3 * size
                    val packet = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
                    packet.newParticles.write(
                        0,
                        WrappedParticle.create(
                            Particle.REDSTONE,
                            Particle.DustOptions(Color.fromARGB(it.second), particleSize.toFloat())
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
        val images = plugin.dataFolder.resolve("image").listFiles()?.map { "!${it.name}" }   ?: return emptyList()
        val emojis = EmojiStamp.emojiProperties.keys.map { it.toString() }
        return (images + emojis)
    }

    @Suggestions("limitedCldr")
    fun suggestLimitedCldr(sender: CommandContext<CommandSender>, input: String?): List<String> {
        val player = sender.sender
        if (player !is Player) {
            return emptyList()
        }

        val images = EmojiStamp.playerEmoji[player.uniqueId] ?: return emptyList()
        val emojis = Config.config.defaultEmoji
        return (images + emojis)
    }
}

