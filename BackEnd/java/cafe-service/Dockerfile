# Simple Dockerfile using pre-built JAR
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from target directory (context is ./BackEnd/java/cafe-service)
COPY target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
