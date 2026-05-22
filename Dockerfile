# Multi-stage Dockerfile: build the jar with Maven, then create a slim runtime image

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Install Maven in the build stage (keeps the runtime image slim)
RUN apt-get update \
	&& apt-get install -y maven \
	&& rm -rf /var/lib/apt/lists/*

# Copy only what is necessary for Maven to build
COPY pom.xml ./
COPY src ./src

# Build the jar
RUN mvn -B -DskipTests package

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
