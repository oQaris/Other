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
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.4.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("bot_version_updater.jar")
    mergeServiceFiles()
    minimize()
}

application {
    mainClass.set("bot_version_updater/BotVersionUpdaterKt")
}