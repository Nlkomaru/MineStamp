package dev.nikomaru.minestamp.command

import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Default
import org.incendo.cloud.annotations.Permission

@Command("minestamp")
class PlayerUtilCommand: KoinComponent {

    @Command("help")
    fun help(actor: CommandSender) { //クリックしたらurlに飛ぶ
        actor.sendI18nRichMessage("help-url")
    }


    @Command("list [reset]")
    fun showEmojiList(actor: Player , @Argument("reset") @Default("4") resetCount: Int) {
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