package dev.nikomaru.emojistamp.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Range
import com.destroystokyo.paper.ParticleBuilder
import dev.nikomaru.emojistamp.EmojiStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin

@CommandMethod("emost color")
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
        if (EmojiCommand.coolingTime[sender] == true) {
            sender.sendPlainMessage("クールダウン中です")
            return
        }
        val time = inputTime ?: 1
        val size = inputSize ?: 1
        val particleSize = inputParticleSize ?: 1

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

        val particleBuilder = ParticleBuilder(Particle.REDSTONE)
        particleBuilder.location(location)

        EmojiStamp.plugin.server.scheduler.runTaskAsynchronously(EmojiStamp.plugin, Runnable {

            repeat(10 * time) {
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
                    particleBuilder.data(Particle.DustOptions(Color.fromARGB(it.second), 0.5f * particleSize))
                    particleBuilder.location(particleLocation)
                    particleBuilder.spawn()
//            sender.world.spawnParticle(Particle.REDSTONE, location, 5, Particle.DustOptions(bukkitColor, 0.5f))
                }
                Thread.sleep(50)
            }
        })
        EmojiCommand.coolingTime[sender] = true
        delay(1000 * 5)
        EmojiCommand.coolingTime.remove(sender)
    }
}