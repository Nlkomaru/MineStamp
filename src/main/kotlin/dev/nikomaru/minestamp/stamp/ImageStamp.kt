package dev.nikomaru.minestamp.stamp

import com.amazonaws.services.s3.model.GetObjectRequest
import dev.nikomaru.minestamp.data.FileType
import dev.nikomaru.minestamp.data.LocalConfig
import dev.nikomaru.minestamp.utils.Utils.getS3Client
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import javax.imageio.ImageIO

class ImageStamp(shortCode: String): AbstractStamp(shortCode), KoinComponent {
    init {
        val config = get<LocalConfig>()
        if (config.type == FileType.LOCAL) {
            image = ImageIO.read(
                plugin.dataFolder.resolve("image/${shortCode.removePrefix("!")}")
            )
        } else {
            val s3Config = config.s3Config!!
            val s3Client = getS3Client()
            val req = GetObjectRequest(s3Config.bucket, "image/${shortCode.removePrefix("!")}")
            req.requestClientOptions.readLimit = 1024 * 1024 * 10
            image = ImageIO.read(s3Client.getObject(req).objectContent)

        }

    }
}