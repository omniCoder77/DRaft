plugins {
    kotlin("jvm") version "2.0.21"
    application
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
}

application {
    mainClass.set("com.ethyllium.MainKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}