package dev.nikomaru.minestamp

import com.amazonaws.services.s3.model.PutObjectRequest
import dev.nikomaru.minestamp.command.ColorEmojiCommand
import dev.nikomaru.minestamp.command.PublishTicketCommand
import dev.nikomaru.minestamp.data.FileType
import dev.nikomaru.minestamp.data.LocalConfig
import dev.nikomaru.minestamp.files.Config
import dev.nikomaru.minestamp.listener.LoginEvent
import dev.nikomaru.minestamp.listener.TicketInteractEvent
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.player.LocalPlayerStampManager
import dev.nikomaru.minestamp.player.S3PlayerStampManager
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.utils.command.StampParser
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.ktx.supportSuspendFunctions
import java.awt.Font
import java.util.*


open class MineStamp: JavaPlugin(), KoinComponent {
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


        Config.loadConfig()
        makeFolder()
        val stampManager: AbstractPlayerStampManager =
            if (get<LocalConfig>().type == FileType.S3) {
                S3PlayerStampManager()
            } else {
                LocalPlayerStampManager()
            }
        loadKoinModules(module {
            single<AbstractPlayerStampManager> { stampManager }
        })
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
            val file = plugin.dataFolder.resolve("image").resolve("test.jpg")
            val inputStream = plugin.javaClass.classLoader.getResourceAsStream("test.jpg")
            if (inputStream != null) {
                file.writeBytes(inputStream.readAllBytes())
            }
        }
        if (get<LocalConfig>().type == FileType.S3) {
            val s3Client = dev.nikomaru.minestamp.utils.Utils.getS3Client()
            val s3Config = get<LocalConfig>().s3Config!!
            if (s3Client.doesBucketExistV2(s3Config.bucket).not()) {
                s3Client.createBucket(s3Config.bucket)
            }
            s3Client.putObject(s3Config.bucket, "image/", "")
            val inputStream = plugin.javaClass.classLoader.getResourceAsStream("test.jpg")
            if (inputStream != null) {
                val req = PutObjectRequest(s3Config.bucket, "image/test.jpg", inputStream, null)
                req.requestClientOptions.readLimit = 1024 * 1024 * 10
                s3Client.putObject(req)
            } else {
                throw IllegalStateException("test.jpg is not found")
            }

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