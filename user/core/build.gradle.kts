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
    implementation(project(":aop"))

    //WEBFLUX
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")


    // reactor test
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    testImplementation("io.projectreactor:reactor-test")

    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
    // mockito
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation ("org.mockito:mockito-core")

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