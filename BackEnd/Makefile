# AI Café Platform - Development Commands

.PHONY: help build up down restart logs clean db ps

# Colors
GREEN  := \033[0;32m
YELLOW := \033[0;33m
BLUE   := \033[0;34m
NC     := \033[0m

help: ## Show this help message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "$(BLUE)%-15s$(NC) %s\n", $$1, $$2}'

# Development
up: ## Start all services
	docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo "  API Gateway: http://localhost:3000"
	@echo "  User Service: http://localhost:3004"
	@echo "  Payment Service: http://localhost:3005"
	@echo "  Order Service: http://localhost:3007"
	@echo "  Member Service: http://localhost:3006"
	@echo "  AI Gateway: http://localhost:4000"

down: ## Stop all services
	docker-compose down

restart: ## Restart all services
	docker-compose restart

build: ## Build all services
	docker-compose build --no-cache

build-fast: ## Build all services (with cache)
	docker-compose build

logs: ## Show logs (all services)
	docker-compose logs -f

logs-gateway: ## Show API Gateway logs
	docker-compose logs -f api-gateway

logs-user: ## Show User Service logs
	docker-compose logs -f user-service

logs-order: ## Show Order Service logs
	docker-compose logs -f order-service

logs-ai: ## Show AI Gateway logs
	docker-compose logs -f ai-gateway

clean: ## Remove all containers, volumes, and images
	docker-compose down -v --remove-orphans
	docker system prune -f

clean-all: ## Full cleanup including build cache
	docker-compose down -v --remove-orphans
	docker system prune -af

# Database
db-reset: ## Reset database (WARNING: deletes all data)
	docker-compose down -v
	docker-compose up -d postgres
	@sleep 5
	docker-compose up -d

db-logs: ## Show PostgreSQL logs
	docker-compose logs -f postgres

db-shell: ## Connect to PostgreSQL shell
	docker exec -it aicafe-postgres psql -U aicafe -d aicafe

# Status
ps: ## Show running containers
	docker-compose ps

status: ## Show status of all services
	@echo "$(BLUE)Services Status:$(NC)"
	@docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

health: ## Check health of all services
	@echo "$(BLUE)Health Check:$(NC)"
	@echo "PostgreSQL: $$(docker exec aicafe-postgres pg_isready -U aicafe >/dev/null 2>&1 && echo '$(GREEN)✓ Healthy$(NC)' || echo '$(YELLOW)✗ Unhealthy$(NC)')"
	@echo "Redis: $$(docker exec aicafe-redis redis-cli ping >/dev/null 2>&1 && echo '$(GREEN)✓ Healthy$(NC)' || echo '$(YELLOW)✗ Unhealthy$(NC)')"
	@echo "API Gateway: $$(curl -s http://localhost:3000/health >/dev/null 2>&1 && echo '$(GREEN)✓ Healthy$(NC)' || echo '$(YELLOW)✗ Unhealthy$(NC)')"
	@echo "User Service: $$(curl -s http://localhost:3004/actuator/health >/dev/null 2>&1 && echo '$(GREEN)✓ Healthy$(NC)' || echo '$(YELLOW)✗ Unhealthy$(NC)')"
	@echo "AI Gateway: $$(curl -s http://localhost:4000/health >/dev/null 2>&1 && echo '$(GREEN)✓ Healthy$(NC)' || echo '$(YELLOW)✗ Unhealthy$(NC)')"

# Development helpers
dev-gateway: ## Start only API Gateway in foreground
	docker-compose up api-gateway

dev-java: ## Start Java services
	docker-compose up user-service payment-service order-service member-service

dev-python: ## Start Python AI Gateway
	docker-compose up ai-gateway

# Testing
test-api: ## Test API Gateway endpoints
	@echo "$(BLUE)Testing API Gateway...$(NC)"
	@curl -s http://localhost:3000/health | jq . || curl -s http://localhost:3000/health

# Environment
env-create: ## Create .env file from template
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "$(GREEN)✓ .env file created from template$(NC)"; \
		cat .env; \
	else \
		echo "$(YELLOW)⚠ .env file already exists$(NC)"; \
	fi

# Production (requires production profile)
prod-up: ## Start production stack with Nginx
	docker-compose --profile production up -d

prod-down: ## Stop production stack
	docker-compose --profile production down
