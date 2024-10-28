plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
//    kotlin("plugin.allopen") version "1.8.22"
}


group = "seungkyu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //MONGO
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    //REACTOR
    implementation("io.projectreactor:reactor-tools")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}