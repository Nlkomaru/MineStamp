package dev.nikomaru.minestamp.command

import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.help.CommandHelp

@Command("minestamp")
class PlayerUtilCommand: KoinComponent {
    @Subcommand("help")
    fun help(actor: CommandSender) { //クリックしたらurlに飛ぶ
        actor.sendI18nRichMessage("help-url")
    }

    @Subcommand("commands")
    fun commands(sender: CommandSender, helpEntries: CommandHelp<String>, @Default("1") page: Int) {
        for (entry in helpEntries.paginate(page, 7))  // 7 entries per page
            sender.sendRichMessage(entry)
    }

    @Subcommand("list")
    fun showEmojiList(actor: Player , @Default("4") resetCount: Int) {
        actor.sendI18nRichMessage("current-emoji-list")
        val playerStampManager: AbstractPlayerStampManager = get<AbstractPlayerStampManager>()
        val list = playerStampManager.getPlayerStamp(actor)
        list.sortBy { it.shortCode }
        var message = ""
        for (i in list.indices) {
            message += list[i].shortCode + if(i % resetCount == resetCount - 1) "\n" else " "
        }
        actor.sendRichMessage(message)
    }

}