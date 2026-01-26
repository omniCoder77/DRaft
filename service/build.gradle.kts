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
    testImplementation("io.grpc:grpc-testing:1.68.0")
    testImplementation("io.grpc:grpc-all:1.68.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}