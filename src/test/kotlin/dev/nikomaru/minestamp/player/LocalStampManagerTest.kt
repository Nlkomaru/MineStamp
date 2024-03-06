package dev.nikomaru.minestamp.player

import be.seeseemelk.mockbukkit.ServerMock
import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.MineStampTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.KoinTest
import org.koin.test.inject


@ExtendWith(MineStampTest::class)
class LocalStampManagerTest : KoinTest{
    private val server : ServerMock by inject()
    private val plugin : MineStamp by inject()
    @Test
    @DisplayName("Get PlayerStamp Test")
    fun testGetPlayerStamp() {
        println(plugin.dataFolder)
        val mockPlayer = server.addPlayer("testPlayer")
        val playerStamp = LocalPlayerStampManager().getPlayerStamp(mockPlayer)
        assertNotNull(playerStamp)
    }


}