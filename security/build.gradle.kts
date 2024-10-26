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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}