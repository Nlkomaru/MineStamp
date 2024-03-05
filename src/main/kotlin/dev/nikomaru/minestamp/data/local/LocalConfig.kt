package dev.nikomaru.minestamp.data.local

import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig(
    val s3 : S3Config
)

@Serializable
data class S3Config(
    val url: String,
    val accessKey: String,
    val secretKey: String
)