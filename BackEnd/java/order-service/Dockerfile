# Simple Dockerfile using pre-built JAR
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from target directory
COPY target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
