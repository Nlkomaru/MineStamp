package dev.nikomaru.minestamp.command.parser

import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.ImageListData
import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.stamp.Stamp
import dev.nikomaru.minestamp.stamp.StampManager
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.ArgumentParseResult
import org.incendo.cloud.parser.ArgumentParser
import org.incendo.cloud.parser.ParserDescriptor
import org.incendo.cloud.suggestion.BlockingSuggestionProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.Properties


class StampArgumentParser<CommandSender> : ArgumentParser<CommandSender, Stamp>, BlockingSuggestionProvider.Strings<CommandSender>, KoinComponent{
    val plugin : MineStamp by inject()
    private val emojiProperties: Properties by inject()

    companion object {
        fun stampParser(): ParserDescriptor<CommandSender, Stamp> {
            return ParserDescriptor.of(StampArgumentParser(), Stamp::class.java)
        }
    }

    override fun stringSuggestions(
        commandContext: CommandContext<CommandSender?>,
        input: CommandInput
    ): MutableList<String> {
        val sender: org.bukkit.command.CommandSender = commandContext.sender() as org.bukkit.command.CommandSender

        if (sender.hasPermission("minestamp.advanced")) {
            val images = get<ImageListData>().list.map { "!$it" }
            val emojis = emojiProperties.keys.map { "$it" }
            return (images + emojis).toMutableList()
        } else {
            val stampManager = get<AbstractPlayerStampManager>()
            if (sender is org.bukkit.entity.Player) {
                return stampManager.getPlayerStamp(sender).map { it.shortCode }
                    .toMutableList()
            }
            return mutableListOf()
        }
    }


    override fun parse(
        commandContext: CommandContext<CommandSender & Any>,
        commandInput: CommandInput
    ): ArgumentParseResult<Stamp> {
        val code = commandInput.readString()
        val stamp = StampManager.getStamp(code)
        if (stamp == null) {
            return ArgumentParseResult.failure(IllegalArgumentException("Stamp not found"))
        }
        return ArgumentParseResult.success(stamp)
    }
}