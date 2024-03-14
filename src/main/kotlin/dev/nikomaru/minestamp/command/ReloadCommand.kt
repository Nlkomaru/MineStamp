package dev.nikomaru.minestamp.command

import dev.nikomaru.minestamp.files.Config
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("minestamp")
class ReloadCommand {
    @Subcommand("reload")
    @CommandPermission("minestamp.command.reload")
    suspend fun reload(sender: CommandSender) {
        Config.loadConfig()
        sender.sendI18nRichMessage("reloaded-config")
    }
}