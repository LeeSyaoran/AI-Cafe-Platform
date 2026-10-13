# AI Café Platform - Product Specification

## 1. Project Overview

### 1.1 Project Name
**AI Café** - Nền tảng truy cập AI với hệ thống tín dụng

### 1.2 Core Functionality
Nền tảng cho phép người dùng truy cập các dịch vụ AI (Chat, Code, Image, Video) thông qua hệ thống tín dụng tích hợp, hỗ trợ thanh toán linh hoạt và chương trình khách hàng thân thiết.

### 1.3 Target Platforms

| Platform | Technology | Users |
|----------|------------|-------|
| **Web** | Next.js 14+ | End-users + Admin |
| **Android** | Expo (React Native) | End-users |
| **iOS** | Expo (React Native) | End-users |

### 1.4 User Types

1. **Guest User** - Người dùng chưa đăng nhập
2. **Registered User** - Người dùng đã đăng ký với các gói membership
3. **Admin User** - Quản trị viên hệ thống

---

## 2. Architecture Overview

### 2.1 System Architecture - Go + Java Hybrid

```
╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                             AI CAFÉ PLATFORM - ARCHITECTURE                                             ║
╠════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║                                                                                                                                     ║
║   ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║   │                                                    CLIENTS                                                   │   ║
║   │   ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────────┐       ┌─────────────────┐   │   ║
║   │   │    Web App      │       │   Mobile App    │       │  Admin Dashboard   │       │   Staff App     │   │   ║
║   │   │   (Next.js)     │       │ (Expo/RN)      │       │    (Next.js)        │       │   (Future)     │   │   ║
║   │   └────────┬────────┘       └────────┬────────┘       └──────────┬──────────┘       └────────┬────────┘   │   ║
║   └────────────┼─────────────────────────┼────────────────────────────┼────────────────────────────┼────────────┘   ║
║                │                         │                            │                            │                 ║
║                └─────────────────────────┼────────────────────────────┘                            │                 ║
║                                          ▼                                                           │                 ║
║   ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║   │                                          API GATEWAY (GO) 🔥                                                    │   ║
║   │   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────────────┐   │   ║
║   │   │ REST API │  │WebSocket │  │ gRPC      │  │ Rate     │  │ JWT      │  │ Service Mesh / Router    │   │   ║
║   │   │          │  │ Handler  │  │ Gateway   │  │ Limiter  │  │ Validate │  │                          │   │   ║
║   │   └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────────────────────┘   │   ║
║   └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘   ║
║                                                    │                                                                  ║
║                          ┌─────────────────────────┼─────────────────────────┐                                   ║
║                          ▼                         ▼                         ▼                                     ║
║   ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║   │                                              GO SERVICES ⚡                                                       │   ║
║   │   ┌─────────────────┐                    ┌─────────────────┐                    ┌─────────────────┐                │   ║
║   │   │   Auth Service  │                    │  Credit Service │                    │  Session Svc    │                │   ║
║   │   │   • OTP Gen     │                    │  • Balance      │                    │  • WebSocket    │                │   ║
║   │   │   • JWT Issue   │                    │  • Deduct        │                    │  • Presence     │                │   ║
║   │   │   • Validation  │                    │  • Budget Limit │                    │                 │                │   ║
║   │   └─────────────────┘                    └─────────────────┘                    └─────────────────┘                │   ║
║   └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘   ║
║                                                    │                                                                  ║
║                          ┌─────────────────────────┼─────────────────────────┬─────────────────────────┐           ║
║                          ▼                         ▼                         ▼                         ▼             ║
║   ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║   │                                              JAVA SERVICES ☕                                                     │   ║
║   │   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐       │   ║
║   │   │  User Service   │  │ Payment Service │  │Member Service  │  │  Order Service  │  │ Loyalty Service │       │   ║
║   │   │  • Profile      │  │  • VNPay        │  │  • Subscript   │  │  • Create       │  │  • Points       │       │   ║
║   │   │  • Settings     │  │  • MoMo        │  │  • Tiers       │  │  • History      │  │  • Rewards      │       │   ║
║   │   │  • Preferences  │  │  • ZaloPay     │  │  • Billing     │  │  • Invoice      │  │  • Referral     │       │   ║
║   │   └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘       │   ║
║   └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘   ║
║                                                    │                                                                  ║
║                          ┌─────────────────────────┼─────────────────────────┐                                   ║
║                          ▼                         ▼                         ▼                                     ║
║   ┌─────────────────────────────────────────────────┐    ┌─────────────────────────────────────────────────┐       ║
║   │              AI GATEWAY (Python) 🧠               │    │                  DATA LAYER 📊                  │       ║
║   │   ┌──────────┐ ┌──────────┐ ┌──────────┐        │    │   ┌─────────────┐     ┌─────────────┐         │       ║
║   │   │   Chat   │ │  Image   │ │  Video   │        │    │   │ PostgreSQL  │     │    Redis    │         │       ║
║   │   │ Handler  │ │ Handler  │ │ Handler  │        │    │   │             │     │             │         │       ║
║   │   └──────────┘ └──────────┘ └──────────┘        │    │   │  • Users    │     │  • Session   │         │       ║
║   │   ┌──────────┐ ┌──────────┐ ┌──────────┐        │    │   │  • Orders   │     │  • Cache     │         │       ║
║   │   │  Router  │ │Classifier│ │ Cost Ctrl│        │    │   │  • Credits │     │  • Queue     │         │       ║
║   │   └──────────┘ └──────────┘ └──────────┘        │    │   └─────────────┘     └─────────────┘         │       ║
║   └─────────────────────────────────────────────────┘    └─────────────────────────────────────────────────┘       ║
║                          │                                                                                         ║
║                          ▼                                                                                         ║
║   ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║   │                                         EXTERNAL AI PROVIDERS 🌐                                                 │   ║
║   │   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐                               │   ║
║   │   │ OpenAI  │  │Anthropic│  │ Google  │  │Stability│  │Replicate│  │  Azure  │                               │   ║
║   │   └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘                               │   ║
║   └─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘   ║
║                                                                                                                                     ║
╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

---

### 2.2 Technology Stack

#### API Gateway & Core Services (Go)

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | **Go** | 1.21+ | High-performance backend |
| **Framework** | **Go Fiber** hoặc **Echo** | Latest | HTTP framework |
| **gRPC** | **google.golang.org/grpc** | Latest | Service communication |
| **Auth** | **golang-jwt/jwt5** | Latest | JWT validation |
| **Validation** | **go-playground/validator** | Latest | Input validation |
| **Database Driver** | **pgx/v5** | Latest | PostgreSQL driver |
| **Cache** | **go-redis/redis/v9** | Latest | Redis client |
| **Logging** | **uber-go/zap** | Latest | Structured logging |
| **Tracing** | **OpenTelemetry** | Latest | Distributed tracing |
| **Config** | **envconfig** | Latest | Environment config |

#### Business Services (Java/Spring Boot)

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | **Java** | 21 LTS | Modern features (Virtual Threads) |
| **Framework** | **Spring Boot** | 3.x | Rapid development |
| **Security** | **Spring Security** | 6.x | Auth, JWT, OAuth2 |
| **Database** | **Spring Data JPA** | Latest | ORM with PostgreSQL |
| **Cache** | **Spring Data Redis** | Latest | Redis integration |
| **gRPC Client** | **grpc-spring-boot** | Latest | gRPC communication |
| **API Docs** | **SpringDoc OpenAPI** | 2.x | Swagger UI |
| **Validation** | **Jakarta Validation** | Latest | Bean validation |
| **Build** | **Gradle** | Latest | Dependency management |

#### AI Gateway (Python)

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Language** | **Python** | 3.12 | AI ecosystem |
| **Framework** | **FastAPI** | 0.109+ | Async API |
| **AI SDKs** | **OpenAI, Anthropic, Google** | Latest | AI providers |
| **Caching** | **Redis** | Latest | Response caching |
| **Validation** | **Pydantic** | Latest | Data validation |

#### Frontend

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Framework** | **Next.js** | 14+ | SSR, App Router |
| **Language** | **TypeScript** | 5.x | Type safety |
| **UI** | **TailwindCSS + shadcn/ui** | Latest | Beautiful UI |
| **State** | **Zustand** | 4.x | Lightweight state |
| **Data Fetch** | **TanStack Query** | 5.x | Caching |
| **Auth** | **NextAuth.js** | 5.x | Authentication |

#### Data Layer

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Primary DB** | **PostgreSQL** | 15+ | Transactional data |
| **Cache/Session** | **Redis** | 7+ | Caching, sessions |
| **Queue** | **BullMQ** hoặc **Machinery** | Latest | Job processing |
| **ORM (Go)** | **pgx + sqlx** | Latest | Type-safe queries |
| **ORM (Java)** | **Spring Data JPA** | Latest | ORM |

#### Infrastructure

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Container** | **Docker** | Containerization |
| **Orchestration** | **AWS ECS Fargate** | Serverless containers |
| **Database** | **AWS RDS PostgreSQL** | Managed database |
| **Cache** | **AWS ElastiCache** | Managed Redis |
| **CDN** | **AWS CloudFront** | Content delivery |
| **CI/CD** | **GitHub Actions** | Automation |
| **IaC** | **Terraform** | Infrastructure as Code |
| **Monitoring** | **CloudWatch + OpenTelemetry** | Observability |
| **Secrets** | **AWS Secrets Manager** | Secrets management |

---

### 2.3 Project Structure

```
ai-cafe-platform/
│
├── apps/
│   ├── web/                          # Next.js - User-facing web app
│   │   ├── app/
│   │   │   ├── (auth)/
│   │   │   ├── (main)/
│   │   │   ├── api/
│   │   │   └── layout.tsx
│   │   ├── components/
│   │   ├── lib/
│   │   └── package.json
│   │
│   ├── mobile/                       # Expo - Mobile app (iOS + Android)
│   │   ├── app/
│   │   ├── components/
│   │   └── package.json
│   │
│   └── admin/                        # Next.js - Admin dashboard
│       ├── app/
│       ├── components/
│       └── package.json
│
├── services/                         # Backend microservices
│   │
│   ├── api-gateway/                 # GO - Main API Gateway
│   │   ├── cmd/
│   │   │   └── server/
│   │   │       └── main.go
│   │   ├── internal/
│   │   │   ├── config/
│   │   │   ├── handler/
│   │   │   ├── middleware/
│   │   │   ├── router/
│   │   │   ├── service/
│   │   │   └── repository/
│   │   ├── pkg/
│   │   │   ├── auth/
│   │   │   ├── grpc/
│   │   │   └── logging/
│   │   ├── proto/
│   │   │   └── aicafe.proto
│   │   ├── go.mod
│   │   ├── go.sum
│   │   └── Dockerfile
│   │
│   ├── auth-service/                 # GO - Authentication service
│   │   ├── cmd/
│   │   │   └── main.go
│   │   ├── internal/
│   │   │   ├── handler/
│   │   │   ├── service/
│   │   │   └── repository/
│   │   ├── proto/
│   │   └── Dockerfile
│   │
│   ├── credit-service/               # GO - Credit management
│   │   ├── cmd/
│   │   │   └── main.go
│   │   ├── internal/
│   │   │   ├── handler/
│   │   │   ├── service/
│   │   │   └── repository/
│   │   ├── proto/
│   │   └── Dockerfile
│   │
│   ├── session-service/              # GO - WebSocket & sessions
│   │   ├── cmd/
│   │   │   └── main.go
│   │   ├── internal/
│   │   │   ├── handler/
│   │   │   ├── hub/                 # WebSocket hub
│   │   │   └── service/
│   │   └── Dockerfile
│   │
│   ├── user-service/                 # JAVA - User management
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/
│   │   │       │       └── aicafe/
│   │   │       │           └── user/
│   │   │       │               ├── controller/
│   │   │       │               ├── service/
│   │   │       │               ├── repository/
│   │   │       │               ├── model/
│   │   │       │               ├── dto/
│   │   │       │               └── config/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── build.gradle
│   │   └── Dockerfile
│   │
│   ├── payment-service/              # JAVA - Payment processing
│   │   ├── src/main/java/com/aicafe/payment/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── dto/
│   │   │   └── config/
│   │   └── Dockerfile
│   │
│   ├── membership-service/            # JAVA - Membership & subscriptions
│   │   ├── src/main/java/com/aicafe/membership/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── dto/
│   │   │   └── config/
│   │   └── Dockerfile
│   │
│   ├── order-service/                 # JAVA - Order management
│   │   ├── src/main/java/com/aicafe/order/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── dto/
│   │   │   └── config/
│   │   └── Dockerfile
│   │
│   ├── loyalty-service/               # JAVA - Loyalty program
│   │   ├── src/main/java/com/aicafe/loyalty/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── dto/
│   │   │   └── config/
│   │   └── Dockerfile
│   │
│   └── ai-gateway/                    # PYTHON - AI services
│       ├── src/
│       │   ├── main.py
│       │   ├── api/
│       │   │   ├── chat.py
│       │   │   ├── image.py
│       │   │   └── video.py
│       │   ├── core/
│       │   │   ├── router.py
│       │   │   ├── classifier.py
│       │   │   └── limiter.py
│       │   └── providers/
│       │       ├── openai.py
│       │       ├── anthropic.py
│       │       └── google.py
│       ├── Dockerfile
│       └── pyproject.toml
│
├── pkg/                               # Shared packages
│   ├── proto/                        # Protocol Buffer definitions
│   │   ├── aicafe/
│   │   │   ├── user.proto
│   │   │   ├── credit.proto
│   │   │   ├── auth.proto
│   │   │   ├── order.proto
│   │   │   └── common.proto
│   │   └── buf.yaml
│   │
│   ├── api-client-go/                 # Go gRPC client
│   │   ├── user/
│   │   ├── credit/
│   │   └── auth/
│   │
│   ├── api-client-java/              # Java gRPC client
│   │   └── com.aicafe.grpc/
│   │
│   └── shared/                        # Shared utilities
│       ├── constants/
│       ├── utils/
│       └── types/
│
├── infrastructure/
│   ├── docker-compose.yml            # Local development
│   ├── docker-compose.prod.yml       # Production
│   └── terraform/                    # AWS infrastructure
│
├── docs/                             # Documentation
└── README.md
```

---

### 2.4 Service Communication

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           SERVICE COMMUNICATION FLOW                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────┐                                                                       │
│  │ Client  │                                                                       │
│  │(Browser)│                                                                       │
│  └────┬────┘                                                                       │
│       │ HTTP/REST                                                                 │
│       ▼                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                    GO API GATEWAY                                           │   │
│  │   • Validates JWT token                                                     │   │
│  │   • Rate limiting                                                            │   │
│  │   • Routes to appropriate service                                            │   │
│  └─────┬───────────────────────────────────────────────────────────────────────┘   │
│        │                                                                             │
│        ├── gRPC ──► Auth Service (Go) ──► OTP, Token validation                   │
│        │                                                                             │
│        ├── gRPC ──► Credit Service (Go) ──► Balance, Deduction                    │
│        │                                                                             │
│        ├── gRPC ──► User Service (Java) ──► Profile, Settings                     │
│        │                                                                             │
│        ├── REST ───► Payment Service (Java) ──► VNPay, MoMo, ZaloPay              │
│        │                                                                             │
│        ├── gRPC ──► Membership Service (Java) ──► Subscriptions                   │
│        │                                                                             │
│        ├── gRPC ──► Order Service (Java) ──► Orders, Invoice                     │
│        │                                                                             │
│        ├── gRPC ──► Loyalty Service (Java) ──► Points, Rewards                   │
│        │                                                                             │
│        └── HTTP ───► AI Gateway (Python) ──► Chat, Image, Video                  │
│                            │                                                      │
│                            ▼                                                      │
│                     ┌─────────────┐                                               │
│                     │ AI Providers│                                               │
│                     │OpenAI, etc.│                                               │
│                     └─────────────┘                                               │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Functionality Specification

### 3.1 Core Features

#### Authentication Module (Go - Auth Service)
- [x] Phone number login with OTP verification
- [x] JWT-based session management
- [x] Refresh token rotation
- [x] Role-based access (user, admin)

#### Credit System (Go - Credit Service)
- [x] Credit wallet per user
- [x] Credit purchase via payment gateway
- [x] Credit deduction on AI usage
- [x] Credit expiration management
- [x] Daily/monthly usage limits by tier

#### AI Services (Python - AI Gateway)
- [x] Chat (GPT-4o, Claude 3.5, Gemini 1.5)
- [x] Code Generation (GPT-4o, Claude 3.5)
- [x] Image Generation (DALL-E 3, Stable Diffusion)
- [x] Video Generation (Sora, Stable Video)
- [x] Real-time streaming responses

#### Membership Tiers (Java - Membership Service)
| Tier | Credits/Month | Workspace Minutes | Rate Limits |
|------|---------------|------------------|-------------|
| Basic | 2,000 | 60 | 10 RPM |
| Developer | 5,000 | 120 | 30 RPM |
| Pro | 10,000 | 240 | 60 RPM |
| Builder | 18,000 | 480 | 120 RPM |

#### Order & Payment (Java - Payment/Order Services)
- [x] Credit package purchases
- [x] Membership subscription
- [x] Payment gateway integration (VNPay, MoMo, ZaloPay)
- [x] Order history
- [x] Invoice generation

#### Loyalty Program (Java - Loyalty Service)
- [x] Points accumulation
- [x] Tier progression (Bronze → Silver → Gold → Platinum)
- [x] Reward redemption
- [x] Referral bonus

#### User Management (Java - User Service)
- [x] Profile management
- [x] Preferences & settings
- [x] Avatar upload
- [x] Notification settings

---

## 4. API Specification

### 4.1 API Gateway Routes (Go)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           API GATEWAY - GO                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  REST Endpoints (External)                                               │
│  ──────────────────────────                                              │
│  POST   /api/v1/auth/send-otp           # Send OTP                      │
│  POST   /api/v1/auth/verify-otp         # Verify OTP & get JWT          │
│  POST   /api/v1/auth/refresh            # Refresh token                  │
│  POST   /api/v1/auth/logout             # Invalidate session            │
│                                                                          │
│  GET    /api/v1/users/me                 # Get current user             │
│  PATCH  /api/v1/users/me                 # Update profile               │
│  GET    /api/v1/users/me/credits         # Get credit balance            │
│  GET    /api/v1/users/me/usage          # Get usage statistics          │
│                                                                          │
│  POST   /api/v1/ai/chat                 # Chat completion              │
│  POST   /api/v1/ai/chat/stream          # Streaming chat                │
│  POST   /api/v1/ai/image                # Image generation             │
│  POST   /api/v1/ai/video                # Video generation             │
│  GET    /api/v1/ai/models               # List available models        │
│                                                                          │
│  GET    /api/v1/orders                  # List orders                  │
│  POST   /api/v1/orders                  # Create order                 │
│  GET    /api/v1/orders/:id             # Get order detail              │
│                                                                          │
│  POST   /api/v1/payments/vnpay/return  # VNPay callback                │
│  POST   /api/v1/payments/momo/return   # MoMo callback                 │
│                                                                          │
│  GET    /api/v1/membership             # Get current membership        │
│  POST   /api/v1/membership/upgrade     # Upgrade tier                  │
│                                                                          │
│  GET    /api/v1/loyalty/points        # Get loyalty points             │
│  GET    /api/v1/loyalty/rewards       # Get available rewards         │
│  POST   /api/v1/loyalty/redeem        # Redeem reward                  │
│                                                                          │
│  WebSocket                                                         │
│  ─────────                                                         │
│  WS     /ws/chat/:roomId              # Real-time chat                 │
│  WS     /ws/notifications             # Real-time notifications        │
│                                                                          │
│  Health Check                                                        │
│  ───────────                                                        │
│  GET    /health                       # Health check                    │
│  GET    /health/ready                 # Readiness check                │
│  GET    /health/live                  # Liveness check                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.2 gRPC Services (Internal)

```protobuf
// Service definitions for internal communication

service AuthService {
  rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);
  rpc GenerateToken(GenerateTokenRequest) returns (GenerateTokenResponse);
  rpc RevokeToken(RevokeTokenRequest) returns (RevokeTokenResponse);
}

service CreditService {
  rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
  rpc Deduct(DeductCreditsRequest) returns (DeductCreditsResponse);
  rpc Add(AddCreditsRequest) returns (AddCreditsResponse);
  rpc CheckLimit(CheckLimitRequest) returns (CheckLimitResponse);
}

service UserService {
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc UpdateUser(UpdateUserRequest) returns (UpdateUserResponse);
  rpc GetProfile(GetProfileRequest) returns (GetProfileResponse);
}
```

---

## 5. Database Schema

### 5.1 PostgreSQL Tables

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    avatar_url VARCHAR(500),
    tier VARCHAR(20) DEFAULT 'BASIC',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Wallets table (managed by Go Credit Service)
CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    credits INTEGER DEFAULT 0,
    workspace_minutes INTEGER DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0  -- Optimistic locking
);

-- Credit transactions table
CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL, -- PURCHASE, DEDUCT, REFUND, EXPIRE
    amount INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    source VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Memberships table
CREATE TABLE memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    tier VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    auto_renew BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL, -- CREDIT, MEMBERSHIP
    status VARCHAR(20) DEFAULT 'PENDING',
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(20),
    payment_id VARCHAR(100),
    items JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Loyalty accounts table
CREATE TABLE loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    tier VARCHAR(20) DEFAULT 'BRONZE',
    points INTEGER DEFAULT 0,
    lifetime_points INTEGER DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Loyalty transactions table
CREATE TABLE loyalty_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES loyalty_accounts(id),
    type VARCHAR(20) NOT NULL, -- EARN, REDEEM, EXPIRE
    points INTEGER NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 6. Security Architecture

### 6.1 Security Layers

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         SECURITY LAYERS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. NETWORK SECURITY                                                     │
│     ├── AWS WAF - Web Application Firewall                              │
│     ├── CloudFront - DDoS protection, CDN                              │
│     ├── Security Groups - Network access control                         │
│     └── Private subnets - Services not directly exposed                  │
│                                                                          │
│  2. API GATEWAY SECURITY (Go)                                          │
│     ├── JWT validation on every request                                  │
│     ├── Rate limiting (100 req/min per user)                            │
│     ├── Input validation & sanitization                                  │
│     ├── CORS configuration                                              │
│     └── Request logging & monitoring                                     │
│                                                                          │
│  3. SERVICE SECURITY                                                     │
│     ├── mTLS between services (gRPC)                                    │
│     ├── Service-to-service authentication                               │
│     ├── Role-based access control (RBAC)                               │
│     └── API key validation for internal services                        │
│                                                                          │
│  4. DATA SECURITY                                                        │
│     ├── Encryption at rest (PostgreSQL, Redis)                          │
│     ├── Encryption in transit (TLS 1.3)                                 │
│     ├── Secrets Manager for credentials                                 │
│     └── Row-level security in database                                  │
│                                                                          │
│  5. APPLICATION SECURITY                                                │
│     ├── SQL injection prevention (parameterized queries)                 │
│     ├── XSS prevention (input sanitization)                            │
│     ├── CSRF protection                                                │
│     ├── Secure headers (HSTS, CSP, etc.)                               │
│     └── Audit logging                                                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 6.2 Authentication Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         AUTHENTICATION FLOW                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────┐     ┌─────────────┐     ┌──────────────────────┐         │
│  │  User   │────▶│  API GW     │────▶│  Auth Service (Go)   │         │
│  │         │     │  Validates   │     │  - OTP Gen/Verify    │         │
│  │ Phone   │     │  Rate Limit  │     │  - JWT Issue         │         │
│  └─────────┘     └─────────────┘     └──────────────────────┘         │
│       │                                        │                        │
│       │ OTP Sent                               │ Access Token           │
│       ◀───────────────────────────────────────┤ + Refresh Token        │
│       │                                        │                        │
│       │ Access Token (JWT)                     │                        │
│       ▼                                        ▼                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    ALL SUBSEQUENT REQUESTS                        │   │
│  │                                                                  │   │
│  │  ┌─────────┐     ┌─────────────┐     ┌──────────────────────┐   │   │
│  │  │ Request │────▶│  API GW     │────▶│  Services (Go/Java)  │   │   │
│  │  │ + JWT   │     │  Decodes    │     │  - Validate JWT      │   │   │
│  │  │         │     │  JWT        │     │  - Check permissions │   │   │
│  │  └─────────┘     └─────────────┘     └──────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Performance & Scalability

### 7.1 Performance Targets

| Metric | Target | Description |
|--------|--------|-------------|
| API Gateway Latency | < 50ms p99 | 95th percentile |
| Credit Deduction | < 10ms | Real-time operations |
| AI Response (streaming) | < 3s first token | Chat responses |
| Database Queries | < 100ms p99 | Read operations |
| WebSocket Connections | 10,000+ per instance | Concurrent users |

### 7.2 Scaling Strategy

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         SCALING STRATEGY                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  API Gateway (Go)                                                       │
│  ├── Horizontal scaling: 2-10 instances                                 │
│  ├── Auto-scaling based on CPU > 70%                                    │
│  └── Target: 100k-500k requests/second                                 │
│                                                                          │
│  Auth Service (Go)                                                      │
│  ├── Horizontal scaling: 2-5 instances                                  │
│  ├── Redis for session storage                                          │
│  └── Target: 50k authentications/second                                 │
│                                                                          │
│  Credit Service (Go)                                                    │
│  ├── Horizontal scaling: 2-5 instances                                  │
│  ├── Optimistic locking for consistency                                │
│  └── Target: 10k credit operations/second                              │
│                                                                          │
│  Java Services (Spring Boot)                                           │
│  ├── Horizontal scaling: 2-8 instances per service                     │
│  ├── Auto-scaling based on request latency                             │
│  └── Connection pooling (HikariCP)                                     │
│                                                                          │
│  AI Gateway (Python)                                                   │
│  ├── Horizontal scaling: 2-10 instances                                │
│  ├── Auto-scaling based on queue depth                                  │
│  └── Upstream rate limiting from AI providers                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Milestones

### Phase 1: Foundation (Week 1-2)
- [x] Project structure (monorepo)
- [x] Proto definitions
- [x] Go API Gateway setup
- [x] PostgreSQL schema & migrations
- [x] Docker Compose for local dev

### Phase 2: Core Services (Week 3-4)
- [x] Go Auth Service (OTP, JWT)
- [x] Go Credit Service
- [x] Java User Service
- [x] Java Order Service
- [x] gRPC integration

### Phase 3: AI & Payments (Week 5-6)
- [ ] Python AI Gateway
- [ ] Java Payment Service (VNPay, MoMo)
- [ ] Java Membership Service
- [ ] AI provider integrations

### Phase 4: Frontend & Integration (Week 7-8)
- [ ] Next.js Web App
- [ ] Expo Mobile App
- [ ] Admin Dashboard
- [ ] End-to-end testing

### Phase 5: Production (Week 9-10)
- [ ] AWS infrastructure (Terraform)
- [ ] CI/CD pipelines
- [ ] Monitoring & alerting
- [ ] Load testing
- [ ] Production deployment

---

## 9. Documentation Files

| Document | Description |
|----------|-------------|
| SPEC.md | This file - Product specification |
| BACKEND_DESIGN.md | Backend architecture (Go + Java) |
| AI_GATEWAY_DESIGN.md | AI services architecture |
| INFRASTRUCTURE_DESIGN.md | AWS infrastructure |
| DATABASE_SCHEMA.md | Database design |
| API_SPEC.md | API documentation |
| SECURITY.md | Security architecture |
| PROTOCOLS.md | gRPC protocol definitions |
| DEPLOYMENT.md | Deployment guide |
| CONTRIBUTING.md | Development guidelines |

---

## 10. Tech Stack Summary

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                      AI CAFÉ PLATFORM - TECH STACK                       ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                           ║
║  FRONTEND                          BACKEND                               ║
║  ───────                           ───────                               ║
║  Next.js + TypeScript         │     Go (API Gateway)                    ║
║  TailwindCSS + shadcn/ui       │     Go Fiber/Echo                      ║
║  Zustand + TanStack Query      │     gRPC                               ║
║                                  ├─────────────────────────────────────  ║
║                                  │     GO SERVICES                       ║
║                                  │     • Auth Service                    ║
║                                  │     • Credit Service                  ║
║                                  │     • Session Service                 ║
║                                  ├─────────────────────────────────────  ║
║                                  │     JAVA SERVICES (Spring Boot)       ║
║                                  │     • User Service                    ║
║                                  │     • Payment Service                 ║
║                                  │     • Membership Service              ║
║                                  │     • Order Service                   ║
║                                  │     • Loyalty Service                 ║
║                                  ├─────────────────────────────────────  ║
║                                  │     PYTHON (AI Gateway)               ║
║                                  │     FastAPI + OpenAI SDK              ║
║                                                                           ║
║  DATA LAYER                                                              ║
║  ───────────                                                             ║
║  PostgreSQL + Redis + BullMQ                                            ║
║                                                                           ║
║  INFRASTRUCTURE                                                         ║
║  ───────────────                                                         ║
║  AWS ECS Fargate + RDS + ElastiCache + CloudFront + Terraform           ║
║                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════╝
```

---

## 11. Contact & Support

- **Email**: support@aicafe.vn
- **Website**: https://aicafe.vn
- **Documentation**: https://docs.aicafe.vn
