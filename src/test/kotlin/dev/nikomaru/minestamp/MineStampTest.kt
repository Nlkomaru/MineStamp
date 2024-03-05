package dev.nikomaru.minestamp

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import org.bukkit.Server
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class MineStampTest : BeforeEachCallback, AfterEachCallback {



    override fun beforeEach(context: ExtensionContext) {
        println("beforeEach() executed before " + context.displayName + ".");
        server = MockBukkit.mock()
        setupKoin()
    }

    override fun afterEach(context: ExtensionContext) {
        MockBukkit.unmock()
        stopKoin()
    }

    companion object{
        lateinit var server: ServerMock
        lateinit var plugin: MineStamp
    }


    private fun setupKoin() {
        plugin = MockBukkit.load(MineStamp::class.java)
        val appModule = module {
            single<MineStamp> { plugin }
            single<ServerMock> { server }
        }
        loadKoinModules(appModule)
    }

}