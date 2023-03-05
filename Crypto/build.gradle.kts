plugins {
    kotlin("jvm")
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
}