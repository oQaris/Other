plugins {
    kotlin("jvm")
//    kotlin("plugin.serialization")
    application
    java
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    // NLP
    implementation(projects.nlp)
    implementation("com.vdurmont:emoji-java:5.1.1")
    // Сериализация
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.4.1")
    // Работа с сетью
    implementation("com.vk.api:sdk:1.0.14")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
