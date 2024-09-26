
plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory)

}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"//Don't change

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly(libs.paper.api)

    implementation(libs.bundles.commands)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.coroutines)

    compileOnly(libs.protocolLib)

    implementation(libs.koin.core)

    implementation(libs.awsJavaSdkS3)
    implementation(libs.commonsMath3)
    implementation(libs.javaJwt)

}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
        kotlinOptions.javaParameters = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    build {
        dependsOn("shadowJar")
    }
    runServer {
        minecraftVersion("1.21")
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}


sourceSets.main {
    resourceFactory {
        bukkitPluginYaml {
            name = rootProject.name
            version = "versionPlaceholder" //Don't change
            website = "https://github.com/Nlkomaru/AdvancedShopFinder"
            main = "$group.minestamp.MineStamp"
            apiVersion = "1.20"
            libraries = libs.bundles.coroutines.asString()
        }
    }
}

fun Provider<ExternalModuleDependencyBundle>.asString(): List<String> {
    return this.get().map { dependency ->
        "${dependency.group}:${dependency.name}:${dependency.version}"
    }
}
