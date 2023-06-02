val kotlinVersion = "1.8.20"

plugins {
    id("java")
    kotlin("jvm") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.11"
    id("xyz.jpenilla.run-paper") version " 2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    kotlin("plugin.serialization") version kotlinVersion
}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
}
val paperVersion = "1.19.4-R0.1-SNAPSHOT"
val lampVersion = "3.1.5"
val vaultVersion = "1.7"
val mccoroutineVersion = "2.11.0"

dependencies {
    compileOnly("io.papermc.paper", "paper-api", paperVersion)

    library(kotlin("stdlib"))

    compileOnly("com.github.MilkBowl", "VaultAPI", vaultVersion)

    implementation("com.github.Revxrsal.Lamp","common",lampVersion)
    implementation("com.github.Revxrsal.Lamp","bukkit",lampVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinVersion)

    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", mccoroutineVersion)
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", mccoroutineVersion)
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
}

tasks {
    runServer {
        minecraftVersion("1.19.4")
    }
}


bukkit {
    name = "Template" // need to change
    version = "miencraft_plugin_version"
    website = "https://github.com/Nlkomaru/NoticeTemplate"  // need to change

    main = "$group.template.Template"  // need to change

    apiVersion = "1.19"
    libraries = listOf("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.11.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.11.0")
}