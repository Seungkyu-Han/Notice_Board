plugins {
    kotlin("jvm") version "2.0.0"
}

group = "seungkyu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}