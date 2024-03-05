package dev.nikomaru.minestamp

import dev.nikomaru.minestamp.command.ColorEmojiCommand
import dev.nikomaru.minestamp.command.PublishTicketCommand
import dev.nikomaru.minestamp.files.network.Config
import dev.nikomaru.minestamp.listener.LoginEvent
import dev.nikomaru.minestamp.listener.TicketInteractEvent
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.utils.command.StampParser
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.ktx.supportSuspendFunctions
import java.awt.Font
import java.util.*


open class MineStamp: JavaPlugin() {
    lateinit var plugin: JavaPlugin

    override fun onEnable() { // Plugin startup logic
        plugin = this
        setKoin()


        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        val br = plugin.javaClass.classLoader.getResourceAsStream("emoji.properties")
        val emojiProperties = Properties()
        emojiProperties.load(br)

        loadKoinModules(module {
            single { emojiProperties }
            single {
                Font.createFont(
                    Font.TRUETYPE_FONT,
                    plugin.javaClass.classLoader.getResourceAsStream("NotoEmoji-VariableFont_wght.ttf")
                )
            }
        })

        makeFolder()

        Config.loadConfig()
        setCommand()
        setListener()
    }


    override fun onDisable() { // Plugin shutdown logic
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

    fun setKoin() {
        val appModule = module {
            single<MineStamp> { this@MineStamp }
        }

        GlobalContext.getOrNull() ?: GlobalContext.startKoin {
            printLogger()
            modules(appModule)
        }
    }

    private fun setCommand() {
        val commandHandle = BukkitCommandHandler.create(this)
        loadKoinModules(module {
            single { commandHandle }
        })

        commandHandle.setSwitchPrefix("--")
        commandHandle.setFlagPrefix("--")
        commandHandle.supportSuspendFunctions()

        commandHandle.setHelpWriter { command, actor ->
            java.lang.String.format(
                """
                <color:yellow>command: <color:gray>%s
                <color:yellow>usage: <color:gray>%s
                <color:yellow>description: <color:gray>%s
                
                """.trimIndent(),
                command.path.toList(),
                command.usage,
                command.description,
            )
        }

        commandHandle.registerValueResolver(AbstractStamp::class.java, StampParser)

        with(commandHandle) {
            register(ColorEmojiCommand())
            register(PublishTicketCommand())
        }

    }

    fun setListener() {
        server.pluginManager.registerEvents(LoginEvent(), this)
        server.pluginManager.registerEvents(TicketInteractEvent(), this)
    }

}