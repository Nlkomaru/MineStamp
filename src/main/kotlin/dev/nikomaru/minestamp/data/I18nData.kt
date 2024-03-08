package dev.nikomaru.minestamp.data

import java.util.*
import kotlin.collections.HashMap

data class I18nData(
    val serverDefaultLocale : String = "en_US",
    val propertiesMap: HashMap<String, Properties> = hashMapOf()
)
