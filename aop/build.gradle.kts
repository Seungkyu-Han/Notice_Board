plugins {
    kotlin("jvm")
}

group = "seungkyu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":security"))
    implementation("org.springframework.boot:spring-boot-starter-aop:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}