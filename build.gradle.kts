plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    // Комбинаторика
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
    // NLP
    implementation("com.github.demidko:aot:2022.05.16")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("com.alphacephei:vosk:0.3.45")
    // Работа с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jsoup:jsoup:1.15.4")
    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    // Логгер
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
}

application {
    // ТУТ МЕНЯЕТСЯ СТАРТОВЫЙ КЛАСС ДЛЯ JAR
    mainClass.set("properties_collector.StarterKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

tasks.jar { enabled = false }
artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + ".jar")
    minimize()
}

tasks.register<proguard.gradle.ProGuardTask>("proguard") {
    //keep("me.oqaris."+application.mainClass.get())
    configuration("proguard-rules.pro")
}

// автоматически запускать задачу proguard после сборки
tasks.shadowJar.get().finalizedBy(tasks.getByName("proguard"))

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("-Xmx8g")
}
