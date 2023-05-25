plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
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
    archiveFileName.set("AutoDeployer.jar")
    mergeServiceFiles()
    minimize()
}
