# Multi-stage Dockerfile for Spring Boot services
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
COPY common/pom.xml common/
COPY user-service/pom.xml user-service/
COPY payment-service/pom.xml payment-service/
COPY order-service/pom.xml order-service/
COPY member-service/pom.xml member-service/
COPY admin-service/pom.xml admin-service/
COPY notification-service/pom.xml notification-service/
COPY cafe-service/pom.xml cafe-service/
COPY api-gateway/pom.xml api-gateway/
COPY service-registry/pom.xml service-registry/

# Download dependencies
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Copy jar file (service-specific)
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Set permissions
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]