plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
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
    // Работа с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.vk.api:sdk:1.0.14")
    // Телеграм бот
    implementation("dev.inmo:tgbotapi:3.2.7")
    implementation("dev.inmo:tgbotapi.core:3.2.7")
    implementation("dev.inmo:tgbotapi.api:3.2.7")
    implementation("dev.inmo:tgbotapi.utils:3.2.7")
    // Корутины
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.4.1")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    // Логгер
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("ch.qos.logback:logback-classic:1.4.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("mastermind.tgbot.BotKt")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        configurations.compileClasspath.get().forEach {
            from(if (it.isDirectory) it else zipTree(it))
        }
    }

    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.ExperimentalStdlibApi"
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

/*tasks.withType<Test> {
    minHeapSize = "512m"
    maxHeapSize = "32768m"
    jvmArgs = listOf("-XX:MaxPermSize=32768m")
}*/
