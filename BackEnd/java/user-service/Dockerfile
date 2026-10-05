# Multi-stage build for Java services
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copy pom.xml and build directly (dependencies downloaded during build)
COPY pom.xml .
RUN apk add --no-cache maven

# Copy source and build (dependencies downloaded as needed)
COPY src ./src
RUN mvn package -DskipTests -B

# Final image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
