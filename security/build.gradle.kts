plugins {
    kotlin("jvm") version "2.0.0"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")

    implementation("io.jsonwebtoken:jjwt:0.9.1")

    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")

    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")

    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}