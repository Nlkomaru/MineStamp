package dev.nikomaru.minestamp.listener

import dev.nikomaru.minestamp.player.AbstractPlayerStampManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LoginEvent: Listener, KoinComponent {
    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val playerStampManager = get<AbstractPlayerStampManager>()
        with(playerStampManager){
            init(event.player)
            load(event.player)
        }
    }
}