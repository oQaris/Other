plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks.shadowJar {
    archiveFileName.set("pc.jar")
}
