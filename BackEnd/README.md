# BackEnd

Hệ thống backend AI Café Platform theo kiến trúc **3 ngôn ngữ**:

| Ngôn ngữ | Services | Port | Framework |
|----------|----------|------|-----------|
| **Go 🔥** | API Gateway, Auth, Credit, Session | 3000-3003 | Fiber/Echo |
| **Java ☕** | User, Payment, Member, Order, Loyalty | 3004-3008 | Spring Boot 3.x |
| **Python 🐍** | AI Gateway | 4000 | FastAPI |

---

## Cấu trúc

```
BackEnd/
│
├── go/                               # ==================== GO SERVICES ====================
│   ├── api-gateway/                  # Port 3000 - Main API Gateway
│   │   ├── cmd/server/main.go        # Entry point
│   │   ├── internal/
│   │   │   ├── handler/             # HTTP handlers
│   │   │   ├── middleware/          # JWT, Rate limiter, CORS
│   │   │   ├── model/               # Data models
│   │   │   ├── repository/          # Database access
│   │   │   ├── service/             # Business logic
│   │   │   └── config/              # Config loader
│   │   ├── pkg/
│   │   │   ├── response/            # API response helpers
│   │   │   ├── jwt/                 # JWT utilities
│   │   │   └── validator/           # Input validation
│   │   └── proto/                   # gRPC proto files
│   │
│   ├── auth-service/                 # Port 3001 - Auth & OTP
│   │   ├── cmd/server/
│   │   ├── internal/handler,middleware,model,repository,service,config
│   │   └── pkg/jwt, otp/
│   │
│   ├── credit-service/               # Port 3002 - Credit balance & deduction
│   │   ├── cmd/server/
│   │   └── internal/handler,middleware,model,repository,service,config
│   │
│   └── session-service/               # Port 3003 - WebSocket & session
│       ├── cmd/server/
│       └── internal/handler,middleware,model,repository,service,config
│
├── java/                             # ==================== JAVA SERVICES ====================
│   ├── user-service/                 # Port 3004 - User profiles & settings
│   │   └── src/main/java/com/example/backend/
│   │       ├── UserServiceApplication.java
│   │       ├── entity/              # JPA entities
│   │       ├── exception/          # ApiException, GlobalExceptionHandler
│   │       ├── repository/         # Spring Data JPA
│   │       ├── request/            # Request DTOs
│   │       ├── response/           # ApiResponse<T>
│   │       ├── security/           # JWT, Security config
│   │       ├── service/            # @Service classes
│   │       └── config/             # @Configuration classes
│   │
│   ├── payment-service/              # Port 3005 - VNPay, MoMo, ZaloPay
│   │   └── src/main/java/com/example/backend/{entity,exception,repository,request,response,security,service,config}/
│   │
│   ├── member-service/               # Port 3006 - Subscriptions & billing
│   │   └── src/main/java/com/example/backend/{entity,exception,repository,request,response,security,service,config}/
│   │
│   ├── order-service/                # Port 3007 - Orders & invoices
│   │   └── src/main/java/com/example/backend/{entity,exception,repository,request,response,security,service,config}/
│   │
│   └── loyalty-service/               # Port 3008 - Points & rewards
│       └── src/main/java/com/example/backend/{entity,exception,repository,request,response,security,service,config}/
│
├── python/                           # ==================== PYTHON SERVICES ====================
│   └── ai-gateway/                   # Port 4000 - AI chat, image, video
│       ├── main.py                   # FastAPI entry point
│       ├── requirements.txt
│       ├── app/
│       │   ├── handlers/            # Request handlers
│       │   ├── routers/            # API routes (chat, models, sessions, credits)
│       │   ├── services/            # AI provider services
│       │   ├── models/              # Pydantic models
│       │   ├── schemas/             # Request/Response schemas
│       │   ├── core/               # Config, database, redis
│       │   └── utils/              # Utilities
│       ├── tests/
│       └── scripts/
│
├── docker/
│   ├── docker-compose.yml           # Full stack (Go + Java + Python + DB)
│   └── docker-compose.infra.yml    # Infrastructure only (Postgres, Redis, MinIO)
│
└── docs/
    ├── API_REFERENCE.md
    ├── BACKEND_DESIGN.md
    ├── SECURITY.md
    └── api/REST_API.md
```

---

## Tech Stack chi tiết

### Go Services 🔥
| Component | Technology |
|-----------|------------|
| Language | Go 1.21+ |
| Framework | Fiber v2 |
| Database | pgx/v5 (PostgreSQL driver) |
| Cache | go-redis/v9 |
| Auth | golang-jwt/jwt/v5 |
| Validation | go-playground/validator |
| Logging | uber-go/zap |
| gRPC | google.golang.org/grpc |

### Java Services ☕
| Component | Technology |
|-----------|------------|
| Language | Java 21 LTS |
| Framework | Spring Boot 3.3.0 |
| Build | Maven |
| ORM | Spring Data JPA + Hibernate 6 |
| Security | Spring Security 6 + JWT (jjwt 0.12.5) |
| Database | PostgreSQL 16 + Flyway |
| Cache | Redis 7 |
| Docs | SpringDoc OpenAPI 2.6.0 |

### Python AI Gateway 🐍
| Component | Technology |
|-----------|------------|
| Language | Python 3.12 |
| Framework | FastAPI 0.109+ |
| AI SDKs | OpenAI, Anthropic, Google Generative AI |
| Database | PostgreSQL (async via asyncpg) |
| Cache | Redis |
| Validation | Pydantic v2 |
| Server | Uvicorn |

---

## Ports

```
Port 3000  → API Gateway (Go)
Port 3001  → Auth Service (Go)
Port 3002  → Credit Service (Go)
Port 3003  → Session Service (Go)
Port 3004  → User Service (Java)
Port 3005  → Payment Service (Java)
Port 3006  → Member Service (Java)
Port 3007  → Order Service (Java)
Port 3008  → Loyalty Service (Java)
Port 4000  → AI Gateway (Python)
Port 5432  → PostgreSQL
Port 6379  → Redis
Port 9000  → MinIO (S3)
```

---

## Quick Start

```bash
# 1. Start infrastructure
cd BackEnd/docker
docker-compose -f docker-compose.infra.yml up -d

# 2. Go Services
cd go/api-gateway && go run cmd/server/main.go
# (repeat for auth-service, credit-service, session-service)

# 3. Java Services
cd java/user-service && ./mvnw spring-boot:run
# (repeat for payment, member, order, loyalty services)

# 4. Python AI Gateway
cd python/ai-gateway
pip install -r requirements.txt
python main.py
```

---

## API Documentation

```
Go API Gateway:     http://localhost:3000/docs
Java Services:      http://localhost:3004/swagger-ui.html
Python AI Gateway:  http://localhost:4000/docs
```

Xem chi tiết: [docs/api/REST_API.md](docs/api/REST_API.md)