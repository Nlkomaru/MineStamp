package dev.nikomaru.minestamp.command

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.stamp.AbstractStamp
import dev.nikomaru.minestamp.utils.RSAUtils.getRSAKeyPair
import dev.nikomaru.minestamp.utils.TicketUtils.getRouletteTicket
import dev.nikomaru.minestamp.utils.TicketUtils.getUniqueTicket
import dev.nikomaru.minestamp.utils.LangUtils.sendI18nRichMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.security.KeyPairGenerator


@Command("minestamp")
@CommandPermission("minestamp.command.publish")
class PublishTicketCommand: KoinComponent {
    val plugin: MineStamp by inject()

    @Subcommand("generate keyPair")
    @Description("Generate private and public keys.")
    @CommandPermission("minestamp.command.publish.generate")
    fun generateKeyPairs(actor: CommandSender) {
        val privateKeyFile = plugin.dataFolder.resolve("privateKey")
        val publicKeyFile = plugin.dataFolder.resolve("publicKey")

        if (privateKeyFile.exists() || publicKeyFile.exists()) {
            actor.sendI18nRichMessage("already-exist-keyPair")
            actor.sendI18nRichMessage("execute-after-remove")
            return
        }
        val keyGenerator = KeyPairGenerator.getInstance("RSA")
        keyGenerator.initialize(2048)
        val keyPair = keyGenerator.genKeyPair()
        val privateKey = keyPair.private
        privateKeyFile.writeBytes(privateKey.encoded)
        val publicKey = keyPair.public
        publicKeyFile.writeBytes(publicKey.encoded)
        actor.sendI18nRichMessage("generate-keyPair")
    }

    @Subcommand("publish roulette")
    @Description("Generate tickets for roulette.")
    @CommandPermission("minestamp.command.publish.roulette")
    fun publishRandom(actor: Player) {
        val rsaKey = getRSAKeyPair() ?: run {
            actor.sendI18nRichMessage("not-found-keyPair")
            actor.sendI18nRichMessage("need-generate-keyPair")
            return
        }
        val algorithm = Algorithm.RSA256(rsaKey.second, rsaKey.first)
        val jwt = JWT.create().withIssuer("minestamp").withClaim("type", "roulette").sign(algorithm)
        val ticket = getRouletteTicket(jwt)
        actor.inventory.addItem(ticket)
    }

    @Subcommand("publish unique")
    @Description("Generate unique tickets.")
    @CommandPermission("minestamp.command.publish.unique")
    fun publishUnique(actor: Player, stamp: AbstractStamp) {
        val rsaKey = getRSAKeyPair() ?: run {
            actor.sendI18nRichMessage("not-found-keyPair")
            actor.sendI18nRichMessage("need-generate-keyPair")
            return
        }
        val algorithm = Algorithm.RSA256(rsaKey.second, rsaKey.first)
        val ticket = getUniqueTicket(algorithm, stamp)
        actor.inventory.addItem(ticket)
    }

}