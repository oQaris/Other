plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    application
}

group = "me.oqaris"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies{
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.4.1")
}

tasks.shadowJar {
    archiveFileName.set("bot_version_updater.jar")
    mergeServiceFiles()
    minimize()
}

application {
    mainClass.set("bot_version_updater/BotVersionUpdaterKt")
}