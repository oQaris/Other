import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    implementation(kotlin("stdlib-jdk8"))
}

tasks.shadowJar {
    archiveFileName.set("AutoDeployer.jar")
    mergeServiceFiles()
    minimize()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}