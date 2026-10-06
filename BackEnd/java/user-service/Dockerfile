# Multi-stage build for user-service
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Install build tools
RUN apk add --no-cache maven bash

# Copy common module first
COPY common/pom.xml ./common/
COPY common/src ./common/src

# Install common module
WORKDIR /build/common
RUN mvn install -DskipTests -B

# Copy user-service
WORKDIR /build
COPY user-service/pom.xml ./user-service/
COPY user-service/src ./user-service/src

# Build user-service
WORKDIR /build/user-service
RUN mvn clean package -DskipTests -B

# Final image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/user-service/target/*.jar app.jar

# Expose port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
