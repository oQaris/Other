import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
//    kotlin("plugin.serialization") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("me.champeau.jmh") version "0.7.1"
    application
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://packages.jetbrains.team/maven/p/skija/maven")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

    // Лексический анализатор
    implementation("com.github.javaparser:javaparser-core:3.25.8")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.8")

    // Micro Benchmark
    jmh("org.openjdk.jmh:jmh-core:1.36")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.36")
    // this is the line that solves the missing /META-INF/BenchmarkList error
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.36")

    implementation("ru.tinkoff.piapi:java-sdk-core:1.5")
    // Графический интерфейс
    api("org.jetbrains.skija:skija-windows:0.93.6")
    implementation("org.openjfx:javafx-controls:16")
    implementation("org.openjfx:javafx-fxml:16")
    // Комбинаторика
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
    implementation("com.ezylang:EvalEx:3.0.4")
    // NLP
    implementation("com.github.demidko:aot:2022.05.16")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("com.alphacephei:vosk:0.3.38")
    // Работа с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jsoup:jsoup:1.15.3")
    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Сериализация
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.4.1")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    // Логгер
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation(kotlin("stdlib-jdk8"))
}

javafx {
    version = "12"
    modules("javafx.controls", "javafx.fxml")
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
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}