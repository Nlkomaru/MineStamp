package dev.nikomaru.emojistamp

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import dev.nikomaru.emojistamp.command.ColorEmojiCommand
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Font
import java.util.*


class EmojiStamp : JavaPlugin() {

    companion object {
        lateinit var font: Font
        lateinit var emojiProperties: Properties
        lateinit var limitedEmojiProperties: Properties
        lateinit var plugin: JavaPlugin
    }


    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        font = Font.createFont(
            Font.TRUETYPE_FONT,
            this.javaClass.classLoader.getResourceAsStream("NotoEmoji-VariableFont_wght.ttf")
        )
        val file = plugin.dataFolder.resolve("emoji.properties")
        if (file.exists()) {
            limitedEmojiProperties = Properties()
            limitedEmojiProperties.load(file.inputStream())
        }

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        val br = this.javaClass.classLoader.getResourceAsStream("emoji.properties")
        emojiProperties = Properties()
        emojiProperties.load(br)

        setCommand()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setCommand() {

        val commandManager: PaperCommandManager<CommandSender> = PaperCommandManager(
            this,
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build(),
            java.util.function.Function.identity(),
            java.util.function.Function.identity()
        )


        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java) {
            SimpleCommandMeta.empty()
        }.installCoroutineSupport()

        with(annotationParser) {
            parse(ColorEmojiCommand())
        }
    }
}