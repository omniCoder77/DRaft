plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "com.ethyllium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jakarta.servlet)
    implementation(libs.jetty.server)
    implementation(libs.jetty.servlet)
    implementation(libs.grpc.service)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}