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
    implementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    jvmArgs = listOf("-Xmx16g")
}
