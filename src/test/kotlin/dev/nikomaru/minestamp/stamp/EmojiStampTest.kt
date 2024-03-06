package dev.nikomaru.minestamp.stamp

import dev.nikomaru.minestamp.MineStampTest
import dev.nikomaru.minestamp.data.PlayerDefaultEmojiConfigData
import dev.nikomaru.minestamp.utils.Utils.json
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.component.get
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File
import java.util.*

@ExtendWith(MineStampTest::class)
class EmojiStampTest : KoinTest {
    private val emojiProperties: Properties by inject()

    private var count = 0

    @Test
    fun testGetStamp() {
        val config = get<PlayerDefaultEmojiConfigData>()
        val map = hashMapOf<String, Int>()
        emojiProperties.stringPropertyNames().parallelStream().forEach {
            val emojiStamp = StampManager.getStamp(it) ?: return@forEach run{
                emojiProperties.remove(it)
                println(it)
                count++
            }
            val stamp = emojiStamp.getStamp()
            assertNotNull(stamp)
            map[emojiStamp.shortCode] = 1
        }
        val file = File("src/main/resources/default-random.json")
        config.defaultEmoji.forEach{
            map.remove(it.shortCode)
        }
        file.writeText(json.encodeToString(map))
    }
}