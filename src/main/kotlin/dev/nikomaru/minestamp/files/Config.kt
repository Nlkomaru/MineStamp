package dev.nikomaru.minestamp.files

import com.amazonaws.services.s3.model.PutObjectRequest
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.nikomaru.minestamp.MineStamp
import dev.nikomaru.minestamp.data.FileType
import dev.nikomaru.minestamp.data.ImageListData
import dev.nikomaru.minestamp.data.LocalConfig
import dev.nikomaru.minestamp.data.PlayerDefaultEmojiConfigData
import dev.nikomaru.minestamp.utils.LangUtils
import dev.nikomaru.minestamp.utils.Utils.getS3Client
import dev.nikomaru.minestamp.utils.Utils.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object Config: KoinComponent {
    val plugin: MineStamp by inject()

    suspend fun loadConfig() {
        withContext(Dispatchers.IO) {
            plugin.logger.info("Loading config.")
            val configFile = plugin.dataFolder.resolve("config.json")
            if (!configFile.exists()) {
                val defaultEmojiConfig = LocalConfig()
                configFile.parentFile.mkdirs()
                configFile.createNewFile()
                configFile.writeText(json.encodeToString(defaultEmojiConfig))
            }
            val localConfig = json.decodeFromString<LocalConfig>(configFile.readText())
            loadKoinModules(module {
                single { localConfig }
            })
            LangUtils.loadLocale()
            if (localConfig.type == FileType.LOCAL) {
                plugin.logger.info("File type is local.")
                loadConfigForSingle()
            } else {
                plugin.logger.info("File type is s3. Loading from s3.")
                loadConfigForProxy()
            }
            makeFolder()
            loadImages()
        }
    }

    private suspend fun loadConfigForSingle() {
        withContext(Dispatchers.IO) {
            val randomConfigFile = plugin.dataFolder.resolve("random.json")
            val playerDefaultConfigFile = plugin.dataFolder.resolve("player-default.json")
            if (!randomConfigFile.exists()) {
                randomConfigFile.parentFile.mkdirs()
                randomConfigFile.createNewFile()
                val resourceStream = plugin.javaClass.getResourceAsStream("/default-random.json")
                if (resourceStream != null) {
                    randomConfigFile.writeText(resourceStream.bufferedReader().readText())
                } else {
                    plugin.logger.warning("default-random.json is not found.")
                }
            }
            if (!playerDefaultConfigFile.exists()) {
                val defaultPlayerConfig = PlayerDefaultEmojiConfigData()
                playerDefaultConfigFile.parentFile.mkdirs()
                playerDefaultConfigFile.createNewFile()
                playerDefaultConfigFile.writeText(json.encodeToString(defaultPlayerConfig))
            }
            val randomConfig = json.decodeFromString<HashMap<String, Int>>(randomConfigFile.readText())
            val playerDefaultConfig =
                json.decodeFromString<PlayerDefaultEmojiConfigData>(playerDefaultConfigFile.readText())
            loadKoinModules(module {
                single { randomConfig }
                single { playerDefaultConfig }
            })
        }
    }

    private suspend fun loadConfigForProxy() {
        withContext(Dispatchers.IO) {
            val s3 = getS3Client()
            val s3Config = get<LocalConfig>().s3Config ?: throw IllegalStateException("S3 config is not found")
            if (!s3.doesBucketExistV2(s3Config.bucket)) {
                s3.createBucket(s3Config.bucket)
            }
            if (!s3.doesObjectExist(s3Config.bucket, "random.json")) {
                val request = PutObjectRequest(
                    s3Config.bucket, "random.json", plugin.javaClass.getResourceAsStream("/default-random.json"), null
                )
                request.requestClientOptions.readLimit = 10485760
                s3.putObject(
                    request
                )
            }
            if (!s3.doesObjectExist(s3Config.bucket, "player-default.json")) {
                val defaultPlayerConfig = PlayerDefaultEmojiConfigData()
                s3.putObject(
                    s3Config.bucket,
                    "player-default.json",
                    json.encodeToString(defaultPlayerConfig).byteInputStream(),
                    null
                )
            }
            val randomConfig = json.decodeFromString<HashMap<String, Int>>(
                s3.getObjectAsString(s3Config.bucket, "random.json")
            )
            val playerDefaultConfig = json.decodeFromString<PlayerDefaultEmojiConfigData>(
                s3.getObjectAsString(s3Config.bucket, "player-default.json")
            )

            loadKoinModules(module {
                single { randomConfig }
                single { playerDefaultConfig }
            })
        }
    }

    private suspend fun makeFolder() {
        withContext(Dispatchers.IO) {
            if (!plugin.dataFolder.exists()) {
                plugin.dataFolder.mkdir()
            }
            if (!plugin.dataFolder.resolve("image").exists()) {
                plugin.dataFolder.resolve("image").mkdir()
                val file = plugin.dataFolder.resolve("image").resolve("test.jpg")
                val inputStream = plugin.javaClass.classLoader.getResourceAsStream("test.jpg")
                if (inputStream != null) {
                    file.writeBytes(inputStream.readAllBytes())
                }
            }
            if (get<LocalConfig>().type == FileType.S3) {
                val s3Client = getS3Client()
                val s3Config = get<LocalConfig>().s3Config!!
                if (s3Client.doesBucketExistV2(s3Config.bucket).not()) {
                    s3Client.createBucket(s3Config.bucket)
                }
                s3Client.putObject(s3Config.bucket, "image/", "")
                val inputStream = plugin.javaClass.classLoader.getResourceAsStream("test.jpg")
                if (inputStream != null) {
                    val req = PutObjectRequest(s3Config.bucket, "image/test.jpg", inputStream, null)
                    req.requestClientOptions.readLimit = 1024 * 1024 * 10
                    s3Client.putObject(req)
                } else {
                    throw IllegalStateException("test.jpg is not found")
                }
            }
        }
    }

    private fun loadImages() {
        plugin.launch {
            val localConfig = get<LocalConfig>()
            if (localConfig.type == FileType.LOCAL) {
                val imageList = plugin.dataFolder.resolve("image").listFiles()?.map { it.name } ?: emptyList()
                loadKoinModules(module {
                    single { ImageListData(imageList) }
                })
            } else {
                val s3Client = getS3Client()
                val s3Config = localConfig.s3Config ?: throw IllegalStateException("S3 config is not found")
                val imageList = s3Client.listObjectsV2(
                    s3Config.bucket, "image/"
                ).objectSummaries.map { it.key.removePrefix("image/") }.filter { it.isNotEmpty() }
                loadKoinModules(module {
                    single { ImageListData(imageList) }
                })
            }
        }
    }
}

