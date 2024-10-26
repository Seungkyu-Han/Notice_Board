plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


repositories {
    mavenCentral()
}


repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":user:persistence"))
    implementation(project(":security"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")

    //MONGO
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")


    //lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    //COROUTINE
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3")


    //AOP
    implementation("org.springframework.boot:spring-boot-starter-aop:3.1.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}