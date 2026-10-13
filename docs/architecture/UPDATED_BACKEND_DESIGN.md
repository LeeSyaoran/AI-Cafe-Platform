# AI Café Platform - Updated Backend Design

> **Version:** 2.0  
> **Date:** 2024  
> **Status:** ACTIVE DEVELOPMENT

---

## Mục lục
1. [Tổng quan Kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Development Phases](#2-development-phases)
3. [Go API Gateway (Current)](#3-go-api-gateway-current)
4. [Java Microservices (Planned)](#4-java-microservices-planned)
5. [Python AI Gateway (In Progress)](#5-python-ai-gateway-in-progress)
6. [Service Communication](#6-service-communication)
7. [Security Architecture](#7-security-architecture)
8. [Database Schema](#8-database-schema)

---

## 1. Tổng quan Kiến trúc

### 1.1 Current Architecture State

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     AI CAFÉ PLATFORM - CURRENT STATE                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  FRONTEND (React/Next.js)                                                  │
│  └── http://localhost:3001                                                  │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    BACKEND - SINGLE MONOLITH                         │   │
│  │                                                                     │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │              Go API Gateway (:3000)                          │   │   │
│  │  │                                                              │   │   │
│  │  │  ├── Auth (OTP, JWT)           ✅ IMPLEMENTED                │   │   │
│  │  │  ├── Products                 ✅ IMPLEMENTED                │   │   │
│  │  │  ├── Cart                     ⚠️ PARTIAL                   │   │   │
│  │  │  ├── Orders                   ⚠️ PARTIAL                   │   │   │
│  │  │  └── Credits                  ❌ TODO                      │   │   │
│  │  │                                                              │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │                                                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  DATA STORAGE                                                              │
│  ├── PostgreSQL (:5432)    ✅ IMPLEMENTED                                 │
│  └── Redis (:6379)          ⚠️ PARTIAL (not used for OTP)                │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  EXTERNAL SERVICES                                                         │
│  ├── AI Services           🔄 Python AI Gateway (in progress)             │
│  └── SMS/Notification      ❌ TODO                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Target Architecture (Phase 3)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     AI CAFÉ PLATFORM - TARGET ARCHITECTURE                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  FRONTEND (React/Next.js)                                                  │
│  └── http://localhost:3001                                                  │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     Go API Gateway (:3000)                           │   │
│  │                                                                     │   │
│  │  Routes traffic, rate limiting, auth, request/response transform    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ gRPC                                   │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         MICROSERVICES                                 │   │
│  │                                                                     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │ Auth Svc     │  │ Credit Svc   │  │ Session Svc  │              │   │
│  │  │ (Go :8081)   │  │ (Go :8082)   │  │ (Go :8083)   │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  │                                                                     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │   │
│  │  │ User Svc     │  │ Payment Svc │  │ Member Svc   │  │Order Svc│ │   │
│  │  │ (Java :9090) │  │ (Java :9091) │  │ (Java :9092) │  │(Java:9093)│ │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  └──────────┘ │   │
│  │                                                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   Python AI Gateway (:7070)                          │   │
│  │                                                                     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │ Chat API     │  │ Image API    │  │ Video API    │              │   │
│  │  │ (Claude)     │  │ (DALL-E)    │  │ (Sora)      │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  DATA STORAGE                                                              │
│  ├── PostgreSQL (:5432)    - Main database                                │
│  ├── Redis (:6379)         - Sessions, cache, OTP                        │
│  └── S3/MinIO              - File storage                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Development Phases

### Phase 1: MVP (Current) ✅ IN PROGRESS
**Mục tiêu:** Single API Gateway, basic functionality

| Component | Status | Priority |
|-----------|--------|----------|
| Go API Gateway | 🔄 In Progress | P0 |
| PostgreSQL Schema | ✅ Done | P0 |
| Redis Setup | ⚠️ Partial | P1 |
| Auth (OTP + JWT) | 🔄 In Progress | P0 |
| Products API | ✅ Done | P0 |
| Cart API | ⚠️ Partial | P1 |
| Orders API | ⚠️ Partial | P1 |

### Phase 2: Production Ready (Week 2-4)
**Mục tiêu:** Security, reliability, monitoring

| Component | Status | Priority |
|-----------|--------|----------|
| Redis OTP | ❌ TODO | P0 |
| Rate Limiting | ⚠️ Basic | P1 |
| bcrypt Password | ❌ TODO | P0 |
| Credit Service | ❌ TODO | P1 |
| Payment Integration | ❌ TODO | P2 |
| Monitoring/Logging | ❌ TODO | P1 |
| API Documentation | ❌ TODO | P2 |

### Phase 3: Microservices (Month 2-3)
**Mục tiêu:** Extract services, gRPC communication

| Service | Language | Port | Status |
|---------|----------|------|--------|
| Auth Service | Go | 8081 | ❌ TODO |
| Credit Service | Go | 8082 | ❌ TODO |
| Session Service | Go | 8083 | ❌ TODO |
| User Service | Java | 9090 | ❌ TODO |
| Order Service | Java | 9091 | ❌ TODO |
| Payment Service | Java | 9092 | ❌ TODO |
| Member Service | Java | 9093 | ❌ TODO |
| AI Gateway | Python | 7070 | 🔄 In Progress |

---

## 3. Go API Gateway (Current)

### 3.1 Directory Structure

```
BackEnd/go/api-gateway/
├── cmd/
│   └── server/
│       └── main.go              # Application entry point
├── internal/
│   ├── config/
│   │   ├── config.go           # Configuration management
│   │   └── jwt.go             # JWT configuration
│   ├── dto/                    # Data Transfer Objects
│   │   ├── auth.go
│   │   ├── product.go
│   │   ├── order.go
│   │   └── cart.go
│   ├── handler/
│   │   ├── auth_handler.go     # Auth endpoints
│   │   ├── product_handler.go  # Product endpoints
│   │   ├── order_handler.go    # Order endpoints
│   │   ├── cart_handler.go     # Cart endpoints
│   │   ├── credit_handler.go   # Credit endpoints
│   │   ├── common_handler.go   # Health, version
│   │   └── handlers.go         # Shared handlers
│   ├── middleware/
│   │   ├── auth.go            # JWT authentication
│   │   ├── rate_limiter.go    # Rate limiting
│   │   └── tracing.go         # OpenTelemetry (TODO)
│   ├── repository/
│   │   ├── postgres.go        # PostgreSQL access
│   │   └── redis.go           # Redis access (TODO)
│   ├── service/
│   │   ├── auth_service.go    # Auth business logic
│   │   ├── product_service.go # Product business logic
│   │   ├── order_service.go   # Order business logic
│   │   ├── cart_service.go    # Cart business logic
│   │   └── credit_service.go  # Credit business logic
│   └── model/
│       └── models.go          # Domain models
├── pkg/
│   ├── response/
│   │   └── response.go       # Unified response format
│   └── validator/
│       └── validator.go      # Input validation
├── proto/                     # Protocol Buffers (TODO)
│   ├── auth.proto
│   ├── credit.proto
│   └── order.proto
├── migrations/                # Database migrations
│   └── 001_initial.sql
├── go.mod
├── go.sum
└── Dockerfile
```

### 3.2 API Endpoints

#### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/v1/auth/send-otp` | Send OTP to phone | ❌ |
| POST | `/v1/auth/verify-otp` | Verify OTP, get tokens | ❌ |
| POST | `/v1/auth/register` | Register new user | ❌ |
| POST | `/v1/auth/login` | Login with email/password | ❌ |
| POST | `/v1/auth/refresh` | Refresh access token | ❌ |
| POST | `/v1/auth/logout` | Logout, blacklist token | ✅ |
| GET | `/v1/me` | Get current user profile | ✅ |
| PUT | `/v1/me` | Update user profile | ✅ |

#### Products
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/v1/products` | List products | ❌ |
| GET | `/v1/products/:id` | Get product details | ❌ |
| GET | `/v1/categories` | List categories | ❌ |

#### Cart
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/v1/cart` | Get user cart | ✅ |
| POST | `/v1/cart/items` | Add item to cart | ✅ |
| PUT | `/v1/cart/items/:id` | Update cart item | ✅ |
| DELETE | `/v1/cart/items/:id` | Remove cart item | ✅ |
| DELETE | `/v1/cart` | Clear cart | ✅ |
| POST | `/v1/cart/checkout` | Checkout cart | ✅ |

#### Orders
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/v1/orders` | List user orders | ✅ |
| POST | `/v1/orders` | Create new order | ✅ |
| GET | `/v1/orders/:id` | Get order details | ✅ |
| POST | `/v1/orders/:id/cancel` | Cancel order | ✅ |

#### Credits
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/v1/credits/balance` | Get credit balance | ✅ |
| GET | `/v1/credits/transactions` | List transactions | ✅ |

### 3.3 Response Format

```json
// Success Response
{
    "success": true,
    "data": { ... },
    "meta": {
        "page": 1,
        "limit": 20,
        "total": 100,
        "totalPages": 5
    }
}

// Error Response
{
    "success": false,
    "error": {
        "code": "OTP_INVALID",
        "message": "Invalid OTP code",
        "details": { ... },
        "traceId": "abc123"
    }
}
```

### 3.4 Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_REQUEST` | 400 | Malformed request body |
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `UNAUTHORIZED` | 401 | Missing or invalid token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Resource already exists |
| `TOO_MANY_REQUESTS` | 429 | Rate limit exceeded |
| `INTERNAL_ERROR` | 500 | Server error |

---

## 4. Java Microservices (Planned)

### 4.1 Service Overview

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| user-service | 9090 | users_db | User management, profiles |
| order-service | 9091 | orders_db | Order processing |
| payment-service | 9092 | payments_db | Payment integration |
| member-service | 9093 | members_db | Subscriptions, loyalty |

### 4.2 Tech Stack

- **Framework:** Spring Boot 3.2+
- **Language:** Java 21
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Build:** Maven
- **Container:** Docker

### 4.3 Common Dependencies

```xml
<!-- All Java services should include -->
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
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Observability -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

### 4.4 Service Structure

```
services/{service-name}/
├── src/main/java/com/aicafe/{service}/
│   ├── Aicafe{Service}Application.java
│   ├── config/
│   │   └── AppConfig.java
│   ├── controller/
│   │   └── {Service}Controller.java
│   ├── service/
│   │   ├── {Service}Service.java
│   │   └── {Service}ServiceImpl.java
│   ├── repository/
│   │   └── {Entity}Repository.java
│   ├── entity/
│   │   └── {Entity}.java
│   ├── dto/
│   │   ├── Request.java
│   │   └── Response.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── {Exception}.java
├── src/main/proto/
│   └── {service}.proto
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       └── V1__initial.sql
├── pom.xml
└── Dockerfile
```

---

## 5. Python AI Gateway (In Progress)

### 5.1 Current Status

```python
# BackEnd/python/ai-gateway/
# ✅ IMPLEMENTED:
- FastAPI application setup
- Chat endpoint (/v1/chat/completions)
- Model listing (/v1/models)
- Credit deduction
- Cost tracking

# ❌ TODO:
- Image generation endpoint
- Video generation endpoint
- WebSocket support
- Better error handling
```

### 5.2 Tech Stack

- **Framework:** FastAPI
- **Language:** Python 3.11+
- **AI Providers:** OpenAI, Anthropic
- **Database:** PostgreSQL (for credits/transactions)
- **Cache:** Redis

### 5.3 API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/v1/chat/completions` | Chat with AI | ✅ Done |
| POST | `/v1/images/generations` | Generate images | ❌ TODO |
| POST | `/v1/videos/generations` | Generate videos | ❌ TODO |
| GET | `/v1/models` | List available models | ✅ Done |
| GET | `/v1/credits/balance` | Check credit balance | ✅ Done |

---

## 6. Service Communication

### 6.1 gRPC vs REST

| Scenario | Protocol | Reason |
|----------|----------|--------|
| API Gateway → Services | gRPC | High performance, type safety |
| External APIs | REST | Compatibility, simplicity |
| Real-time updates | WebSocket | Bidirectional communication |
| Event streaming | Kafka | Event-driven architecture |

### 6.2 gRPC Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                     gRPC Communication Flow                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐  │
│  │ API Gateway │       │  Credit    │       │   Credit   │  │
│  │  (Client)   │──────▶│  Service   │──────▶│  Database  │  │
│  │             │  gRPC │  (Server)  │  JDBC │             │  │
│  │             │◀──────│            │◀──────│             │  │
│  │             │ resp  │            │ resp  │             │  │
│  └─────────────┘       └─────────────┘       └─────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3 Proto Definition Example

```protobuf
// proto/credit/credit.proto
syntax = "proto3";

package aicafe.credit;

option go_package = "github.com/aicafe-api-gateway/pkg/grpc/credit";
option java_package = "com.aicafe.credit.grpc";

service CreditService {
    rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
    rpc Deduct(DeductRequest) returns (DeductResponse);
    rpc AddCredit(AddCreditRequest) returns (AddCreditResponse);
    rpc CheckBalance(CheckBalanceRequest) returns (CheckBalanceResponse);
}

message GetBalanceRequest {
    string user_id = 1;
}

message GetBalanceResponse {
    string user_id = 1;
    double balance = 2;
}

message DeductRequest {
    string user_id = 1;
    double amount = 2;
    string order_id = 3;
    string reason = 4;
}

message DeductResponse {
    bool success = 1;
    double new_balance = 2;
    string error_code = 3;
}
```

---

## 7. Security Architecture

### 7.1 Authentication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Authentication Flow                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. User sends phone number                                     │
│     POST /v1/auth/send-otp                                       │
│     { "phone": "+84..." }                                        │
│                    │                                             │
│                    ▼                                             │
│  2. Server generates OTP, stores in Redis                        │
│     Key: "otp:+84..." → Value: "123456"                          │
│     TTL: 5 minutes                                                │
│                    │                                             │
│                    ▼                                             │
│  3. SMS Gateway sends OTP to user                                │
│     (In MVP: Print to console)                                    │
│                                                                  │
│  4. User submits OTP                                              │
│     POST /v1/auth/verify-otp                                      │
│     { "phone": "+84...", "code": "123456" }                     │
│                    │                                             │
│                    ▼                                             │
│  5. Server validates OTP from Redis                              │
│     - Check existence                                             │
│     - Check expiration                                           │
│     - Check attempts (< 3)                                        │
│                    │                                             │
│                    ▼                                             │
│  6. Generate JWT tokens                                          │
│     - Access token (1 hour)                                       │
│     - Refresh token (7 days)                                     │
│     - Store JTI in Redis for blacklist                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 Token Structure

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-uuid",
    "user_id": "user-uuid",
    "email": "user@example.com",
    "phone": "+84...",
    "role": "customer",
    "tier": "free",
    "jti": "unique-token-id",
    "iat": 1704067200,
    "exp": 1704070800,
    "iss": "aicafe-api"
  }
}
```

### 7.3 Rate Limiting

| Tier | Requests/minute | Burst | Description |
|------|-----------------|-------|-------------|
| Free | 60 | 10 | Default for new users |
| Basic | 180 | 30 | After first purchase |
| Premium | 300 | 60 | Paid subscribers |
| Enterprise | 720 | 120 | B2B customers |

### 7.4 Security Checklist

- [x] Password hashing (bcrypt)
- [x] JWT with expiration
- [x] Token blacklisting (Redis)
- [x] OTP rate limiting
- [x] Input validation
- [x] CORS configuration
- [x] Rate limiting
- [ ] SQL injection prevention (use parameterized queries)
- [ ] XSS prevention
- [ ] CSRF protection
- [ ] API key for external services
- [ ] Audit logging

---

## 8. Database Schema

### 8.1 Main Tables

```sql
-- Companies (Multi-tenant)
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    tax_id VARCHAR(50),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    name VARCHAR(255),
    role VARCHAR(20) DEFAULT 'customer',
    status VARCHAR(20) DEFAULT 'active',
    tier VARCHAR(20) DEFAULT 'free',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Credits
CREATE TABLE user_credits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) UNIQUE,
    balance DECIMAL(12,2) DEFAULT 0,
    pending_balance DECIMAL(12,2) DEFAULT 0,
    lifetime_spent DECIMAL(12,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Credit Transactions
CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    amount DECIMAL(12,2) NOT NULL,
    balance_after DECIMAL(12,2) NOT NULL,
    type VARCHAR(50) NOT NULL, -- deduct, topup, refund, promotion
    reference_id VARCHAR(255), -- order_id, payment_id
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cafes
CREATE TABLE cafes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id),
    region_id UUID,
    cafe_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    description TEXT,
    address TEXT,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    is_delivery_enabled BOOLEAN DEFAULT FALSE,
    is_pickup_enabled BOOLEAN DEFAULT TRUE,
    tax_rate DECIMAL(5,4) DEFAULT 0.1,
    opening_time TIME,
    closing_time TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255),
    icon VARCHAR(100),
    color VARCHAR(20),
    sort_order INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID REFERENCES companies(id),
    category_id UUID REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    slug VARCHAR(255),
    sku VARCHAR(100),
    description TEXT,
    short_desc VARCHAR(500),
    price DECIMAL(12,2) NOT NULL,
    images JSONB DEFAULT '[]',
    calories INT,
    preparation_time INT, -- minutes
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_best_seller BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID REFERENCES users(id),
    company_id UUID REFERENCES companies(id),
    cafe_id UUID REFERENCES cafes(id),
    seat_id UUID,
    order_type VARCHAR(20) NOT NULL, -- pickup, delivery, dine_in
    status VARCHAR(20) DEFAULT 'pending',
    subtotal DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) DEFAULT 0,
    tax_amount DECIMAL(12,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    total_paid DECIMAL(12,2) DEFAULT 0,
    payment_method VARCHAR(20),
    payment_status VARCHAR(20) DEFAULT 'pending',
    payment_id VARCHAR(255),
    customer_note TEXT,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order Items
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id),
    product_id UUID REFERENCES products(id),
    variant_id UUID,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    options JSONB,
    modifiers JSONB,
    notes TEXT,
    line_total DECIMAL(12,2) NOT NULL,
    item_status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cart
CREATE TABLE carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    company_id UUID REFERENCES companies(id),
    cafe_id UUID REFERENCES cafes(id),
    subtotal DECIMAL(12,2) DEFAULT 0,
    item_count INT DEFAULT 0,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, cafe_id)
);

-- Cart Items
CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID REFERENCES carts(id),
    product_id UUID REFERENCES products(id),
    variant_id UUID,
    quantity INT NOT NULL DEFAULT 1,
    options JSONB,
    modifiers JSONB,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refresh Tokens
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    token_hash VARCHAR(255) NOT NULL,
    jti VARCHAR(255) UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI Chat Sessions
CREATE TABLE chat_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    model VARCHAR(50) NOT NULL,
    system_prompt TEXT,
    context_window INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI Chat Messages
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES chat_sessions(id),
    role VARCHAR(20) NOT NULL, -- user, assistant, system
    content TEXT NOT NULL,
    model VARCHAR(50),
    tokens_used INT,
    cost DECIMAL(10,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8.2 Indexes

```sql
-- Performance indexes
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_company ON products(company_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at DESC);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_chat_messages_session ON chat_messages(session_id);
```

### 8.3 Relationships

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Company    │────▶│    Cafe     │────▶│   Product   │
└─────────────┘     └─────────────┘     └─────────────┘
       │                                        │
       │                                        │
       ▼                                        ▼
┌─────────────┐                         ┌─────────────┐
│    User     │────────────────────────▶│  Category   │
└─────────────┘                         └─────────────┘
       │
       │
       ├────────────────┬─────────────────┬─────────────▶
       │                │                 │
       ▼                ▼                 ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│    Cart     │  │   Order     │  │User Credit  │
└─────────────┘  └─────────────┘  └─────────────┘
       │                │                 │
       ▼                ▼                 ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────────┐
│ Cart Items  │  │ Order Items │  │Credit Tx        │
└─────────────┘  └─────────────┘  └─────────────────┘
```

---

## 9. Deployment

### 9.1 Docker Compose (Development)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: aicafe
      POSTGRES_PASSWORD: aicafe
      POSTGRES_DB: aicafe
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./migrations:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U aicafe"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  api-gateway:
    build: ./go/api-gateway
    ports:
      - "3000:3000"
    environment:
      - DATABASE_URL=postgres://aicafe:aicafe@postgres:5432/aicafe
      - REDIS_URL=redis://redis:6379
      - JWT_SECRET=change-me-in-production
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  ai-gateway:
    build: ./python/ai-gateway
    ports:
      - "7070:7070"
    environment:
      - DATABASE_URL=postgres://aicafe:aicafe@postgres:5432/aicafe
      - REDIS_URL=redis://redis:6379
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}

volumes:
  postgres_data:
  redis_data:
```

### 9.2 Environment Variables

```bash
# .env.example

# Server
PORT=3000
ENVIRONMENT=development

# Database
DB_HOST=localhost
DB_PORT=5432
DB_USER=aicafe
DB_PASSWORD=aicafe
DB_NAME=aicafe
DB_SSLMODE=disable

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DB=0

# JWT
JWT_SECRET=your-super-secret-key-change-in-production
JWT_ACCESS_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=604800
JWT_ISSUER=aicafe-api

# CORS
CORS_ORIGIN=http://localhost:3001

# Rate Limiting
RATE_LIMIT_ENABLED=true
RATE_LIMIT_CAPACITY=100

# External Services
SMS_PROVIDER=twilio
SMS_API_KEY=
SMS_API_SECRET=
SMS_FROM_NUMBER=

# AI Services
OPENAI_API_KEY=
ANTHROPIC_API_KEY=
```

---

## 10. Monitoring & Observability

### 10.1 Metrics

| Metric | Type | Description |
|--------|------|-------------|
| `http_requests_total` | Counter | Total HTTP requests |
| `http_request_duration_seconds` | Histogram | Request latency |
| `active_connections` | Gauge | Current active connections |
| `credit_balance` | Gauge | User credit balances |
| `ai_tokens_used` | Counter | AI API token usage |
| `ai_cost_total` | Counter | Total AI API cost |

### 10.2 Logging

```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "level": "info",
  "service": "api-gateway",
  "traceId": "abc123",
  "spanId": "def456",
  "message": "Request completed",
  "method": "POST",
  "path": "/v1/orders",
  "statusCode": 201,
  "duration": 45,
  "userId": "user-uuid"
}
```

---

## 11. Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024 | Initial design with 9 microservices |
| 2.0 | 2024 | Updated to reflect actual implementation state |

---

**Document Status:** ACTIVE DEVELOPMENT  
**Last Updated:** 2024
