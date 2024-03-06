package dev.nikomaru.minestamp.player

import dev.nikomaru.minestamp.stamp.AbstractStamp
import org.bukkit.entity.Player
import java.util.*

abstract class AbstractPlayerStampManager {
    val playerEmoji = mutableMapOf<UUID, List<AbstractStamp>>()

    abstract fun init(player: Player)
    abstract fun load(player: Player)
    abstract fun getPlayerStamp(player: Player): ArrayList<AbstractStamp>
    abstract fun addStamp(player: Player, stamp: AbstractStamp)
    abstract fun removeStamp(player: Player, stamp: AbstractStamp)
    abstract fun hasStamp(player: Player, stamp: AbstractStamp): Boolean
}