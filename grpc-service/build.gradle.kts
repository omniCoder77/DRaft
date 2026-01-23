plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf)
}

group = "com.ethyllium"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.netty)

    // api for exposing dependencies to consumers
    api(libs.protobuf.kotlin)
    api(libs.grpc.stub)
    api(libs.kotlinx.coroutines.core)
    api(libs.grpc.kotlin.stub)

    testImplementation(kotlin("test"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.3:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
                create("grpckt")
            }
            task.builtins {
                create("kotlin")
            }
        }
    }
}


kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}