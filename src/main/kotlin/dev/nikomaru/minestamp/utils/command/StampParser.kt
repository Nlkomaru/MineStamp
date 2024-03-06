package dev.nikomaru.minestamp.utils.command

import dev.nikomaru.minestamp.data.ImageListData
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.stamp.StampManager
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
    private val emojiProperties: Properties by inject()
    override fun resolve(context: ValueResolver.ValueResolverContext): AbstractStamp {
        val code = context.pop()
        return StampManager.getStamp(code) ?: throw IllegalArgumentException("Stamp not found: $code")
    }

    init {
        commandHandler.autoCompleter.registerParameterSuggestions(
            AbstractStamp::class.java,
        ) { _: List<String>, actor: CommandActor, _: ExecutableCommand ->
            if (actor.player.hasPermission("minestamp.advanced")) {
                val images = get<ImageListData>().list.map { "!$it" }
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