plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.ethyllium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    implementation("org.eclipse.jetty:jetty-server:11.0.24")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.24")
    implementation ("com.ethyllium:grpc-service:1.0.0")
    testImplementation("io.mockk:mockk:1.14.7")
}


kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}