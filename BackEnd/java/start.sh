# AI Café Platform - Startup Script
# Run this script to start all microservices

echo "========================================="
echo "  AI Café Platform - Microservices"
echo "========================================="

# Start infrastructure
echo "Starting PostgreSQL & Redis..."
docker compose up -d postgres redis

# Wait for infrastructure
echo "Waiting for database..."
sleep 10

# Build all services
echo "Building all services..."
mvn clean package -DskipTests

# Start services in order
echo "Starting Service Registry (Eureka)..."
cd service-registry && java -jar target/*.jar &
sleep 10

echo "Starting User Service..."
cd ../user-service && java -jar target/*.jar &
sleep 5

echo "Starting Payment Service..."
cd ../payment-service && java -jar target/*.jar &
sleep 5

echo "Starting Order Service..."
cd ../order-service && java -jar target/*.jar &
sleep 5

echo "Starting Member Service..."
cd ../member-service && java -jar target/*.jar &
sleep 5

echo "Starting Admin Service..."
cd ../admin-service && java -jar target/*.jar &
sleep 5

echo "Starting Notification Service..."
cd ../notification-service && java -jar target/*.jar &
sleep 5

echo "Starting Cafe Service..."
cd ../cafe-service && java -jar target/*.jar &
sleep 5

echo "Starting API Gateway..."
cd ../api-gateway && java -jar target/*.jar &

echo ""
echo "========================================="
echo "  Services Started!"
echo "========================================="
echo "API Gateway:      http://localhost:8080"
echo "Service Registry: http://localhost:8761"
echo "User Service:     http://localhost:8081"
echo "Payment Service:  http://localhost:8082"
echo "Order Service:    http://localhost:8083"
echo "Member Service:   http://localhost:8084"
echo "Admin Service:    http://localhost:8085"
echo "Notification:     http://localhost:8086"
echo "Cafe Service:     http://localhost:8087"
echo "========================================="