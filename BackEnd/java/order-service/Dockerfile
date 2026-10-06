# Multi-stage build for Java services
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Install build tools
RUN apk add --no-cache maven bash

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build with Lombok annotation processing
RUN mvn clean package -DskipTests -B

# Final image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
