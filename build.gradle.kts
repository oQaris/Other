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
    implementation("com.github.demidko:aot:2021.11.17")
    implementation("com.vdurmont:emoji-java:5.1.1")
    // Работа с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.vk.api:sdk:1.0.14")
    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    // Логгер
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

/*tasks.withType<Test> {
    minHeapSize = "512m"
    maxHeapSize = "32768m"
    jvmArgs = listOf("-XX:MaxPermSize=32768m")
}*/

application {
    mainClass.set("MainKt")
}
