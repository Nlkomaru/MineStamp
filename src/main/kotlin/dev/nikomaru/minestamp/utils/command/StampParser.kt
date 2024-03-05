package dev.nikomaru.minestamp.utils.command

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.player.PlayerStampManager
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.stamp.StampManager
import org.koin.core.component.KoinComponent
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
                val images = plugin.dataFolder.resolve("image").listFiles()?.map { "!${it.name}" } ?: emptyList()
                val emojis = emojiProperties.keys.map { "$it" }
                return@registerParameterSuggestions (images + emojis).toSet()
            } else {
                return@registerParameterSuggestions PlayerStampManager.getPlayerStamp(actor.player).map { it.shortCode }
                    .toSet()
            }
        }
    }

}