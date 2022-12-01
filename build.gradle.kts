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
    implementation(files("/libs/YandexLinguisticBundle-1.1.jar"))
    implementation("net.java.dev.jna:jna:5.12.1")
    implementation("com.alphacephei:vosk:0.3.38")
    // Работа с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.vk.api:sdk:1.0.14")
    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.4.1")
    // Дата
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")
    // Логгер
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-api:2.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("-Xmx8g")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    // ТУТ МЕНЯЕТСЯ СТАРТОВЫЙ КЛАСС ДЛЯ JAR
    mainClass.set("properties_collector.StarterKt")
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
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
