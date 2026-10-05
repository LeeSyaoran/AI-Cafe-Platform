# AI Café Platform - Infrastructure Design

## 1. Infrastructure Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                      AWS Cloud Infrastructure                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Global Edge (CloudFront)                    │  │
│  │  • Static assets CDN                                          │  │
│  │  • DDoS protection                                            │  │
│  │  • WAF rules                                                  │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Application Load Balancer                   │  │
│  │  • SSL termination                                            │  │
│  │  • Path-based routing                                          │  │
│  │  • Health checks                                              │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                              │                                       │
│              ┌───────────────┼───────────────┐                     │
│              ▼               ▼               ▼                     │
│  ┌─────────────────┐ ┌─────────────┐ ┌─────────────────┐         │
│  │   ECS Cluster    │ │   Lambda    │ │   EC2 Instance  │         │
│  │   (Fargate)     │ │ (AI Gateway) │ │  (Workspace IDE) │         │
│  │                 │ │              │ │                 │         │
│  │ ┌─────────────┐ │ │             │ │                 │         │
│  │ │ API Gateway │ │ │             │ │                 │         │
│  │ └─────────────┘ │ │             │ │                 │         │
│  │ ┌─────────────┐ │ │             │ │                 │         │
│  │ │   Workers   │ │ │             │ │                 │         │
│  │ └─────────────┘ │ │             │ │                 │         │
│  └─────────────────┘ └─────────────┘ └─────────────────┘         │
│                                                                      │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                         Data Layer                             │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐ │  │
│  │  │  RDS        │ │ ElastiCache │ │        S3              │ │  │
│  │  │  PostgreSQL │ │    Redis    │ │   (File Storage)       │ │  │
│  │  │             │ │             │ │                        │ │  │
│  │  │ Primary     │ │ Session     │ │ Static Assets          │ │  │
│  │  │ Replica     │ │ Cache       │ │ User Files            │ │  │
│  │  │             │ │ Queue       │ │ Backups               │ │  │
│  │  └─────────────┘ └─────────────┘ └─────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Monitoring & Logging                       │  │
│  │  CloudWatch │ CloudTrail │ Datadog │ Sentry │              │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. VPC Architecture

### 2.1 VPC Design

```
┌─────────────────────────────────────────────────────────────────────┐
│                          VPC: 10.0.0.0/16                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ Public Subnets (Application Tier)        10.0.1.0/24        │   │
│  │                                               10.0.2.0/24    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │   │
│  │  │  ALB/NLB   │  │  NAT GW    │  │   Bastion   │        │   │
│  │  │            │  │            │  │   Host      │        │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘        │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                              │                                       │
│                              │ Route to IGW                        │
│                              ▼                                       │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ Private Subnets (Compute Tier)          10.0.11.0/24        │   │
│  │                                               10.0.12.0/24    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │   │
│  │  │  ECS Tasks │  │  ECS Tasks  │  │  Lambda    │        │   │
│  │  │ (API)      │  │ (Workers)   │  │ (AI GW)   │        │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘        │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                              │                                       │
│                              │ NAT for outbound                    │
│                              ▼                                       │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ Data Subnets (Data Tier)                    10.0.21.0/24    │   │
│  │                                               10.0.22.0/24    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │   │
│  │  │  RDS PG    │  │ ElastiCache │  │    S3      │        │   │
│  │  │  (Primary) │  │  (Redis)   │  │  (VPC)    │        │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘        │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Security Groups

| Security Group | Inbound | Outbound | Purpose |
|---------------|---------|----------|---------|
| **sg-alb** | HTTP/HTTPS (0.0.0.0/0) | To sg-ecs | ALB to ECS |
| **sg-ecs-api** | 80, 443 (from sg-alb) | To sg-rds, sg-redis | API containers |
| **sg-ecs-workers** | 6379 (from sg-ecs-api) | To sg-rds, sg-redis, S3 | Worker containers |
| **sg-rds** | 5432 (from sg-ecs-*) | - | PostgreSQL access |
| **sg-redis** | 6379 (from sg-ecs-*) | - | Redis access |
| **sg-bastion** | 22 (Admin IPs only) | To sg-ecs-api | SSH access |
| **sg-lambda** | VPC Interface | To sg-rds, sg-redis, S3 | Lambda in VPC |

## 3. Container Architecture

### 3.1 ECS Fargate Services

```yaml
# docker-compose.yml (local development)
version: '3.8'

services:
  api-gateway:
    build: ./backend/api-gateway
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=development
      - DATABASE_URL=postgresql://postgres:password@postgres:5432/aicafe
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis
    volumes:
      - ./backend/api-gateway:/app
      - /app/node_modules

  ai-gateway:
    build: ./backend/ai-gateway
    ports:
      - "8000:8000"
    environment:
      - PYTHON_ENV=development
      - DATABASE_URL=postgresql://postgres:password@postgres:5432/aicafe
      - REDIS_URL=redis://redis:6379
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}
    depends_on:
      - postgres
      - redis

  worker:
    build: ./backend/workers
    environment:
      - NODE_ENV=development
      - DATABASE_URL=postgresql://postgres:password@postgres:5432/aicafe
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=aicafe
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  postgres_data:
  redis_data:
```

### 3.2 Dockerfile Templates

```dockerfile
# backend/api-gateway/Dockerfile
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci --only=production

# Copy source
COPY . .

# Build TypeScript
RUN npm run build

# Production image
FROM node:20-alpine AS production

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

# Copy built files
COPY --from=builder --chown=nodejs:nodejs /app/dist ./dist
COPY --from=builder --chown=nodejs:nodejs /app/node_modules ./node_modules
COPY --from=builder --chown=nodejs:nodejs /app/package.json ./

USER nodejs

EXPOSE 3000

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1

CMD ["node", "dist/index.js"]
```

```dockerfile
# backend/ai-gateway/Dockerfile
FROM python:3.12-slim AS builder

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source
COPY . .

# Production image
FROM python:3.12-slim AS production

WORKDIR /app

# Install runtime dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy from builder
COPY --from=builder /usr/local/lib/python3.12/site-packages /usr/local/lib/python3.12/site-packages
COPY --from=builder /app /app

# Create non-root user
RUN useradd -m -u 1001 appuser
USER appuser

EXPOSE 8000

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8000/health || exit 1

CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

### 3.3 ECS Task Definitions

```json
{
  "family": "aicafe-api-gateway",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "containerDefinitions": [
    {
      "name": "api-gateway",
      "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/aicafe/api-gateway:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 3000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "NODE_ENV",
          "value": "production"
        },
        {
          "name": "PORT",
          "value": "3000"
        }
      ],
      "secrets": [
        {
          "name": "DATABASE_URL",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:aicafe/database-url"
        },
        {
          "name": "REDIS_URL",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:aicafe/redis-url"
        },
        {
          "name": "JWT_SECRET",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:aicafe/jwt-secret"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/aicafe/api-gateway",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1"],
        "interval": 30,
        "timeout": 3,
        "retries": 3,
        "startPeriod": 10
      }
    }
  ]
}
```

## 4. Database Architecture

### 4.1 RDS PostgreSQL Setup

```yaml
# terraform/rds.tf
resource "aws_db_instance" "postgres" {
  identifier           = "aicafe-postgres"
  engine              = "postgres"
  engine_version      = "15.4"
  instance_class      = "db.r6g.large"
  
  allocated_storage     = 100
  max_allocated_storage = 500
  storage_type         = "gp3"
  storage_encrypted     = true
  
  # Multi-AZ for production
  multi_az            = true
  
  # Backup configuration
  backup_retention_period = 7
  backup_window         = "03:00-04:00"
  maintenance_window    = "mon:04:00-mon:05:00"
  
  # Network
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  
  # Credentials (use Secrets Manager in production)
  username = "postgres"
  password = random_password.db_password.result
  
  # Performance
  performance_insights_enabled = true
  monitoring_interval         = 60
  
  # Deletion protection
  deletion_protection = true
  
  # PostgreSQL settings
  parameters = [
    {
      name  = "max_connections"
      value = "500"
    },
    {
      name  = "shared_buffers"
      value = "256MB"
    },
    {
      name  = "effective_cache_size"
      value = "768MB"
    },
    {
      name  = "maintenance_work_mem"
      value = "256MB"
    },
    {
      name  = "checkpoint_completion_target"
      value = "0.9"
    },
    {
      name  = "wal_buffers"
      value = "16MB"
    },
    {
      name  = "default_statistics_target"
      value = "100"
    },
    {
      name  = "random_page_cost"
      value = "1.1"
    },
    {
      name  = "effective_io_concurrency"
      value = "200"
    },
    {
      name  = "work_mem"
      value = "4MB"
    },
    {
      name  = "min_wal_size"
      value = "1GB"
    },
    {
      name  = "max_wal_size"
      value = "4GB"
    }
  ]
}

# Read replica for analytics
resource "aws_db_instance" "postgres_replica" {
  identifier           = "aicafe-postgres-replica"
  engine              = "postgres"
  engine_version      = "15.4"
  instance_class      = "db.r6g.large"
  
  source_region        = "us-east-1"
  replicate_source_db   = aws_db_instance.postgres.identifier
  
  storage_type         = "gp3"
  storage_encrypted    = true
  
  # Network
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  
  # No deletion protection for replica
  deletion_protection = false
}
```

### 4.2 Redis ElastiCache Setup

```yaml
# terraform/redis.tf
resource "aws_elasticache_subnet_group" "redis" {
  name       = "aicafe-redis-subnet"
  subnet_ids = [aws_subnet.private_1.id, aws_subnet.private_2.id]
}

resource "aws_elasticache_parameter_group" "redis" {
  name   = "aicafe-redis"
  family = "redis7"
  
  parameter {
    name  = "maxmemory-policy"
    value = "allkeys-lru"
  }
  
  parameter {
    name  = "timeout"
    value = "300"
  }
  
  parameter {
    name  = "tcp-keepalive"
    value = "300"
  }
}

resource "aws_elasticache_replication_group" "redis" {
  replication_group_id       = "aicafe-redis"
  replication_group_description = "AI Café Redis Cluster"
  
  engine               = "redis"
  engine_version       = "7.0"
  node_type            = "cache.r7g.large"
  
  num_cache_clusters       = 2
  num_node_groups          = 1
  replicas_per_node_group   = 1
  
  # Network
  subnet_group_name         = aws_elasticache_subnet_group.redis.name
  security_group_ids        = [aws_security_group.redis.id]
  
  # Encryption
  at_rest_encryption_enabled = true
  transit_encryption_enabled  = true
  auth_token_enabled         = true
  
  # Backup
  auto_minor_version_upgrade = true
  snapshot_retention_limit   = 7
  snapshot_window           = "03:00-05:00"
  
  # Maintenance
  maintenance_window        = "mon:05:00-mon:06:00"
  
  # Deletion protection
  final_snapshot_identifier = "aicafe-redis-final"
  automatic_failover_enabled = true
  
  log_delivery_configuration {
    destination      = aws_cloudwatch_log_group.redis_slow.name
    destination_type = "cloudwatch-logs"
    log_format      = "json"
    log_type        = "slow-log"
  }
  
  log_delivery_configuration {
    destination      = aws_cloudwatch_log_group.redis_engine.name
    destination_type = "cloudwatch-logs"
    log_format       = "json"
    log_type        = "engine-log"
  }
}
```

## 5. CI/CD Pipeline

### 5.1 GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml
name: Deploy to AWS

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  AWS_REGION: us-east-1
  ECR_REPOSITORY: aicafe

jobs:
  # ========================================
  # Lint & Type Check
  # ========================================
  lint:
    name: Lint & Type Check
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: backend/api-gateway/package-lock.json
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run ESLint
        run: npm run lint
      
      - name: Run TypeScript check
        run: npm run type-check

  # ========================================
  # Unit Tests
  # ========================================
  test:
    name: Unit Tests
    runs-on: ubuntu-latest
    needs: lint
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run tests
        run: npm test -- --coverage
      
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info

  # ========================================
  # Build Docker Images
  # ========================================
  build:
    name: Build Docker Images
    runs-on: ubuntu-latest
    needs: test
    steps:
      - uses: actions/checkout@v4
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}
      
      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2
      
      - name: Build API Gateway image
        run: |
          docker build -t $ECR_REGISTRY/api-gateway:$GITHUB_SHA \
            ./backend/api-gateway
          docker tag $ECR_REGISTRY/api-gateway:$GITHUB_SHA \
            $ECR_REGISTRY/api-gateway:latest
      
      - name: Build AI Gateway image
        run: |
          docker build -t $ECR_REGISTRY/ai-gateway:$GITHUB_SHA \
            ./backend/ai-gateway
          docker tag $ECR_REGISTRY/ai-gateway:$GITHUB_SHA \
            $ECR_REGISTRY/ai-gateway:latest
      
      - name: Build Worker image
        run: |
          docker build -t $ECR_REGISTRY/worker:$GITHUB_SHA \
            ./backend/workers
          docker tag $ECR_REGISTRY/worker:$GITHUB_SHA \
            $ECR_REGISTRY/worker:latest
      
      - name: Push to ECR
        run: |
          docker push $ECR_REGISTRY/api-gateway:$GITHUB_SHA
          docker push $ECR_REGISTRY/api-gateway:latest
          docker push $ECR_REGISTRY/ai-gateway:$GITHUB_SHA
          docker push $ECR_REGISTRY/ai-gateway:latest
          docker push $ECR_REGISTRY/worker:$GITHUB_SHA
          docker push $ECR_REGISTRY/worker:latest

  # ========================================
  # Deploy to Staging
  # ========================================
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: build
    environment: staging
    steps:
      - uses: actions/checkout@v4
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}
      
      - name: Deploy to ECS Staging
        run: |
          aws ecs update-service \
            --cluster aicafe-staging \
            --service api-gateway \
            --force-new-deployment
          
          aws ecs update-service \
            --cluster aicafe-staging \
            --service ai-gateway \
            --force-new-deployment
          
          aws ecs update-service \
            --cluster aicafe-staging \
            --service worker \
            --force-new-deployment
      
      - name: Wait for deployment
        run: |
          aws ecs wait services-stable \
            --cluster aicafe-staging \
            --services api-gateway,ai-gateway,worker
      
      - name: Run integration tests
        run: |
          npm run test:integration -- --env=staging

  # ========================================
  # Deploy to Production
  # ========================================
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: deploy-staging
    environment: production
    steps:
      - uses: actions/checkout@v4
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}
      
      - name: Deploy to ECS Production
        run: |
          aws ecs update-service \
            --cluster aicafe-production \
            --service api-gateway \
            --force-new-deployment
          
          aws ecs update-service \
            --cluster aicafe-production \
            --service ai-gateway \
            --force-new-deployment
          
          aws ecs update-service \
            --cluster aicafe-production \
            --service worker \
            --force-new-deployment
      
      - name: Wait for deployment
        run: |
          aws ecs wait services-stable \
            --cluster aicafe-production \
            --services api-gateway,ai-gateway,worker
```

## 6. Terraform Infrastructure

### 6.1 Main Terraform Configuration

```hcl
# terraform/main.tf
terraform {
  required_version = ">= 1.5.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "aicafe-terraform-state"
    key    = "prod/terraform.tfstate"
    region = "us-east-1"
    
    # Enable state locking
    dynamodb_table = "aicafe-terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "AI-Cafe"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# ========================================
# VPC Module
# ========================================
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"
  
  name = "aicafe-${var.environment}"
  cidr = "10.0.0.0/16"
  
  # Availability zones
  azs = ["${var.aws_region}a", "${var.aws_region}b", "${var.aws_region}c"]
  
  # Public subnets
  public_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  
  # Private subnets
  private_subnets = [
    "10.0.11.0/24",
    "10.0.12.0/24",
    "10.0.13.0/24"
  ]
  
  # Data subnets
  database_subnets = [
    "10.0.21.0/24",
    "10.0.22.0/24",
    "10.0.23.0/24"
  ]
  
  # Enable NAT Gateway for private subnets
  enable_nat_gateway     = true
  single_nat_gateway     = false
  one_nat_gateway_per_az = true
  
  # Enable VPC endpoints
  enable_flow_log                      = true
  create_flow_log_cloudwatch_log_group = true
  flow_log_max_aggregation_interval    = 60
  
  tags = {
    Name = "aicafe-vpc"
  }
}

# ========================================
# ECR Repositories
# ========================================
resource "aws_ecr_repository" "api_gateway" {
  name         = "aicafe/api-gateway"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
  
  encryption_configuration {
    encryption_type = "AES256"
  }
}

resource "aws_ecr_repository" "ai_gateway" {
  name         = "aicafe/ai-gateway"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "worker" {
  name         = "aicafe/worker"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
}

# ========================================
# ECS Cluster
# ========================================
resource "aws_ecs_cluster" "main" {
  name = "aicafe-${var.environment}"
  
  setting {
    name  = "containerInsights"
    value = "enabled"
  }
  
  tags = {
    Name = "aicafe-ecs-cluster"
  }
}

# ECS Cluster capacity providers
resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name = aws_ecs_cluster.main.name
  
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]
  
  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = "FARGATE"
  }
}

# ========================================
# Secrets Manager
# ========================================
resource "aws_secretsmanager_secret" "database" {
  name        = "aicafe/database-${var.environment}"
  description = "Database credentials"
  
  recovery_window_in_days = 0  # Immediate deletion for dev
}

resource "aws_secretsmanager_secret_version" "database" {
  secret_id = aws_secretsmanager_secret.database.id
  secret_string = jsonencode({
    host     = aws_db_instance.postgres.address
    port     = aws_db_instance.postgres.port
    database = "aicafe"
    username = "postgres"
    password = random_password.db_password.result
  })
}

# ========================================
# CloudWatch
# ========================================
resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/aicafe-${var.environment}"
  retention_in_days = 7
  
  tags = {
    Name = "aicafe-ecs-logs"
  }
}

# ========================================
# S3 Buckets
# ========================================
resource "aws_s3_bucket" "assets" {
  bucket = "aicafe-assets-${var.environment}"
  
  versioning {
    enabled = true
  }
  
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }
}

resource "aws_s3_bucket" "backups" {
  bucket = "aicafe-backups-${var.environment}"
  
  versioning {
    enabled = true
  }
  
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }
  
  lifecycle_rule {
    enabled = true
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
    transition {
      days          = 90
      storage_class = "GLACIER"
    }
  }
}
```

### 6.2 Variables and Outputs

```hcl
# terraform/variables.tf
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

variable "domain_name" {
  description = "Domain name for the application"
  type        = string
  default     = "aicafe.example.com"
}

variable "enable_ddos_protection" {
  description = "Enable AWS Shield protection"
  type        = bool
  default     = true
}

# terraform/outputs.tf
output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "ecs_cluster_name" {
  description = "ECS Cluster name"
  value       = aws_ecs_cluster.main.name
}

output "ecs_cluster_arn" {
  description = "ECS Cluster ARN"
  value       = aws_ecs_cluster.main.arn
}

output "ecr_api_gateway" {
  description = "API Gateway ECR repository URL"
  value       = aws_ecr_repository.api_gateway.repository_url
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.postgres.address
}

output "redis_endpoint" {
  description = "Redis ElastiCache endpoint"
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
}

output "s3_assets_bucket" {
  description = "S3 assets bucket name"
  value       = aws_s3_bucket.assets.bucket
}
```

## 7. Monitoring & Observability

### 7.1 CloudWatch Alarms

```yaml
# terraform/monitoring.tf
# API Gateway Health
resource "aws_cloudwatch_metric_alarm" "api_gateway_cpu" {
  alarm_name          = "aicafe-api-gateway-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "API Gateway container CPU usage is high"
  
  dimensions = {
    ClusterName = aws_ecs_cluster.main.name
    ServiceName = aws_ecs_service.api_gateway.name
  }
  
  alarm_actions = [aws_sns_topic.alerts.arn]
  ok_actions    = [aws_sns_topic.alerts.arn]
}

# Database Connections
resource "aws_cloudwatch_metric_alarm" "db_connections" {
  alarm_name          = "aicafe-db-connections-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 400
  alarm_description   = "RDS connection count is high"
  
  dimensions = {
    DBInstanceIdentifier = aws_db_instance.postgres.identifier
  }
  
  alarm_actions = [aws_sns_topic.alerts.arn]
}

# AI Cost Alert
resource "aws_cloudwatch_metric_alarm" "ai_cost_daily" {
  alarm_name          = "aicafe-ai-cost-daily"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "AICost"
  namespace           = "AI-Cafe"
  period              = 86400
  statistic           = "Sum"
  threshold           = 100000  # 100,000 credits per day
  alarm_description   = "Daily AI cost exceeds threshold"
  
  alarm_actions = [aws_sns_topic.alerts.arn]
}

# Error Rate
resource "aws_cloudwatch_metric_alarm" "error_rate" {
  alarm_name          = "aicafe-error-rate-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "5XXErrorCount"
  namespace           = "AWS/ApplicationELB"
  period              = 300
  statistic           = "Sum"
  threshold           = 100
  alarm_description   = "ALB error rate is high"
  
  dimensions = {
    LoadBalancer = aws_lb.main.arn_suffix
  }
  
  alarm_actions = [aws_sns_topic.alerts.arn]
}

# Redis Memory
resource "aws_cloudwatch_metric_alarm" "redis_memory" {
  alarm_name          = "aicafe-redis-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "DatabaseMemoryUsagePercentage"
  namespace           = "AWS/ElastiCache"
  period              = 300
  statistic           = "Average"
  threshold           = 75
  alarm_description   = "Redis memory usage is high"
  
  dimensions = {
    ReplicationGroupId = aws_elasticache_replication_group.redis.id
  }
  
  alarm_actions = [aws_sns_topic.alerts.arn]
}
```

### 7.2 CloudWatch Dashboard

```json
{
  "widgets": [
    {
      "type": "metric",
      "x": 0,
      "y": 0,
      "width": 12,
      "height": 6,
      "properties": {
        "title": "API Gateway Overview",
        "metrics": [
          ["AWS/ApplicationELB", "RequestCount", {"stat": "Sum"}],
          [".", "TargetResponseTime", {"stat": "Average"}],
          [".", "HTTPCode_Target_5XX_Count", {"stat": "Sum"}],
          [".", "HTTPCode_Target_4XX_Count", {"stat": "Sum"}]
        ],
        "period": 300,
        "stat": "Average",
        "region": "us-east-1",
        "yAxis": {
          "left": {"min": 0},
          "right": {"min": 0}
        }
      }
    },
    {
      "type": "metric",
      "x": 12,
      "y": 0,
      "width": 12,
      "height": 6,
      "properties": {
        "title": "ECS Container Metrics",
        "metrics": [
          ["AWS/ECS", "CPUUtilization", {"stat": "Average"}],
          ["AWS/ECS", "MemoryUtilization", {"stat": "Average"}]
        ],
        "period": 300,
        "stat": "Average",
        "region": "us-east-1"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 6,
      "width": 8,
      "height": 6,
      "properties": {
        "title": "Database Metrics",
        "metrics": [
          ["AWS/RDS", "CPUUtilization", {"stat": "Average"}],
          [".", "DatabaseConnections", {"stat": "Average"}],
          [".", "FreeableMemory", {"stat": "Average"}]
        ],
        "period": 300,
        "stat": "Average",
        "region": "us-east-1"
      }
    },
    {
      "type": "metric",
      "x": 8,
      "y": 6,
      "width": 8,
      "height": 6,
      "properties": {
        "title": "AI Usage & Cost",
        "metrics": [
          ["AI-Cafe", "CreditsUsed", {"stat": "Sum"}],
          [".", "AICost", {"stat": "Sum"}],
          [".", "Requests", {"stat": "Sum"}]
        ],
        "period": 86400,
        "stat": "Sum",
        "region": "us-east-1"
      }
    },
    {
      "type": "metric",
      "x": 16,
      "y": 6,
      "width": 8,
      "height": 6,
      "properties": {
        "title": "Redis Cache",
        "metrics": [
          ["AWS/ElastiCache", "DatabaseMemoryUsagePercentage", {"stat": "Average"}],
          [".", "CacheHitRate", {"stat": "Average"}],
          [".", "CurrConnections", {"stat": "Maximum"}]
        ],
        "period": 300,
        "stat": "Average",
        "region": "us-east-1"
      }
    }
  ]
}
```

## 8. Backup & Disaster Recovery

### 8.1 Backup Strategy

| Data Type | Backup Frequency | Retention | Recovery Point Objective |
|-----------|-----------------|-----------|-------------------------|
| Database | Daily + Continuous | 30 days | < 1 hour |
| Redis Data | Daily | 7 days | < 24 hours |
| File Storage | Versioning | Indefinite | Instant |
| Application Logs | Daily | 90 days | < 1 hour |
| System State | Weekly | 4 weeks | < 4 hours |

### 8.2 RDS Backup Configuration

```yaml
# terraform/rds-backup.tf
resource "aws_backup_plan" "rds" {
  name = "aicafe-rds-backup"
  
  rule {
    name                = "daily-backup"
    target_vault_name    = aws_backup_vault.main.name
    schedule            = "cron(0 5 * * ? *)"  # 5 AM UTC
    
    start_window        = 60
    completion_window    = 180
    
    lifecycle {
      delete_after = 30
    }
    
    copy_action {
      destination_vault_arn = aws_backup_vault.cross_region.arn
      lifecycle {
        delete_after = 90
      }
    }
  }
  
  rule {
    name                = "monthly-backup"
    target_vault_name    = aws_backup_vault.main.name
    schedule            = "cron(0 6 1 * ? *)"  # First of month
    
    start_window        = 60
    completion_window    = 300
    
    lifecycle {
      delete_after = 365
    }
  }
}

resource "aws_backup_selection" "rds" {
  name         = "aicafe-rds-backup"
  backup_plan_id = aws_backup_plan.rds.id
  
  resources = [
    aws_db_instance.postgres.arn
  ]
  
  conditions {
    string_equals {
      key  = "aws:ResourceTag/Backup"
      value = "enabled"
    }
  }
}
```

## 9. Security Configuration

### 9.1 WAF Rules

```yaml
# terraform/waf.tf
resource "aws_wafv2_web_acl" "main" {
  name        = "aicafe-waf"
  description = "WAF rules for AI Café"
  scope       = "CLOUDFRONT"
  
  default_action {
    allow {}
  }
  
  rule {
    name     = "rate-limit-requests"
    priority = 1
    
    action {
      rate_limit {
        custom_key {
          header {
            name = "X-Forwarded-For"
          }
        }
        limit = 1000
        count_until_blocked = 2000
      }
    }
    
    statement {
      rate_based_statement {
        aggregate_key_type = "IP"
        scope_down_statement {
          not_statement {
            statement {
              byte_match_statement {
                field_to_match {
                  uri_path {}
                }
                positional_constraint = "STARTS_WITH"
                search_string         = "/health"
                text_transformation   = "NONE"
              }
            }
          }
        }
      }
    }
  }
  
  rule {
    name     = "block-sql-injection"
    priority = 2
    
    action {
      block {
        custom_response {
          response_code = 400
          custom_response_body_key = "sql-injection-response"
        }
      }
    }
    
    statement {
      sqli_match_statement {
        field_to_match {
          query_string {}
        }
        text_transformation {
          priority = 0
          type     = "URL_DECODE"
        }
        text_transformation {
          priority = 1
          type     = "LOWERCASE"
        }
      }
    }
  }
  
  rule {
    name     = "block-xss"
    priority = 3
    
    action {
      block {
        custom_response {
          response_code = 400
        }
      }
    }
    
    statement {
      xss_match_statement {
        field_to_match {
          body {}
        }
        text_transformation {
          priority = 0
          type     = "URL_DECODE"
        }
      }
    }
  }
  
  visibility_config {
    metric_name = "aicafe-waf-metrics"
    cloudwatch_metrics_enabled = true
    sampled_requests_enabled    = true
  }
}
```

### 9.2 Secrets Manager

```yaml
# terraform/secrets.tf
resource "aws_secretsmanager_secret" "api_keys" {
  name        = "aicafe/api-keys-${var.environment}"
  description = "External API keys"
  
  recovery_window_in_days = 0
  
  tags = {
    Name = "aicafe-api-keys"
  }
}

resource "aws_secretsmanager_secret_version" "api_keys" {
  secret_id = aws_secretsmanager_secret.api_keys.id
  
  secret_string = jsonencode({
    openai    = var.openai_api_key
    anthropic = var.anthropic_api_key
    google    = var.google_api_key
  })
}

# IAM policy for ECS tasks to access secrets
resource "aws_iam_policy" "secrets_access" {
  name        = "aicafe-secrets-access"
  description = "Allow ECS tasks to access Secrets Manager"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue"
        ]
        Resource = [
          aws_secretsmanager_secret.database.arn,
          aws_secretsmanager_secret.api_keys.arn,
          aws_secretsmanager_secret.redis.arn,
          aws_secretsmanager_secret.jwt.arn
        ]
      }
    ]
  })
}
```

## 10. Cost Optimization

### 10.1 Cost Allocation Tags

```yaml
# terraform/cost-allocation.tf
resource "aws_cost_allocation_tag" "project" {
  tag_key = "Project"
}

resource "aws_cost_allocation_tag" "environment" {
  tag_key = "Environment"
}

resource "aws_cost_allocation_tag" "service" {
  tag_key = "Service"
}

resource "aws_cost_allocation_tag" "team" {
  tag_key = "Team"
}
```

### 10.2 Cost Optimization Strategies

| Component | Strategy | Estimated Savings |
|-----------|----------|-------------------|
| **ECS** | Use Fargate Spot for workers | 30-50% |
| **RDS** | Use Reserved Instances (1yr) | 40-60% |
| **ElastiCache** | Use Reserved Nodes | 35-55% |
| **S3** | Enable intelligent tiering | 20-40% |
| **CloudWatch** | Reduce retention period | 15-25% |
| **AI Usage** | Route to cheaper models | 30-50% |

## 11. Deployment Checklist

### Pre-Deployment
- [ ] All tests passing
- [ ] Code review approved
- [ ] Migration scripts tested
- [ ] Backup completed
- [ ] Monitoring dashboard accessible
- [ ] Rollback plan ready

### Deployment
- [ ] Deploy to staging first
- [ ] Run integration tests
- [ ] Verify health checks
- [ ] Check CloudWatch logs
- [ ] Validate error rates
- [ ] Deploy to production

### Post-Deployment
- [ ] Monitor for 1 hour
- [ ] Check error rates
- [ ] Verify performance
- [ ] Update deployment record
- [ ] Notify stakeholders
- [ ] Schedule follow-up review
