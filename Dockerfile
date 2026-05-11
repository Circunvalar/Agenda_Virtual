FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 4444

ENTRYPOINT ["java","-jar","app.jar"]