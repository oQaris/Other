plugins {
    kotlin("jvm")
    application
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation(kotlin("test"))

    // Комбинаторика и решатели
    //todo избавиться
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
    // Text
    implementation("com.github.demidko:aot:2022.05.16")
    implementation(files("/libs/YandexLinguisticBundle-1.1.jar"))
    // Audio
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("com.alphacephei:vosk:0.3.38")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
