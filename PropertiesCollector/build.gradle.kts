plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "me.oqaris"
version = "1.0"

repositories {
    mavenCentral()
}



tasks.shadowJar {
    archiveFileName.set("pc.jar")
    mergeServiceFiles()
    minimize()
}
