package dev.nikomaru.emojistamp.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import com.destroystokyo.paper.ParticleBuilder
import dev.nikomaru.emojistamp.EmojiStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin


@CommandMethod("emost")
class EmojiCommand {
    @CommandMethod("<cldr> [color]")
    suspend fun summon(
        sender: CommandSender, @Argument(value = "cldr", suggestions = "cldr") cldr: String,
        @javax.annotation.Nullable @Argument(value = "color", suggestions = "color") color: String? = "black"
    ) {
        if (!EmojiStamp.emojiProperties.containsKey(cldr)) {
            sender.sendPlainMessage("その絵文字は存在しません")
            return
        }
        if (sender !is Player) {
            sender.sendPlainMessage("プレイヤーから実行してください")
            return
        }
        if (coolingTime[sender] == true) {
            sender.sendPlainMessage("クールダウン中です")
            return
        }

        val codePoints = EmojiStamp.emojiProperties.getProperty(cldr).split(" ").map { it.toInt(16) }.toIntArray()
        val unicode = String(codePoints, 0, codePoints.size)
        val img = BufferedImage(200, 200, BufferedImage.TYPE_INT_BGR)
        val g: Graphics2D = img.createGraphics()
        g.color = java.awt.Color.WHITE
        g.fillRect(0, 0, 200, 200)
        g.color = java.awt.Color.BLACK
        g.font = EmojiStamp.font.deriveFont(120f)
        g.drawString(unicode, 30, 140)
        g.dispose()

        val list = arrayListOf<Pair<Int, Int>>()
        repeat(200) { x ->
            repeat(200) { y ->
                if (img.getRGB(x, y) == java.awt.Color.BLACK.rgb) {
                    if (x % 3 == 0 && y % 3 == 0) {
                        list.add(Pair(x, y))
                    }
                }
            }
        }

        val xMax = list.maxBy { it.first }.first // 180
        val xMin = list.minBy { it.first }.first // 20
        val yMax = list.maxBy { it.second }.second
        val yMin = list.minBy { it.second }.second

        val width = (xMax - xMin).toDouble() // 180 - 20 = 160
        val midWidth = width / 2 // 80
        val height = (yMax - yMin).toDouble()

        val bukkitColor = when (color) {
            "aqua" -> Color.AQUA
            "black" -> Color.BLACK
            "blue" -> Color.BLUE
            "fuchsia" -> Color.FUCHSIA
            "gray" -> Color.GRAY
            "green" -> Color.GREEN
            "lime" -> Color.LIME
            "maroon" -> Color.MAROON
            "navy" -> Color.NAVY
            "olive" -> Color.OLIVE
            "orange" -> Color.ORANGE
            "purple" -> Color.PURPLE
            "red" -> Color.RED
            "silver" -> Color.SILVER
            "teal" -> Color.TEAL
            "white" -> Color.WHITE
            "yellow" -> Color.YELLOW
            else -> Color.BLACK
        }

        val particleBuilder = ParticleBuilder(Particle.REDSTONE)

        particleBuilder.data(Particle.DustOptions(bukkitColor, 0.5f))
        val location = sender.location
        particleBuilder.location(location)
        withContext(Dispatchers.Default) {

            repeat(10) {
                list.forEach {
                    val x = (it.first - (xMin + midWidth)) / width * 3 // 80 / 160 * 3 = 1.5
                    val y = (yMax - it.second) / height * 3
                    val particleLocation = Location(
                        location.world,
                        location.x + (x * cos(-location.yaw.toDouble() / 180 * Math.PI)),
                        location.y + y + 2,
                        location.z + (x * sin(location.yaw.toDouble() / 180 * Math.PI))
                    )
                    particleBuilder.location(particleLocation)
                    particleBuilder.spawn()
//            sender.world.spawnParticle(Particle.REDSTONE, location, 5, Particle.DustOptions(bukkitColor, 0.5f))
                }
                delay(50)
            }
        }

        coolingTime[sender] = true
        delay(3000)
        coolingTime.remove(sender)
    }


    @Suggestions("cldr")
    fun suggestCldr(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return EmojiStamp.emojiProperties.keys.map { it.toString() }
    }

    @Suggestions("color")
    fun suggestColor(sender: CommandContext<CommandSender>, input: String?): List<String> {
        return listOf(
            "aqua",
            "black",
            "blue",
            "fuchsia",
            "gray",
            "green",
            "lime",
            "maroon",
            "navy",
            "olive",
            "orange",
            "purple",
            "red",
            "silver",
            "teal",
            "white",
            "yellow"
        )
    }

    companion object {
        val coolingTime = mutableMapOf<CommandSender, Boolean>()
    }
}