package dev.nikomaru.minestamp.utils.command

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.FileType
import dev.nikomaru.minestamp.data.LocalConfig
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.stamp.StampManager
import dev.nikomaru.minestamp.utils.Utils.getS3Client
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.bukkit.player
import revxrsal.commands.command.CommandActor
import revxrsal.commands.command.ExecutableCommand
import revxrsal.commands.process.ValueResolver
import java.util.*

object StampParser: ValueResolver<AbstractStamp>, KoinComponent {
    private val commandHandler: BukkitCommandHandler by inject()
    private val plugin: MineStamp by inject()
    private val emojiProperties: Properties by inject()
    private val localConfig: LocalConfig by inject()
    override fun resolve(context: ValueResolver.ValueResolverContext): AbstractStamp {
        val code = context.pop()
        plugin.logger.info("code: $code")
        return StampManager.getStamp(code) ?: throw IllegalArgumentException("スタンプが見つかりませんでした")
    }

    init {
        commandHandler.autoCompleter.registerParameterSuggestions(
            AbstractStamp::class.java,
        ) { _: List<String>, actor: CommandActor, _: ExecutableCommand ->
            if (actor.player.hasPermission("minestamp.advanced")) {
                val images = if (localConfig.type == FileType.LOCAL) {
                    plugin.dataFolder.resolve("image").listFiles()?.map { it.name } ?: emptyList()
                } else {
                    getS3Client().listObjects(localConfig.s3Config!!.bucket, "image").objectSummaries.map { it.key.removePrefix("image/") }
                }
                val emojis = emojiProperties.keys.map { "$it" }
                return@registerParameterSuggestions (images + emojis).toSet()
            } else {
                val stampManager = get<AbstractPlayerStampManager>()
                return@registerParameterSuggestions stampManager.getPlayerStamp(actor.player).map { it.shortCode }
                    .toSet()
            }
        }
    }

}