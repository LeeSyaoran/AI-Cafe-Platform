# AI Café Platform - Migration Plan

## Từ Current State → Target Architecture

> **Duration:** 3-6 tháng  
> **Team Size:** 2-3 developers  
> **Phases:** 4

---

## Mục lục
1. [Current vs Target](#1-current-vs-target)
2. [Migration Phases Overview](#2-migration-phases-overview)
3. [Phase 1: MVP Stabilization](#3-phase-1-mvp-stabilization-weeks-1-2)
4. [Phase 2: Security Hardening](#4-phase-2-security-hardening-weeks-3-4)
5. [Phase 3: Service Extraction](#5-phase-3-service-extraction-weeks-5-12)
6. [Phase 4: AI Integration](#6-phase-4-ai-integration-weeks-13-20)
7. [Rollback Plan](#7-rollback-plan)
8. [Success Metrics](#8-success-metrics)

---

## 1. Current vs Target

### Current State (MVP)
```
┌─────────────────────────────────────────────────────────────────┐
│                     CURRENT: SINGLE MONOLITH                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Go API Gateway (:3000)                                         │
│  ├── Handler → Repository (No Service Layer)                   │
│  ├── In-memory OTP Storage                                      │
│  ├── SHA256 Password Hashing                                     │
│  └── PostgreSQL + Redis (not used for OTP)                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Target Architecture (Microservices)
```
┌─────────────────────────────────────────────────────────────────┐
│                     TARGET: MICROSERVICES                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Go API Gateway (:3000)  ──gRPC──▶  Auth Svc (:8081)         │
│                                        Credit Svc (:8082)        │
│                                        Session Svc (:8083)       │
│                                                                  │
│  Java Services                          Python AI (:7070)        │
│  ├── User Svc (:9090)                                         │
│  ├── Order Svc (:9091)                                        │
│  ├── Payment Svc (:9092)                                      │
│  └── Member Svc (:9093)                                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Migration Phases Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              MIGRATION TIMELINE                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  Week 1-2     Week 3-4     Week 5-8      Week 9-12     Week 13-16    Week 17-20  │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐          │
│  │ Phase 1│  │ Phase 2│  │ Phase 3│  │ Phase 3│  │ Phase 4│  │ Phase 4│          │
│  │Stabilize│→│Secure  │→│Extract │→│Extract │→│ AI     │→│ AI     │          │
│  │  MVP   │  │ Hardening│ │Credit │  │ User   │  │ Gateway│  │Gateway │          │
│  └────────┘  └────────┘  └────────┘  └────────┘  └────────┘  └────────┘          │
│                                                                                     │
│  Tasks:                Tasks:               Tasks:           Tasks:              │
│  - DTOs                - Redis OTP         - gRPC setup     - User Service      │
│  - Service Layer       - bcrypt             - Credit Svc     - Order Service     │
│  - Response Format     - Token Blacklist    - Proto files    - Payment Service   │
│  - Error Handling      - Rate Limiting      - API Gateway    - Member Service    │
│                        - Input Validation     update                              │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Phase 1: MVP Stabilization (Weeks 1-2)

### Mục tiêu
- Thiết lập nền tảng vững chắc cho development tiếp theo
- Cải thiện code quality và maintainability
- Không thay đổi functionality hiện tại

### Tasks

#### Week 1: Layered Architecture

| Task | Description | Files | Effort |
|------|-------------|-------|--------|
| T1.1 | Create DTO package | `internal/dto/*.go` | 1 day |
| T1.2 | Extract business logic to Service layer | `internal/service/*.go` | 3 days |
| T1.3 | Update Handlers to delegate to Services | `internal/handler/*.go` | 2 days |
| T1.4 | Standardize response format | `pkg/response/` | 1 day |
| T1.5 | Add error types and handling | `internal/errors/` | 1 day |
| T1.6 | Update main.go wiring | `cmd/server/main.go` | 1 day |

#### Week 2: Code Quality

| Task | Description | Files | Effort |
|------|-------------|-------|--------|
| T1.7 | Add input validation | `pkg/validator/` | 1 day |
| T1.8 | Create repository interfaces | `internal/repository/` | 1 day |
| T1.9 | Add basic unit tests | `*_test.go` | 2 days |
| T1.10 | Add logging middleware | `internal/middleware/logging.go` | 1 day |
| T1.11 | Update README and docs | `docs/` | 1 day |

### Deliverables
```
Phase 1/
├── internal/
│   ├── dto/
│   │   ├── auth.go
│   │   ├── product.go
│   │   ├── order.go
│   │   └── cart.go
│   ├── service/
│   │   ├── auth_service.go
│   │   ├── product_service.go
│   │   ├── order_service.go
│   │   └── cart_service.go
│   └── errors/
│       └── errors.go
├── pkg/
│   └── validator/
│       └── validator.go
└── go.sum (updated)
```

### Verification
```bash
# Run tests
go test ./... -v

# Check coverage
go test ./... -coverprofile=coverage.out
go tool cover -html=coverage.out

# Run linter
golangci-lint run
```

---

## 4. Phase 2: Security Hardening (Weeks 3-4)

### Mục tiêu
- Sửa tất cả security vulnerabilities
- Chuẩn bị hạ tầng cho production
- Compliance với security best practices

### Tasks

#### Week 3: Core Security

| Task | Description | Priority | Files |
|------|-------------|----------|-------|
| T2.1 | Move OTP to Redis | P0 | `internal/repository/redis.go` |
| T2.2 | Change SHA256 to bcrypt | P0 | `internal/service/auth_service.go` |
| T2.3 | Add token blacklist in Redis | P0 | `internal/repository/redis.go` |
| T2.4 | Remove `_debug_otp` from response | P0 | `internal/handler/auth_handler.go` |
| T2.5 | Fix OTP randomness (crypto/rand) | P1 | `internal/service/auth_service.go` |

#### Week 4: Production Ready

| Task | Description | Priority | Files |
|------|-------------|----------|-------|
| T2.6 | Implement tier-based rate limiting | P1 | `internal/middleware/rate_limiter.go` |
| T2.7 | Add input validation middleware | P1 | `internal/middleware/validation.go` |
| T2.8 | Add audit logging | P1 | `internal/middleware/audit.go` |
| T2.9 | Setup environment configuration | P2 | `internal/config/` |
| T2.10 | Add health check endpoints | P2 | `internal/handler/common_handler.go` |

### Security Checklist

```markdown
✅ DONE:
- [x] Password hashing (bcrypt)
- [x] JWT with expiration
- [x] Token blacklisting (Redis)
- [x] OTP rate limiting
- [x] Input validation
- [x] CORS configuration

🔄 IN PROGRESS:
- [ ] Redis OTP storage

❌ TODO:
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention
- [ ] CSRF protection
- [ ] API key for external services
- [ ] Audit logging
```

### Deliverables
```yaml
# Docker Compose with Security
services:
  api-gateway:
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - REDIS_URL=redis://redis:6379
    secrets:
      - jwt_secret
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Verification
```bash
# Security scan
gosec ./...

# Dependency check
nancy孤独 ./go.sum

# Docker security scan
trivy image aicafe-api-gateway:latest
```

---

## 5. Phase 3: Service Extraction (Weeks 5-12)

### Mục tiêu
- Extract Credit Service thành standalone microservice
- Thiết lập gRPC communication
- Maintain backward compatibility

### Service Extraction Order

```
┌─────────────────────────────────────────────────────────────────┐
│                   SERVICE EXTRACTION ORDER                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Credit Service (Week 5-6)                                  │
│     ├── Proto definition                                        │
│     ├── Go service implementation                                │
│     ├── gRPC client in API Gateway                              │
│     └── Database migration                                       │
│                                                                  │
│  2. Auth Service (Week 7-8)                                   │
│     ├── Token generation/validation                             │
│     ├── gRPC integration                                        │
│     └── Session management                                       │
│                                                                  │
│  3. User Service (Week 9-10) - Java                            │
│     ├── Spring Boot setup                                       │
│     ├── User CRUD                                               │
│     └── Profile management                                       │
│                                                                  │
│  4. Order Service (Week 11-12) - Java                         │
│     ├── Order processing                                        │
│     ├── Inventory management                                    │
│     └── Status tracking                                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Week 5-6: Credit Service (Go)

#### Proto Definition
```protobuf
// proto/credit.proto
syntax = "proto3";

package aicafe.credit;

service CreditService {
    rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
    rpc Deduct(DeductRequest) returns (DeductResponse);
    rpc AddCredit(AddCreditRequest) returns (AddCreditResponse);
    rpc CheckBalance(CheckBalanceRequest) returns (CheckBalanceResponse);
    rpc GetTransactions(GetTransactionsRequest) returns (GetTransactionsResponse);
}
```

#### Service Implementation
```go
// services/credit-service/cmd/server/main.go
package main

import (
    "log"
    "net"
    
    "github.com/aicafe/credit-service/internal/config"
    "github.com/aicafe/credit-service/internal/repository"
    "github.com/aicafe/credit-service/internal/service"
    creditpb "github.com/aicafe/credit-service/pkg/grpc"
    
    "google.golang.org/grpc"
)

func main() {
    cfg := config.Load()
    
    repo := repository.NewPostgresRepo(cfg.Database.DSN())
    svc := service.NewCreditService(repo)
    
    lis, _ := net.Listen("tcp", ":8082")
    srv := grpc.NewServer()
    creditpb.RegisterCreditServiceServer(srv, &server{service: svc})
    
    log.Printf("Credit service listening on :8082")
    srv.Serve(lis)
}
```

#### Database Migration
```sql
-- migrations/001_create_credit_service.sql

-- Create credit service specific tables
CREATE TABLE IF NOT EXISTS credit_balances (
    user_id UUID PRIMARY KEY,
    balance DECIMAL(12,2) DEFAULT 0,
    pending_balance DECIMAL(12,2) DEFAULT 0,
    lifetime_spent DECIMAL(12,2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    amount DECIMAL(12,2) NOT NULL,
    balance_after DECIMAL(12,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add foreign key to existing user_credits table
ALTER TABLE user_credits 
    ADD CONSTRAINT fk_user_credits_user 
    FOREIGN KEY (user_id) REFERENCES users(id);
```

### Week 7-8: Auth Service (Go)

#### Proto Definition
```protobuf
// proto/auth.proto
syntax = "proto3";

package aicafe.auth;

service AuthService {
    rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);
    rpc GenerateToken(GenerateTokenRequest) returns (GenerateTokenResponse);
    rpc RefreshToken(RefreshTokenRequest) returns (RefreshTokenResponse);
    rpc RevokeToken(RevokeTokenRequest) returns (RevokeTokenResponse);
    rpc VerifyOTP(VerifyOTPRequest) returns (VerifyOTPResponse);
    rpc SendOTP(SendOTPRequest) returns (SendOTPResponse);
}
```

### Week 9-10: User Service (Java)

#### Project Structure
```
services/user-service/
├── pom.xml
├── src/main/
│   ├── java/com/aicafe/userservice/
│   │   ├── UserServiceApplication.java
│   │   ├── config/
│   │   │   ├── AppConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   └── UserController.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── UserServiceImpl.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── UserProfileRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── UserProfile.java
│   │   ├── dto/
│   │   │   ├── CreateUserRequest.java
│   │   │   └── UserResponse.java
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   └── proto/
│       └── user.proto
└── src/main/resources/
    └── application.yml
```

#### Spring Boot Setup
```xml
<!-- pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.aicafe</groupId>
    <artifactId>user-service</artifactId>
    <version>1.0.0</version>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        
        <!-- gRPC -->
        <dependency>
            <groupId>net.devh</groupId>
            <artifactId>grpc-spring-boot-starter</artifactId>
            <version>2.15.0.RELEASE</version>
        </dependency>
        
        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Week 11-12: Order Service (Java)

```java
// Order Service - Spring Boot Implementation
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CreditServiceGrpcClient creditClient;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. Validate cart
        Cart cart = cartRepository.findByUserIdAndCafeId(
            request.getUserId(), 
            request.getCafeId()
        ).orElseThrow(() -> new ApiException("CART_NOT_FOUND"));
        
        // 2. Check credit balance via gRPC
        var balanceCheck = creditClient.checkBalance(
            request.getUserId(), 
            request.getTotalAmount()
        );
        
        if (!balanceCheck.getSufficient()) {
            throw new ApiException("INSUFFICIENT_CREDIT");
        }
        
        // 3. Create order
        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .userId(request.getUserId())
            .cafeId(request.getCafeId())
            .status(OrderStatus.PENDING)
            .totalAmount(request.getTotalAmount())
            .build();
        
        order = orderRepository.save(order);
        
        // 4. Deduct credit via gRPC
        creditClient.deduct(
            request.getUserId(), 
            request.getTotalAmount(),
            order.getId().toString()
        );
        
        // 5. Clear cart
        cartRepository.delete(cart);
        
        return order;
    }
}
```

### Migration Checklist

```markdown
# Week 5-6: Credit Service
- [ ] Define proto files
- [ ] Implement Credit Service
- [ ] Create database migrations
- [ ] Add gRPC client to API Gateway
- [ ] Update handlers to use gRPC
- [ ] Test end-to-end
- [ ] Deploy Credit Service
- [ ] Monitor and rollback if needed

# Week 7-8: Auth Service
- [ ] Define proto files
- [ ] Implement Auth Service
- [ ] Migrate token logic
- [ ] Update API Gateway
- [ ] Test authentication flow

# Week 9-10: User Service
- [ ] Setup Java project
- [ ] Implement User CRUD
- [ ] Setup gRPC server
- [ ] Add database schema
- [ ] Deploy and test

# Week 11-12: Order Service
- [ ] Setup Java project
- [ ] Implement Order logic
- [ ] Integrate with Credit Service
- [ ] Deploy and test
```

---

## 6. Phase 4: AI Integration (Weeks 13-20)

### Mục tiêu
- Hoàn thiện Python AI Gateway
- Tích hợp AI services vào platform
- Implement real-time features

### Week 13-14: Chat Enhancement

```python
# app/services/claude_service.py
class ClaudeService:
    def __init__(self, api_key: str):
        self.client = Anthropic(api_key=api_key)
    
    async def chat(
        self,
        messages: list[ChatMessage],
        model: str = "claude-3-opus-20240229",
        max_tokens: int = 1024,
        user_id: str = None,
        cafe_id: str = None,
    ) -> ChatResponse:
        # Calculate cost
        cost = self.calculate_cost(model, max_tokens)
        
        # Check credit balance
        if not await self.check_credit(user_id, cost):
            raise InsufficientCreditException()
        
        # Call API
        response = await self.client.messages.create(
            model=model,
            max_tokens=max_tokens,
            messages=[m.to_dict() for m in messages]
        )
        
        # Deduct credit
        await self.deduct_credit(user_id, cost)
        
        # Log transaction
        await self.log_transaction(user_id, cost, response.usage)
        
        return ChatResponse(
            content=response.content[0].text,
            model=model,
            tokens_used=response.usage.total_tokens,
            cost=cost
        )
```

### Week 15-16: Image Generation

```python
# app/services/image_service.py
class ImageService:
    async def generate(
        self,
        prompt: str,
        model: str = "dall-e-3",
        size: str = "1024x1024",
        user_id: str = None,
    ) -> ImageResponse:
        # Calculate cost based on model and size
        cost = self.calculate_image_cost(model, size)
        
        # Check credit
        if not await self.check_credit(user_id, cost):
            raise InsufficientCreditException()
        
        # Generate image
        response = await self.openai.images.generate(
            model=model,
            prompt=prompt,
            size=size,
            n=1
        )
        
        # Deduct and log
        await self.deduct_credit(user_id, cost)
        
        return ImageResponse(
            url=response.data[0].url,
            revised_prompt=response.data[0].revised_prompt,
            cost=cost
        )
```

### Week 17-18: Real-time Features

```python
# app/routers/websocket.py
from fastapi import WebSocket, WebSocketDisconnect

@router.websocket("/v1/chat/ws")
async def chat_websocket(websocket: WebSocket, token: str):
    # Authenticate
    user = await authenticate(websocket, token)
    
    # Connect to session
    session = await get_or_create_session(user.id)
    
    await websocket.accept()
    
    try:
        while True:
            # Receive message
            data = await websocket.receive_json()
            
            # Process with AI
            response = await claude_service.chat(
                messages=session.messages + [data],
                user_id=user.id
            )
            
            # Send response
            await websocket.send_json({
                "type": "message",
                "content": response.content,
                "tokens": response.tokens_used
            })
            
            # Update session
            await session.add_message(data)
            await session.add_response(response)
            
    except WebSocketDisconnect:
        await session.close()
```

### Week 19-20: Testing & Documentation

```markdown
# AI Gateway Testing
- [ ] Unit tests for all services
- [ ] Integration tests with API Gateway
- [ ] Load testing (k6)
- [ ] Error handling tests
- [ ] Credit deduction tests

# Documentation
- [ ] API documentation (OpenAPI/Swagger)
- [ ] Integration guide
- [ ] Example code
- [ ] Rate limits documentation
- [ ] Error codes reference
```

---

## 7. Rollback Plan

### Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                      ROLLBACK STRATEGY                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    BLUE-GREEN DEPLOYMENT                  │   │
│  │                                                          │   │
│  │   Blue (Current)  ←── Switch ──→  Green (New)           │   │
│  │                                                          │   │
│  │   If Green fails → Switch back to Blue                   │   │
│  │   If Green succeeds → Promote Blue to Green              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Rollback Commands

```bash
# Service rollback
docker-compose -f docker-compose.staging.yml up -d --scale api-gateway=0
docker-compose -f docker-compose.staging.yml up -d api-gateway:blue

# Database rollback
psql -h localhost -U aicafe -d aicafe -f rollback/migrations/001.sql

# gRPC rollback
# 1. Revert API Gateway to use direct DB calls
# 2. Deploy previous version
# 3. Stop extracted service
```

### Rollback Checklist

```markdown
# Immediate Rollback (< 5 minutes)
- [ ] Switch load balancer to previous version
- [ ] Verify health endpoints
- [ ] Check error rates
- [ ] Notify team

# Database Rollback
- [ ] Check data integrity
- [ ] Execute migration rollback
- [ ] Verify FK constraints
- [ ] Test critical flows

# Post-Rollback
- [ ] Document incident
- [ ] Identify root cause
- [ ] Create fix
- [ ] Schedule retry
```

---

## 8. Success Metrics

### Phase 1: MVP Stabilization

| Metric | Target | Current |
|--------|--------|---------|
| Code coverage | > 60% | - |
| Linter errors | 0 | - |
| Test pass rate | 100% | - |
| API response time (p95) | < 200ms | - |

### Phase 2: Security Hardening

| Metric | Target | Current |
|--------|--------|---------|
| Security vulnerabilities | 0 critical | - |
| OTP storage | Redis | In-memory |
| Password hashing | bcrypt | SHA256 |
| Token blacklisting | ✅ | ❌ |

### Phase 3: Service Extraction

| Metric | Target | Current |
|--------|--------|---------|
| Services extracted | 4 | 0 |
| gRPC calls | < 50ms | - |
| Service availability | 99.9% | - |
| Service isolation | ✅ | ❌ |

### Phase 4: AI Integration

| Metric | Target | Current |
|--------|--------|---------|
| AI response time | < 5s | - |
| Credit accuracy | 100% | - |
| AI models supported | 5 | 1 |
| Real-time latency | < 500ms | - |

---

## Appendix

### A. Dependencies

```bash
# Go
go get github.com/golang-jwt/jwt/v5
go get github.com/redis/go-redis/v9
go get github.com/go-playground/validator/v10
go get google.golang.org/grpc
go get google.golang.org/protobuf

# Java
# (See pom.xml in each service)

# Python
pip install anthropic openai redis fastapi uvicorn
```

### B. Docker Images

```dockerfile
# API Gateway
FROM golang:1.21-alpine
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o server ./cmd/server
EXPOSE 3000
CMD ["./server"]

# Credit Service
FROM golang:1.21-alpine
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o server ./cmd/server
EXPOSE 8082
CMD ["./server"]
```

### C. Monitoring Setup

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'api-gateway'
    static_configs:
      - targets: ['api-gateway:3000']
        labels:
          service: 'api-gateway'
  
  - job_name: 'credit-service'
    static_configs:
      - targets: ['credit-service:8082']
        labels:
          service: 'credit-service'
```

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Next Review:** Weekly during migration
