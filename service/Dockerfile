FROM eclipse-temurin:21-jdk AS builder
ARG GRADLE_USER_HOME=/root/.gradle
ENV GRADLE_USER_HOME=${GRADLE_USER_HOME}
WORKDIR /app

COPY grpc-service/build.gradle.kts ./grpc-service/
COPY grpc-service/settings.gradle.kts ./grpc-service/
COPY grpc-service/gradlew ./grpc-service/
COPY grpc-service/gradlew.bat ./grpc-service/
COPY grpc-service/gradle ./grpc-service/gradle
COPY grpc-service/src ./grpc-service/src

COPY service/gradlew service/
COPY service/gradle service/gradle

RUN --mount=type=cache,target=${GRADLE_USER_HOME} \
    --mount=type=cache,target=${GRADLE_USER_HOME}/wrapper \
    chmod +x service/gradlew && service/gradlew --version --no-daemon

COPY service/settings.gradle.kts service/
COPY service/*.lockfile service/
COPY service/build.gradle.kts service/

WORKDIR /app/service

RUN --mount=type=cache,target=${GRADLE_USER_HOME} \
    --mount=type=cache,target=${GRADLE_USER_HOME}/wrapper \
    ./gradlew dependencies --no-daemon --console=plain

COPY service/src ./src

RUN --mount=type=cache,target=${GRADLE_USER_HOME} \
    --mount=type=cache,target=${GRADLE_USER_HOME}/wrapper \
    ./gradlew shadowJar -x test --no-daemon --console=plain

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/service/build/libs/*-all.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseZGC","-XX:+UseContainerSupport","-Djava.security.egd=state:/dev/./urandom","-jar","app.jar"]