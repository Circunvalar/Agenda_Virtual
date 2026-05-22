# Multi-stage Dockerfile: build the jar with Maven, then create a slim runtime image

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy only what is necessary for Maven to build (use mvnw if present)
COPY mvnw pom.xml .mvn/ ./
COPY pom.xml ./
COPY src ./src

# If mvnw is present make it executable and use it (ensures reproducible build)
RUN if [ -f ./mvnw ]; then chmod +x ./mvnw && ./mvnw -B -DskipTests package; else mvn -B -DskipTests package; fi

FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /workspace/target/*.jar app.jar

# Copy entrypoint script and make it executable
COPY entrypoint.sh ./
RUN chmod +x ./entrypoint.sh

# Default port (can be overridden via environment variable at runtime)
ENV APP_PORT=4444
EXPOSE ${APP_PORT}

# Use the PORT provided by platforms like Render if present, otherwise fall back to APP_PORT
ENTRYPOINT ["./entrypoint.sh"]
