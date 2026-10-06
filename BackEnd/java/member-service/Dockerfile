# Multi-stage build for Java services
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

# Copy member-service
WORKDIR /build
COPY member-service/pom.xml ./member-service/
COPY member-service/src ./member-service/src

# Build member-service
WORKDIR /build/member-service
RUN mvn clean package -DskipTests -B

# Final image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/member-service/target/*.jar app.jar

# Expose port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
