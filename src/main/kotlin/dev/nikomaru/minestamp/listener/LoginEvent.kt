package dev.nikomaru.minestamp.listener

import dev.nikomaru.minestamp.player.PlayerStampManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class LoginEvent: Listener {
    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        PlayerStampManager.initialize(event.player)
    }
}