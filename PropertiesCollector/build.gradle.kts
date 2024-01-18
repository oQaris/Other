import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    application
}

group = "me.oqaris"
version = "1.0"

application {
    mainClass.set("bot_version_updater/BotVersionUpdaterKt")
}

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.3.0") {
            exclude("com.android.tools.build")
        }
    }
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.4.1")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<proguard.gradle.ProGuardTask>("proguard") {
    configuration("proguard-rules.pro")
}

tasks.shadowJar {
    archiveFileName.set("bvu.jar")
    mergeServiceFiles()
    minimize()
    finalizedBy(tasks.getByName("proguard"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
