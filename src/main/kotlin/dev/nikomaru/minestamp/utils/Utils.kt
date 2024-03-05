package dev.nikomaru.minestamp.utils

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import dev.nikomaru.minestamp.data.local.LocalConfig
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.minimessage.MiniMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


object Utils: KoinComponent {
    val json = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
    }
    val mm = MiniMessage.miniMessage()

    fun getS3Client(): AmazonS3 {
        val config = get<LocalConfig>()
        val endpointConfiguration = AwsClientBuilder.EndpointConfiguration(
            config.s3.url, Regions.DEFAULT_REGION.name
        )
        val credential = BasicAWSCredentials(config.s3.accessKey, config.s3.secretKey)
        val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
            .withPathStyleAccessEnabled(true).withCredentials(AWSStaticCredentialsProvider(credential)).build()

        return s3Client
    }
}