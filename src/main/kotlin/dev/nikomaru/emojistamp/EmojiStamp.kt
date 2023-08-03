package dev.nikomaru.emojistamp

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import dev.nikomaru.emojistamp.command.ColorEmojiCommand
import dev.nikomaru.emojistamp.event.LoginEvent
import dev.nikomaru.emojistamp.files.Config
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Font
import java.util.*


class EmojiStamp : JavaPlugin() {

    companion object {
        lateinit var font: Font
        lateinit var emojiProperties: Properties
        lateinit var plugin: JavaPlugin
        val playerEmoji = mutableMapOf<UUID, List<String>>()
    }


    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        font = Font.createFont(
            Font.TRUETYPE_FONT,
            this.javaClass.classLoader.getResourceAsStream("NotoEmoji-VariableFont_wght.ttf")
        )

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        val br = this.javaClass.classLoader.getResourceAsStream("emoji.properties")
        emojiProperties = Properties()
        emojiProperties.load(br)

        makeFolder()
        Config.loadConfig()
        setCommand()
        setEvent()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun makeFolder() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        if (!plugin.dataFolder.resolve("image").exists()) {
            plugin.dataFolder.resolve("image").mkdir()
        }
        val file = plugin.dataFolder.resolve("emoji.properties")
        if (!file.exists()) {
            file.createNewFile()
        }
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

    private fun setEvent() {
        server.pluginManager.registerEvents(LoginEvent(), this)
    }

}