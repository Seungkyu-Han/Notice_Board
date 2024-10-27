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
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    testImplementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")

    testImplementation("javax.xml.bind:jaxb-api:2.3.1")

    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")

    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.9.0")

    testImplementation("org.mockito:mockito-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.projectreactor:reactor-test:3.6.10")

    // reactor test
    testImplementation("io.projectreactor:reactor-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}