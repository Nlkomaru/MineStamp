plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    val paperVersion = "1.20.4-R0.1-SNAPSHOT"
    val mccoroutineVersion = "2.15.0"
    val lampVersion = "3.1.9"
    val koinVersion = "3.5.3"
    val coroutineVersion = "1.8.0"
    val serializationVersion = "1.6.2"
    val protocolLibVersion = "5.2.0-SNAPSHOT"
    val jwtVersion = "4.4.0"

    library(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("com.github.Revxrsal.Lamp:common:$lampVersion")
    implementation("com.github.Revxrsal.Lamp:bukkit:$lampVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutineVersion")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutineVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")

    implementation("com.amazonaws:aws-java-sdk-s3:1.12.696")
    library("org.apache.commons:commons-math3:3.6.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:$protocolLibVersion")
    library("com.auth0:java-jwt:$jwtVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.javaParameters = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    build {
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.20.4")
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}


bukkit {
    name = "MineStamp"
    version = "1.0.0"
    website = "https://github.com/Nlkomaru/MineStamp"
    author = "Nikomaru"
    main = "dev.nikomaru.minestamp.MineStamp"
    apiVersion = "1.20"
    depend = listOf("ProtocolLib")
    description = "A Stamp plugin for Minecraft"
    libraries = listOf(
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.15.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.15.0"
    )
}
