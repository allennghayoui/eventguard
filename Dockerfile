# Stage 1: build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy Gradle wrapper and build files first for better layer caching
COPY gradlew settings.gradle.kts ./
COPY gradle gradle
COPY app/build.gradle.kts app/build.gradle.kts

# Prime the dependency cache. This layer only invalidates when build
# files change, not when source changes.
RUN ./gradlew --no-daemon :app:dependencies > /dev/null 2>&1 || true

# Now copy source and build
COPY app/src app/src
RUN ./gradlew --no-daemon :app:bootJar -x test

# Stage 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user for the running app
RUN useradd --system --create-home --shell /bin/false eventguard
USER eventguard

COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

# JVM options: heap sized for a small container, exit on OOM
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
