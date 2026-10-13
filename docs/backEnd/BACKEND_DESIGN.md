# Backend Design - Go + Java Hybrid Architecture

## 1. Architecture Overview

```
╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                          BACKEND ARCHITECTURE                                                   ║
╠════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║                                                                                                                                     ║
║    ┌──────────────────────────────────────────────────────────────────────────────────────────────────────────┐   ║
║    │                                                 CLIENTS                                                   │   ║
║    │         ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────────┐                       │   ║
║    │         │    Web App     │       │   Mobile App    │       │  Admin Dashboard   │                       │   ║
║    │         │   (Next.js)    │       │  (Expo/RN)     │       │    (Next.js)      │                       │   ║
║    │         └────────┬────────┘       └────────┬────────┘       └──────────┬──────────┘                       │   ║
║    └────────────────────┼────────────────────────┼────────────────────────────┼────────────────────────────────┘   ║
║                         │                        │                            │                                 ║
║                         └────────────────────────┼────────────────────────────┘                                 ║
║                                                   │                                                             ║
║    ┌─────────────────────────────────────────────┼─────────────────────────────────────────────────────────────┐ ║
║    │                                       API GATEWAY (GO) 🔥                                                 │ ║
║    │   ┌─────────────────────────────────────────────────────────────────────────────────────────────┐       │ ║
║    │   │ • HTTP/REST Handler (Go Fiber)   • JWT Validation   • Rate Limiting                         │       │ ║
║    │   │ • WebSocket Hub                   • Request Logging  • Service Routing (gRPC/HTTP)          │       │ ║
║    │   └─────────────────────────────────────────────────────────────────────────────────────────────┘       │ ║
║    └─────────────────────────────────────────────┬─────────────────────────────────────────────────────────────┘ ║
║                                                   │                                                             ║
║           ┌──────────────────────────────────────┼──────────────────────────────────────┐                   ║
║           │                                      │                                      │                   ║
║           ▼                                      ▼                                      ▼                   ║
║    ┌─────────────────────────────────┐  ┌─────────────────────────────────┐  ┌─────────────────────────────────┐ ║
║    │        GO SERVICES ⚡           │  │        GO SERVICES ⚡            │  │        GO SERVICES ⚡            │ ║
║    │                                 │  │                                 │  │                                 │ ║
║    │  ┌─────────────────────────┐   │  │  ┌─────────────────────────┐   │  │  ┌─────────────────────────┐   │ ║
║    │  │    AUTH SERVICE         │   │  │  │   CREDIT SERVICE       │   │  │  │   SESSION SERVICE       │   │ ║
║    │  │   • OTP Generation      │   │  │  │   • Balance Check      │   │  │  │   • WebSocket Hub       │   │ ║
║    │  │   • OTP Verification     │   │  │  │   • Credit Deduction   │   │  │  │   • Presence Tracking   │   │ ║
║    │  │   • JWT Generation       │   │  │  │   • Budget Enforcement│   │  │  │   • Real-time Events    │   │ ║
║    │  │   • Token Validation     │   │  │  │   • Daily Limits      │   │  │  │   • Heartbeat          │   │ ║
║    │  └─────────────────────────┘   │  │  └─────────────────────────┘   │  │  └─────────────────────────┘   │ ║
║    └─────────────────────────────────┘  └─────────────────────────────────┘  └─────────────────────────────────┘ ║
║                                                   │                                                             ║
║           ┌──────────────────────────────────────┼──────────────────────────────────────┐                   ║
║           │                                      │                                      │                   ║
║           ▼                                      ▼                                      ▼                   ║
║    ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐ ║
║    │                                          JAVA SERVICES ☕                                                    │ ║
║    │                                                                                                             │ ║
║    │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │ ║
║    │  │  USER SERVICE   │  │PAYMENT SERVICE │  │MEMBERSHIP SVC   │  │  ORDER SERVICE  │  │ LOYALTY SERVICE │  │ ║
║    │  │  • Profile CRUD │  │  • VNPay       │  │  • Subscriptions│  │  • Order CRUD   │  │  • Points CRUD  │  │ ║
║    │  │  • Settings     │  │  • MoMo       │  │  • Tier Mgmt    │  │  • Invoice Gen  │  │  • Rewards      │  │ ║
║    │  │  • Preferences │  │  • ZaloPay    │  │  • Billing Cycle│  │  • History      │  │  • Referral     │  │ ║
║    │  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘  │ ║
║    └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘ ║
║                                                   │                                                             ║
║                                                   ▼                                                             ║
║    ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐ ║
║    │                                         AI GATEWAY (Python) 🧠                                             │ ║
║    │   ┌────────────────┐   ┌────────────────┐   ┌────────────────┐   ┌────────────────────────────────────┐  │ ║
║    │   │   Chat API     │   │  Image API     │   │  Video API     │   │        Core Services             │  │ ║
║    │   │   • GPT-4o     │   │   • DALL-E 3   │   │   • Sora       │   │  • Model Router                  │  │ ║
║    │   │   • Claude 3.5 │   │   • SDXL       │   │   • SD Video   │   │  • Cost Controller               │  │ ║
║    │   │   • Gemini 1.5 │   │   • FLUX       │   │                │   │  • Rate Limiter                  │  │ ║
║    │   └────────────────┘   └────────────────┘   └────────────────┘   └────────────────────────────────────┘  │ ║
║    └─────────────────────────────────────────────┬─────────────────────────────────────────────────────────────┘ ║
║                                                   │                                                             ║
║    ┌─────────────────────────────────────────────┼─────────────────────────────────────────────────────────────┐ ║
║    │                                              │ DATA LAYER                                                 │ ║
║    │   ┌─────────────────────┐   ┌─────────────────────┐   ┌─────────────────────────────────────────────┐  │ ║
║    │   │     PostgreSQL      │   │       Redis         │   │          Message Queue (BullMQ)              │  │ ║
║    │   │                     │   │                     │   │                                             │  │ ║
║    │   │  • Users, Orders    │   │  • Session Store    │   │  • Async credit deductions                 │  │ ║
║    │   │  • Memberships      │   │  • Rate Limit      │   │  • Loyalty points processing               │  │ ║
║    │   │  • Loyalty          │   │  • AI Response     │   │  • Payment webhooks                        │  │ ║
║    │   │  • Credit Trans    │   │    Cache           │   │  • Notifications                          │  │ ║
║    │   │                     │   │  • Job Queue      │   │                                             │  │ ║
║    │   └─────────────────────┘   └─────────────────────┘   └─────────────────────────────────────────────┘  │ ║
║    └─────────────────────────────────────────────────────────────────────────────────────────────────────────┘ ║
║                                                                                                                                     ║
╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
```

---

## 2. Service Responsibilities

### 2.1 Go Services (Performance Critical)

| Service | Language | Port | Purpose | SLO |
|---------|----------|------|---------|-----|
| **API Gateway** | Go | 8080 | Main entry, routing, JWT validation | < 50ms p99 |
| **Auth Service** | Go | 8081 | OTP, JWT generation, validation | < 100ms |
| **Credit Service** | Go | 8082 | Balance, deduction, limits | < 10ms |
| **Session Service** | Go | 8083 | WebSocket, presence, real-time | 10k connections |

### 2.2 Java Services (Business Logic)

| Service | Language | Port | Purpose | SLO |
|---------|----------|------|---------|-----|
| **User Service** | Java 21 | 9090 | Profile, settings, preferences | < 200ms |
| **Payment Service** | Java 21 | 9091 | VNPay, MoMo, ZaloPay integration | < 500ms |
| **Membership Service** | Java 21 | 9092 | Subscriptions, tiers, billing | < 200ms |
| **Order Service** | Java 21 | 9093 | Order management, invoices | < 300ms |
| **Loyalty Service** | Java 21 | 9094 | Points, rewards, referrals | < 200ms |

### 2.3 Python Services (AI)

| Service | Language | Port | Purpose | SLO |
|---------|----------|------|---------|-----|
| **AI Gateway** | Python 3.12 | 7070 | Chat, Image, Video APIs | < 3s first token |

---

## 3. API Gateway (Go) - Detailed Design

### 3.1 Technology Stack

```go
// go.mod
module github.com/aicafe/api-gateway

go 1.21

require (
    // HTTP Framework
    github.com/gofiber/fiber/v2 v2.52.0
    github.com/gofiber/adaptor/v2 v2.2.1
    
    // gRPC
    google.golang.org/grpc v1.60.0
    google.golang.org/protobuf v1.32.0
    github.com/grpc-ecosystem/grpc-gateway/v2 v2.19.0
    
    // Auth
    github.com/golang-jwt/jwt/v5 v5.2.0
    
    // Database
    github.com/jackc/pgx/v5 v5.5.0
    
    // Redis
    github.com/redis/go-redis/v9 v9.4.0
    
    // Config
    github.com/kelseyhightower/envconfig v1.4.0
    
    // Logging
    go.uber.org/zap v1.26.0
    
    // Validation
    github.com/go-playground/validator/v10 v10.17.0
    
    // Rate Limiting
    github.com/ulule/limiter/v3 v3.11.2
    
    // WebSocket
    github.com/gorilla/websocket v1.5.1
    
    // OpenTelemetry
    go.opentelemetry.io/otel v1.23.0
)
```

### 3.2 Project Structure

```
services/api-gateway/
├── cmd/
│   └── server/
│       └── main.go                 # Entry point
│
├── internal/
│   ├── config/
│   │   └── config.go              # Configuration
│   │
│   ├── handler/
│   │   ├── auth.go                # Auth endpoints
│   │   ├── user.go                # User endpoints
│   │   ├── credit.go              # Credit endpoints
│   │   ├── order.go               # Order endpoints
│   │   ├── ai.go                  # AI endpoints
│   │   ├── membership.go           # Membership endpoints
│   │   ├── loyalty.go              # Loyalty endpoints
│   │   └── websocket.go            # WebSocket handler
│   │
│   ├── middleware/
│   │   ├── auth.go                # JWT middleware
│   │   ├── ratelimit.go           # Rate limiting
│   │   ├── logging.go             # Request logging
│   │   ├── cors.go                # CORS
│   │   ├── recovery.go            # Panic recovery
│   │   └── tracing.go             # OpenTelemetry
│   │
│   ├── router/
│   │   └── router.go              # Route definitions
│   │
│   ├── service/
│   │   ├── auth_client.go         # gRPC client to Auth Service
│   │   ├── credit_client.go        # gRPC client to Credit Service
│   │   ├── user_client.go          # gRPC client to User Service
│   │   └── session_client.go       # gRPC client to Session Service
│   │
│   ├── repository/
│   │   └── audit.go               # Audit logging
│   │
│   └── dto/
│       ├── request.go             # Request DTOs
│       └── response.go             # Response DTOs
│
├── pkg/
│   ├── auth/
│   │   ├── jwt.go                 # JWT utilities
│   │   └── claims.go              # JWT claims
│   │
│   ├── grpc/
│   │   ├── client.go              # gRPC client setup
│   │   └── interceptor.go         # gRPC interceptors
│   │
│   ├── logging/
│   │   └── logger.go              # Logger setup
│   │
│   └── middleware/
│       └── fiber.go               # Fiber middleware helpers
│
├── proto/
│   ├── aicafe/
│   │   ├── auth.proto
│   │   ├── credit.proto
│   │   ├── user.proto
│   │   └── common.proto
│   └── buf.yaml
│
├── Dockerfile
├── docker-compose.yaml
├── Makefile
└── README.md
```

### 3.3 Main Entry Point

```go
// cmd/server/main.go
package main

import (
    "context"
    "fmt"
    "log"
    "net/http"
    "os"
    "os/signal"
    "syscall"
    "time"

    "github.com/gofiber/fiber/v2"
    "github.com/gofiber/fiber/v2/middleware/cors"
    "github.com/gofiber/fiber/v2/middleware/recover"
    "github.com/gofiber/adaptor/v2"
    "go.uber.org/zap"

    "github.com/aicafe/api-gateway/internal/config"
    "github.com/aicafe/api-gateway/internal/handler"
    "github.com/aicafe/api-gateway/internal/middleware"
    "github.com/aicafe/api-gateway/internal/router"
    "github.com/aicafe/api-gateway/pkg/logging"
   grpcserver "github.com/aicafe/api-gateway/pkg/grpc"
)

func main() {
    // Initialize logger
    logger, _ := logging.NewLogger()
    defer logger.Sync()

    // Load configuration
    cfg := config.Load()

    // Initialize gRPC clients
    grpcClients, err := grpcserver.NewClients(logger, cfg)
    if err != nil {
        log.Fatalf("Failed to initialize gRPC clients: %v", err)
    }
    defer grpcClients.Close()

    // Create Fiber app
    app := fiber.New(fiber.Config{
        AppName:      "AI Café API Gateway",
        ReadTimeout:  30 * time.Second,
        WriteTimeout: 30 * time.Second,
        IdleTimeout:  120 * time.Second,
        ErrorHandler: customErrorHandler(logger),
    })

    // Global middleware
    app.Use(recover.New())
    app.Use(cors.New(cors.Config{
        AllowOrigins:     cfg.CORS.AllowOrigins,
        AllowMethods:     "GET,POST,PUT,PATCH,DELETE,OPTIONS",
        AllowHeaders:     "Origin,Content-Type,Accept,Authorization,X-Request-ID",
        ExposeHeaders:    "X-Request-ID",
        AllowCredentials: true,
    }))

    // Initialize handlers
    handlers := handler.NewHandlers(logger, grpcClients, cfg)

    // Setup routes
    router.Setup(app, handlers, logger)

    // Health check endpoints
    app.Get("/health", handlers.Health.Health)
    app.Get("/health/ready", handlers.Health.Ready)
    app.Get("/health/live", handlers.Health.Live)

    // Start server
    serverAddr := fmt.Sprintf(":%d", cfg.Server.Port)
    go func() {
        logger.Info("Starting API Gateway",
            zap.String("addr", serverAddr),
            zap.String("env", cfg.App.Env),
        )
        if err := app.Listen(serverAddr); err != nil {
            logger.Fatal("Failed to start server", zap.Error(err))
        }
    }()

    // Graceful shutdown
    quit := make(chan os.Signal, 1)
    signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
    <-quit

    logger.Info("Shutting down server...")
    ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()

    if err := app.ShutdownWithContext(ctx); err != nil {
        logger.Error("Server forced to shutdown", zap.Error(err))
    }

    logger.Info("Server exited")
}

func customErrorHandler(logger *zap.Logger) fiber.ErrorHandler {
    return func(c *fiber.Ctx, err error) error {
        code := fiber.StatusInternalServerError
        message := "Internal Server Error"

        if e, ok := err.(*fiber.Error); ok {
            code = e.Code
            message = e.Message
        }

        logger.Error("Request error",
            zap.Int("status", code),
            zap.String("error", err.Error()),
            zap.String("path", c.Path()),
            zap.String("method", c.Method()),
        )

        return c.Status(code).JSON(fiber.Map{
            "error": message,
            "code":  code,
        })
    }
}
```

### 3.4 Router Setup

```go
// internal/router/router.go
package router

import (
    "github.com/gofiber/fiber/v2"
    "go.uber.org/zap"

    "github.com/aicafe/api-gateway/internal/handler"
    "github.com/aicafe/api-gateway/internal/middleware"
)

type Router struct {
    app      *fiber.App
    handlers *handler.Handlers
    logger   *zap.Logger
}

func Setup(app *fiber.App, handlers *handler.Handlers, logger *zap.Logger) {
    r := &Router{
        app:      app,
        handlers: handlers,
        logger:   logger,
    }

    // API v1 routes
    api := app.Group("/api/v1")

    // Health (no auth required)
    api.Get("/health", r.handlers.Health.Health)

    // Auth routes (no auth required)
    auth := api.Group("/auth")
    auth.Post("/send-otp", r.handlers.Auth.SendOTP)
    auth.Post("/verify-otp", r.handlers.Auth.VerifyOTP)

    // Protected routes (require JWT)
    protected := api.Group("", middleware.Auth(handlers.JWT))

    // Auth (protected)
    protected.Post("/auth/refresh", r.handlers.Auth.Refresh)
    protected.Post("/auth/logout", r.handlers.Auth.Logout)

    // User routes
    users := protected.Group("/users")
    users.Get("/me", r.handlers.User.GetMe)
    users.Patch("/me", r.handlers.User.UpdateMe)
    users.Get("/me/credits", r.handlers.Credit.GetBalance)
    users.Get("/me/usage", r.handlers.User.GetUsage)
    users.Get("/me/membership", r.handlers.Membership.GetMyMembership)

    // AI routes
    ai := protected.Group("/ai")
    ai.Post("/chat", r.handlers.AI.Chat)
    ai.Post("/chat/stream", r.handlers.AI.ChatStream)
    ai.Post("/image", r.handlers.AI.Image)
    ai.Post("/video", r.handlers.AI.Video)
    ai.Get("/models", r.handlers.AI.GetModels)

    // Order routes
    orders := protected.Group("/orders")
    orders.Get("/", r.handlers.Order.List)
    orders.Post("/", r.handlers.Order.Create)
    orders.Get("/:id", r.handlers.Order.Get)
    orders.Get("/:id/invoice", r.handlers.Order.GetInvoice)

    // Payment routes (webhook)
    payments := api.Group("/payments")
    payments.Post("/vnpay/return", r.handlers.Payment.VNPayReturn)
    payments.Post("/vnpay/ipn", r.handlers.Payment.VNPayIPN)
    payments.Post("/momo/return", r.handlers.Payment.MoMoReturn)
    payments.Post("/momo/ipn", r.handlers.Payment.MoMoIPN)

    // Membership routes
    membership := protected.Group("/membership")
    membership.Get("/", r.handlers.Membership.Get)
    membership.Post("/subscribe", r.handlers.Membership.Subscribe)
    membership.Post("/upgrade", r.handlers.Membership.Upgrade)
    membership.Post("/cancel", r.handlers.Membership.Cancel)

    // Loyalty routes
    loyalty := protected.Group("/loyalty")
    loyalty.Get("/points", r.handlers.Loyalty.GetPoints)
    loyalty.Get("/history", r.handlers.Loyalty.GetHistory)
    loyalty.Get("/rewards", r.handlers.Loyalty.GetRewards)
    loyalty.Post("/redeem", r.handlers.Loyalty.Redeem)
    loyalty.Get("/tier", r.handlers.Loyalty.GetTier)

    // WebSocket
    app.Use("/ws", r.handlers.WebSocket.Handler)
}
```

### 3.5 JWT Middleware

```go
// internal/middleware/auth.go
package middleware

import (
    "strings"

    "github.com/gofiber/fiber/v2"

    "github.com/aicafe/api-gateway/pkg/auth"
)

type AuthMiddleware struct {
    jwtService *auth.JWTService
}

func NewAuthMiddleware(jwtService *auth.JWTService) *AuthMiddleware {
    return &AuthMiddleware{jwtService: jwtService}
}

func (m *AuthMiddleware) Handler() fiber.Handler {
    return func(c *fiber.Ctx) error {
        // Get token from header
        authHeader := c.Get("Authorization")
        if authHeader == "" {
            return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
                "error": "Missing authorization header",
                "code":  "UNAUTHORIZED",
            })
        }

        // Check Bearer prefix
        parts := strings.SplitN(authHeader, " ", 2)
        if len(parts) != 2 || strings.ToLower(parts[0]) != "bearer" {
            return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
                "error": "Invalid authorization header format",
                "code":  "INVALID_TOKEN",
            })
        }

        tokenString := parts[1]

        // Validate token
        claims, err := m.jwtService.ValidateToken(tokenString)
        if err != nil {
            return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
                "error": "Invalid or expired token",
                "code":  "INVALID_TOKEN",
            })
        }

        // Store claims in context
        c.Locals("user_id", claims.UserID)
        c.Locals("phone", claims.Phone)
        c.Locals("tier", claims.Tier)
        c.Locals("role", claims.Role)

        return c.Next()
    }
}

// Auth helper for router
func Auth(jwtService *auth.JWTService) fiber.Handler {
    return NewAuthMiddleware(jwtService).Handler()
}
```

---

## 4. Auth Service (Go) - Detailed Design

### 4.1 Technology Stack

```go
// go.mod
module github.com/aicafe/auth-service

go 1.21

require (
    github.com/jmoiron/sqlx v1.3.5
    github.com/jackc/pgx/v5 v5.5.0
    github.com/redis/go-redis/v9 v9.4.0
    github.com/golang-jwt/jwt/v5 v5.2.0
    google.golang.org/grpc v1.60.0
    google.golang.org/protobuf v1.32.0
    github.com/twilio/twilio-go v1.12.0
    github.com/afex/hystrix-go v0.0.0-20180812160131-7de47a074bb5
    go.uber.org/zap v1.26.0
    github.com/kelseyhightower/envconfig v1.4.0
)
```

### 4.2 Project Structure

```
services/auth-service/
├── cmd/
│   └── main.go
│
├── internal/
│   ├── config/
│   ├── handler/
│   │   ├── grpc.go              # gRPC handlers
│   │   └── http.go              # Health check HTTP
│   │
│   ├── service/
│   │   ├── otp.go               # OTP generation & verification
│   │   ├── token.go             # JWT generation
│   │   └── session.go            # Session management
│   │
│   ├── repository/
│   │   ├── user.go              # User lookup
│   │   └── otp.go               # OTP storage
│   │
│   └── proto/
│       └── auth.pb.go           # Generated protobuf
│
├── pkg/
│   ├── otp/
│   │   ├── generator.go         # OTP generation
│   │   └── sender.go            # SMS sender (Twilio)
│   │
│   └── jwt/
│       └── provider.go           # JWT provider
│
└── Dockerfile
```

### 4.3 OTP Service

```go
// internal/service/otp.go
package service

import (
    "context"
    "crypto/rand"
    "fmt"
    "math/big"
    "time"

    "github.com/redis/go-redis/v9"
    "go.uber.org/zap"

    "github.com/aicafe/auth-service/pkg/otp"
)

type OTPService struct {
    redis  *redis.Client
    sender *otp.Sender
    logger *zap.Logger
    
    // Config
    otpLength    int = 6
    otpExpires   time.Duration = 5 * time.Minute
    maxAttempts  int = 3
    resendDelay  time.Duration = 60 * time.Second
}

func NewOTPService(redis *redis.Client, sender *otp.Sender, logger *zap.Logger) *OTPService {
    return &OTPService{
        redis:  redis,
        sender: sender,
        logger: logger,
    }
}

func (s *OTPService) generateOTP() (string, error) {
    const digits = "0123456789"
    otp := make([]byte, s.otpLength)
    
    for i := range otp {
        num, err := rand.Int(rand.Reader, big.NewInt(int64(len(digits))))
        if err != nil {
            return "", err
        }
        otp[i] = digits[num.Int64()]
    }
    
    return string(otp), nil
}

type SendOTPResult struct {
    Success    bool
    MessageID  string
    RateLimit  bool
    RetryAfter time.Duration
}

func (s *OTPService) SendOTP(ctx context.Context, phone string) (*SendOTPResult, error) {
    logger := s.logger.With(zap.String("phone", maskPhone(phone)))
    
    // Check rate limit
    rateLimitKey := fmt.Sprintf("otp:ratelimit:%s", phone)
    exists, err := s.redis.Exists(ctx, rateLimitKey).Result()
    if err != nil {
        logger.Error("Failed to check rate limit", zap.Error(err))
        return nil, err
    }
    
    if exists > 0 {
        ttl, _ := s.redis.TTL(ctx, rateLimitKey).Result()
        logger.Info("Rate limited", zap.Duration("retry_after", ttl))
        return &SendOTPResult{
            Success:    false,
            RateLimit:  true,
            RetryAfter: ttl,
        }, nil
    }
    
    // Generate OTP
    otp, err := s.generateOTP()
    if err != nil {
        logger.Error("Failed to generate OTP", zap.Error(err))
        return nil, err
    }
    
    // Store OTP in Redis
    otpKey := fmt.Sprintf("otp:code:%s", phone)
    err = s.redis.Set(ctx, otpKey, otp, s.otpExpires).Err()
    if err != nil {
        logger.Error("Failed to store OTP", zap.Error(err))
        return nil, err
    }
    
    // Set rate limit
    err = s.redis.Set(ctx, rateLimitKey, "1", s.resendDelay).Err()
    if err != nil {
        logger.Error("Failed to set rate limit", zap.Error(err))
    }
    
    // Attempt to send SMS
    messageID, err := s.sender.Send(phone, fmt.Sprintf("Your AI Cafe OTP is: %s", otp))
    if err != nil {
        logger.Error("Failed to send OTP SMS", zap.Error(err))
        // Still return success - don't reveal SMS failures to user
    }
    
    logger.Info("OTP sent successfully", zap.String("message_id", messageID))
    
    return &SendOTPResult{
        Success:   true,
        MessageID: messageID,
    }, nil
}

type VerifyOTPResult struct {
    Valid    bool
    UserID   string
    IsNewUser bool
}

func (s *OTPService) VerifyOTP(ctx context.Context, phone, code string) (*VerifyOTPResult, error) {
    logger := s.logger.With(zap.String("phone", maskPhone(phone)))
    
    otpKey := fmt.Sprintf("otp:code:%s", phone)
    
    // Get stored OTP
    storedOTP, err := s.redis.Get(ctx, otpKey).Result()
    if err == redis.Nil {
        logger.Warn("OTP not found or expired")
        return &VerifyOTPResult{Valid: false}, nil
    }
    if err != nil {
        logger.Error("Failed to get OTP", zap.Error(err))
        return nil, err
    }
    
    // Check attempts
    attemptsKey := fmt.Sprintf("otp:attempts:%s", phone)
    attempts, _ := s.redis.Incr(ctx, attemptsKey).Result()
    s.redis.Expire(ctx, attemptsKey, s.otpExpires)
    
    if attempts > int64(s.maxAttempts) {
        logger.Warn("Too many OTP attempts", zap.Int64("attempts", attempts))
        s.redis.Del(ctx, otpKey)
        return &VerifyOTPResult{Valid: false}, nil
    }
    
    // Verify code
    if storedOTP != code {
        logger.Warn("Invalid OTP", zap.Int64("attempt", attempts))
        return &VerifyOTPResult{Valid: false}, nil
    }
    
    // Success - delete OTP
    s.redis.Del(ctx, otpKey)
    s.redis.Del(ctx, attemptsKey)
    
    logger.Info("OTP verified successfully")
    
    return &VerifyOTPResult{
        Valid:     true,
        IsNewUser: true, // Will be determined by caller based on user lookup
    }, nil
}

func maskPhone(phone string) string {
    if len(phone) < 4 {
        return "****"
    }
    return phone[:3] + "****" + phone[len(phone)-2:]
}
```

### 4.4 JWT Service

```go
// pkg/jwt/provider.go
package jwt

import (
    "errors"
    "time"

    "github.com/golang-jwt/jwt/v5"
)

var (
    ErrInvalidToken = errors.New("invalid token")
    ErrExpiredToken = errors.New("token has expired")
)

type Claims struct {
    jwt.RegisteredClaims
    UserID string `json:"user_id"`
    Phone  string `json:"phone"`
    Tier   string `json:"tier"`
    Role   string `json:"role"`
}

type JWTService struct {
    secretKey     []byte
    accessExpiry  time.Duration
    refreshExpiry time.Duration
}

func NewJWTService(secretKey string, accessExpiry, refreshExpiry time.Duration) *JWTService {
    return &JWTService{
        secretKey:     []byte(secretKey),
        accessExpiry:  accessExpiry,
        refreshExpiry: refreshExpiry,
    }
}

type TokenPair struct {
    AccessToken  string `json:"access_token"`
    RefreshToken string `json:"refresh_token"`
    ExpiresIn   int64  `json:"expires_in"`
    TokenType   string `json:"token_type"`
}

func (s *JWTService) GenerateTokenPair(userID, phone, tier, role string) (*TokenPair, error) {
    now := time.Now()
    
    accessClaims := &Claims{
        RegisteredClaims: jwt.RegisteredClaims{
            Subject:   userID,
            Issuer:    "aicafe",
            Audience:  jwt.ClaimStrings{"aicafe-app"},
            ExpiresAt: jwt.NewNumericDate(now.Add(s.accessExpiry)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
            JWTID:     generateUUID(),
        },
        UserID: userID,
        Phone:  phone,
        Tier:   tier,
        Role:   role,
    }
    
    refreshClaims := &Claims{
        RegisteredClaims: jwt.RegisteredClaims{
            Subject:   userID,
            Issuer:    "aicafe",
            Audience:  jwt.ClaimStrings{"aicafe-refresh"},
            ExpiresAt: jwt.NewNumericDate(now.Add(s.refreshExpiry)),
            NotBefore: jwt.NewNumericDate(now),
            IssuedAt:  jwt.NewNumericDate(now),
            JWTID:     generateUUID(),
        },
        UserID: userID,
        Phone:  phone,
        Tier:   tier,
        Role:   role,
    }
    
    accessToken := jwt.NewWithClaims(jwt.SigningMethodHS256, accessClaims)
    accessTokenString, err := accessToken.SignedString(s.secretKey)
    if err != nil {
        return nil, err
    }
    
    refreshToken := jwt.NewWithClaims(jwt.SigningMethodHS256, refreshClaims)
    refreshTokenString, err := refreshToken.SignedString(s.secretKey)
    if err != nil {
        return nil, err
    }
    
    return &TokenPair{
        AccessToken:   accessTokenString,
        RefreshToken:  refreshTokenString,
        ExpiresIn:     int64(s.accessExpiry.Seconds()),
        TokenType:     "Bearer",
    }, nil
}

func (s *JWTService) ValidateToken(tokenString string) (*Claims, error) {
    token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
        if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
            return nil, ErrInvalidToken
        }
        return s.secretKey, nil
    })
    
    if err != nil {
        if errors.Is(err, jwt.ErrTokenExpired) {
            return nil, ErrExpiredToken
        }
        return nil, ErrInvalidToken
    }
    
    claims, ok := token.Claims.(*Claims)
    if !ok || !token.Valid {
        return nil, ErrInvalidToken
    }
    
    return claims, nil
}

func (s *JWTService) ValidateRefreshToken(tokenString string) (*Claims, error) {
    claims, err := s.ValidateToken(tokenString)
    if err != nil {
        return nil, err
    }
    
    // Verify it's a refresh token (longer expiry)
    if claims.Audience[0] != "aicafe-refresh" {
        return nil, ErrInvalidToken
    }
    
    return claims, nil
}
```

---

## 5. Credit Service (Go) - Detailed Design

### 5.1 Project Structure

```
services/credit-service/
├── cmd/
│   └── main.go
│
├── internal/
│   ├── config/
│   ├── handler/
│   │   └── grpc.go               # gRPC handlers
│   │
│   ├── service/
│   │   ├── credit.go             # Credit operations
│   │   ├── limit.go             # Usage limits
│   │   └── transaction.go        # Transaction logging
│   │
│   ├── repository/
│   │   └── credit.go             # Database operations
│   │
│   └── proto/
│       └── credit.pb.go
│
└── Dockerfile
```

### 5.2 Credit Service Implementation

```go
// internal/service/credit.go
package service

import (
    "context"
    "fmt"
    "time"

    "github.com/jackc/pgx/v5"
    "github.com/jackc/pgx/v5/pgxpool"
    "github.com/redis/go-redis/v9"
    "go.uber.org/zap"

    pb "github.com/aicafe/credit-service/internal/proto"
)

type CreditService struct {
    db    *pgxpool.Pool
    redis *redis.Client
    logger *zap.Logger
}

func NewCreditService(db *pgxpool.Pool, redis *redis.Client, logger *zap.Logger) *CreditService {
    return &CreditService{
        db:    db,
        redis: redis,
        logger: logger,
    }
}

type CreditBalance struct {
    UserID            string
    Credits           int
    WorkspaceMinutes  int
    Version           int
}

func (s *CreditService) GetBalance(ctx context.Context, userID string) (*CreditBalance, error) {
    // Try cache first
    cacheKey := fmt.Sprintf("credit:balance:%s", userID)
    cached, err := s.redis.Get(ctx, cacheKey).Result()
    if err == nil {
        // Parse cached balance (format: "credits:minutes:version")
        var credits, minutes, version int
        fmt.Sscanf(cached, "%d:%d:%d", &credits, &minutes, &version)
        return &CreditBalance{
            UserID:           userID,
            Credits:          credits,
            WorkspaceMinutes: minutes,
            Version:          version,
        }, nil
    }
    
    // Cache miss - query database
    var balance CreditBalance
    err = s.db.QueryRow(ctx, `
        SELECT user_id, credits, workspace_minutes, version 
        FROM wallets WHERE user_id = $1
    `, userID).Scan(&balance.UserID, &balance.Credits, &balance.WorkspaceMinutes, &balance.Version)
    
    if err == pgx.ErrNoRows {
        // Create wallet if not exists
        balance = CreditBalance{
            UserID:           userID,
            Credits:          0,
            WorkspaceMinutes: 0,
            Version:          0,
        }
        _, err = s.db.Exec(ctx, `
            INSERT INTO wallets (user_id, credits, workspace_minutes, version)
            VALUES ($1, 0, 0, 0)
        `, userID)
        if err != nil {
            s.logger.Error("Failed to create wallet", zap.Error(err))
            return nil, err
        }
    } else if err != nil {
        s.logger.Error("Failed to get balance", zap.Error(err))
        return nil, err
    }
    
    // Update cache
    cacheValue := fmt.Sprintf("%d:%d:%d", balance.Credits, balance.WorkspaceMinutes, balance.Version)
    s.redis.Set(ctx, cacheKey, cacheValue, 5*time.Minute)
    
    return &balance, nil
}

type DeductResult struct {
    Success     bool
    NewBalance  int
    Insufficient bool
}

func (s *CreditService) DeductCredits(ctx context.Context, req *pb.DeductCreditsRequest) (*DeductResult, error) {
    logger := s.logger.With(
        zap.String("user_id", req.UserId),
        zap.Int32("amount", req.Amount),
        zap.String("service", req.Service),
    )
    
    // Check limit first
    if err := s.checkDailyLimit(ctx, req.UserId, req.Amount); err != nil {
        logger.Warn("Daily limit exceeded")
        return &DeductResult{Success: false, Insufficient: true}, nil
    }
    
    // Use optimistic locking for atomic deduction
    for attempts := 0; attempts < 3; attempts++ {
        // Get current balance with version
        balance, err := s.GetBalance(ctx, req.UserId)
        if err != nil {
            return nil, err
        }
        
        // Check sufficient balance
        if balance.Credits < int(req.Amount) {
            return &DeductResult{
                Success:     false,
                NewBalance:  balance.Credits,
                Insufficient: true,
            }, nil
        }
        
        // Attempt atomic update with optimistic locking
        newBalance := balance.Credits - int(req.Amount)
        result, err := s.db.Exec(ctx, `
            UPDATE wallets 
            SET credits = $1, updated_at = NOW(), version = version + 1
            WHERE user_id = $2 AND version = $3 AND credits >= $4
        `, newBalance, req.UserId, balance.Version, req.Amount)
        
        if err != nil {
            logger.Error("Failed to deduct credits", zap.Error(err))
            return nil, err
        }
        
        rowsAffected := result.RowsAffected()
        if rowsAffected == 0 {
            // Version mismatch - another transaction modified it
            // Invalidate cache and retry
            s.redis.Del(ctx, fmt.Sprintf("credit:balance:%s", req.UserId))
            logger.Info("Optimistic lock conflict, retrying", zap.Int("attempt", attempts+1))
            continue
        }
        
        // Success - log transaction
        s.logTransaction(ctx, req.UserId, "DEDUCT", -int(req.Amount), newBalance, req.Service, req.Description)
        
        // Update daily usage
        s.incrementDailyUsage(ctx, req.UserId, req.Amount)
        
        // Invalidate cache
        s.redis.Del(ctx, fmt.Sprintf("credit:balance:%s", req.UserId))
        
        logger.Info("Credits deducted", zap.Int("new_balance", newBalance))
        
        return &DeductResult{
            Success:    true,
            NewBalance: newBalance,
        }, nil
    }
    
    return nil, fmt.Errorf("failed to deduct credits after 3 attempts")
}

func (s *CreditService) AddCredits(ctx context.Context, userID string, amount int, source, description string) error {
    logger := s.logger.With(
        zap.String("user_id", userID),
        zap.Int("amount", amount),
        zap.String("source", source),
    )
    
    // Atomic add
    result, err := s.db.Exec(ctx, `
        UPDATE wallets 
        SET credits = credits + $1, updated_at = NOW(), version = version + 1
        WHERE user_id = $2
    `, amount, userID)
    
    if err != nil {
        logger.Error("Failed to add credits", zap.Error(err))
        return err
    }
    
    if result.RowsAffected() == 0 {
        // Wallet doesn't exist, create it
        _, err = s.db.Exec(ctx, `
            INSERT INTO wallets (user_id, credits, workspace_minutes, version)
            VALUES ($1, $2, 0, 0)
        `, userID, amount)
        if err != nil {
            logger.Error("Failed to create wallet with credits", zap.Error(err))
            return err
        }
    }
    
    // Get new balance
    var newBalance int
    s.db.QueryRow(ctx, "SELECT credits FROM wallets WHERE user_id = $1", userID).Scan(&newBalance)
    
    // Log transaction
    s.logTransaction(ctx, userID, "PURCHASE", amount, newBalance, source, description)
    
    // Invalidate cache
    s.redis.Del(ctx, fmt.Sprintf("credit:balance:%s", userID))
    
    logger.Info("Credits added", zap.Int("new_balance", newBalance))
    
    return nil
}

func (s *CreditService) checkDailyLimit(ctx context.Context, userID string, amount int32) error {
    dailyKey := fmt.Sprintf("credit:daily:%s:%s", userID, time.Now().Format("2006-01-02"))
    
    current, err := s.redis.Get(ctx, dailyKey).Int()
    if err != nil && err != redis.Nil {
        return err
    }
    
    // Default limit based on tier (will be loaded from config)
    dailyLimit := int32(100000) // Default
    
    if int(current)+int(amount) > int(dailyLimit) {
        return fmt.Errorf("daily limit exceeded")
    }
    
    return nil
}

func (s *CreditService) incrementDailyUsage(ctx context.Context, userID string, amount int32) {
    dailyKey := fmt.Sprintf("credit:daily:%s:%s", userID, time.Now().Format("2006-01-02"))
    s.redis.IncrBy(ctx, dailyKey, int64(amount))
    s.redis.Expire(ctx, dailyKey, 24*time.Hour)
}

func (s *CreditService) logTransaction(ctx context.Context, userID, txType string, amount, balanceAfter int, service, description string) {
    s.db.Exec(ctx, `
        INSERT INTO credit_transactions (user_id, type, amount, balance_after, source, description)
        VALUES ($1, $2, $3, $4, $5, $6)
    `, userID, txType, amount, balanceAfter, service, description)
}
```

---

## 6. Java Services (Spring Boot) - Overview

### 6.1 Common Spring Boot Configuration

```yaml
# application.yml - Common configuration for all Java services
spring:
  application:
    name: ${SERVICE_NAME}
  
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379
      timeout: 2000ms

grpc:
  client:
    global:
      negotiationType: plaintext

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: INFO
    com.aicafe: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 6.2 User Service - Project Structure

```
services/user-service/
├── src/main/
│   ├── java/com/aicafe/user/
│   │   ├── UserServiceApplication.java
│   │   │
│   │   ├── config/
│   │   │   ├── GrpcConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── OpenApiConfig.java
│   │   │
│   │   ├── controller/
│   │   │   ├── UserController.java
│   │   │   └── ProfileController.java
│   │   │
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── ProfileService.java
│   │   │
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── UserPreferencesRepository.java
│   │   │
│   │   ├── model/
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   └── UserPreferences.java
│   │   │   └── dto/
│   │   │       ├── UserDTO.java
│   │   │       ├── CreateUserRequest.java
│   │   │       └── UpdateUserRequest.java
│   │   │
│   │   ├── grpc/
│   │   │   └── UserGrpcService.java
│   │   │
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── UserNotFoundException.java
│   │       └── DuplicatePhoneException.java
│   │
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           └── V1__init_schema.sql
│
├── build.gradle
└── Dockerfile
```

### 6.3 User Entity

```java
// model/entity/User.java
package com.aicafe.user.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "phone", unique = true, nullable = false, length = 20)
    private String phone;
    
    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 20)
    @Builder.Default
    private UserTier tier = UserTier.BASIC;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreferences preferences;
    
    public enum UserTier {
        BASIC, DEVELOPER, PRO, BUILDER
    }
    
    public enum UserStatus {
        ACTIVE, SUSPENDED, DELETED
    }
}
```

### 6.4 User Service

```java
// service/UserService.java
package com.aicafe.user.service;

import com.aicafe.user.model.entity.User;
import com.aicafe.user.model.dto.*;
import com.aicafe.user.repository.UserRepository;
import com.aicafe.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findByIdWithPreferences(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return UserDTO.fromEntity(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("Phone: " + phone));
        return UserDTO.fromEntity(user);
    }
    
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        log.info("Creating user with phone: {}", maskPhone(request.getPhone()));
        
        User user = User.builder()
                .phone(request.getPhone())
                .name(request.getName())
                .email(request.getEmail())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getId());
        
        return UserDTO.fromEntity(savedUser);
    }
    
    @Transactional
    public UserDTO updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", userId);
        
        return UserDTO.fromEntity(updatedUser);
    }
    
    @Transactional
    public void updateUserTier(UUID userId, User.UserTier tier) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.setTier(tier);
        userRepository.save(user);
        
        log.info("User tier updated: {} -> {}", userId, tier);
    }
    
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 2);
    }
}
```

### 6.5 Payment Service - VNPay Integration

```java
// service/PaymentService.java
package com.aicafe.payment.service;

import com.aicafe.payment.config.VNPayConfig;
import com.aicafe.payment.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final VNPayConfig vnpayConfig;
    
    public String createVNPayUrl(PaymentRequest request) {
        Map<String, String> vnpParams = new TreeMap<>();
        
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // Convert to cents
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_BankCode", request.getBankCode() != null ? request.getBankCode() : "");
        vnpParams.put("vnp_TxnRef", request.getOrderId());
        vnpParams.put("vnp_OrderInfo", request.getDescription());
        vnpParams.put("vnp_OrderType", request.getOrderType());
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", request.getIpAddress());
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnpParams.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(
                new Date(System.currentTimeMillis() + 15 * 60 * 1000))); // 15 minutes
        
        // Build hash data
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (value != null && !value.isEmpty()) {
                hashData.append(key).append('=').append(value);
                query.append(key).append('=').append(value);
                
                if (!entry.equals(vnpParams.lastEntry())) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }
        
        // Create secure hash
        String secureHash = hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
        
        // Build final URL
        query.append("&vnp_SecureHash=").append(secureHash);
        
        return vnpayConfig.getPaymentUrl() + "?" + query;
    }
    
    public PaymentResult processVNPayReturn(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        
        // Remove secure hash from params for verification
        Map<String, String> verifyParams = new TreeMap<>(params);
        verifyParams.remove("vnp_SecureHash");
        verifyParams.remove("vnp_SecureHashType");
        
        // Build hash data
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        
        // Verify secure hash
        String expectedHash = hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
        
        if (!secureHash.equals(expectedHash)) {
            log.warn("Invalid VNPay return signature");
            return PaymentResult.builder()
                    .success(false)
                    .message("Invalid signature")
                    .build();
        }
        
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");
        String amount = params.get("vnp_Amount");
        
        boolean success = "00".equals(responseCode);
        
        log.info("VNPay return: txnRef={}, responseCode={}, success={}", 
                txnRef, responseCode, success);
        
        return PaymentResult.builder()
                .success(success)
                .orderId(txnRef)
                .transactionId(transactionNo)
                .amount(Long.parseLong(amount) / 100)
                .responseCode(responseCode)
                .message(getResponseMessage(responseCode))
                .build();
    }
    
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKey);
            byte[] hash = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }
    
    private String getResponseMessage(String code) {
        return switch (code) {
            case "00" -> "Giao dịch thành công";
            case "07" -> "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch trùng lặp)";
            case "09" -> "Thẻ/Tài khoản chưa đăng ký Internet Banking";
            case "10" -> "Thẻ/Tài khoản chưa verify";
            case "11" -> "Đã hết hạn mức giao dịch";
            case "12" -> "Thẻ/Tài khoản bị khóa";
            case "13" -> "Nhập sai mật khẩu quá 3 lần";
            case "24" -> "Khách hàng hủy giao dịch";
            case "51" -> "Tài khoản không đủ số dư";
            case "65" -> "Tài khoản đã vượt quá hạn mức giao dịch trong ngày";
            case "99" -> "Các lỗi khác";
            default -> "Lỗi không xác định";
        };
    }
}
```

---

## 7. Service Communication

### 7.1 gRPC Protocol Definitions

```protobuf
// proto/aicafe/auth.proto
syntax = "proto3";

package aicafe.auth;

option go_package = "github.com/aicafe/proto/aicafe/auth";

service AuthService {
  rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);
  rpc GenerateToken(GenerateTokenRequest) returns (GenerateTokenResponse);
  rpc RevokeToken(RevokeTokenRequest) returns (RevokeTokenResponse);
}

message ValidateTokenRequest {
  string token = 1;
}

message ValidateTokenResponse {
  bool valid = 1;
  string user_id = 2;
  string phone = 3;
  string tier = 4;
  string role = 5;
}

message GenerateTokenRequest {
  string user_id = 1;
  string phone = 2;
  string tier = 3;
  string role = 4;
}

message GenerateTokenResponse {
  string access_token = 1;
  string refresh_token = 2;
  int64 expires_in = 3;
}

message RevokeTokenRequest {
  string token = 1;
}

message RevokeTokenResponse {
  bool success = 1;
}
```

```protobuf
// proto/aicafe/credit.proto
syntax = "proto3";

package aicafe.credit;

option go_package = "github.com/aicafe/proto/aicafe/credit";

service CreditService {
  rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
  rpc DeductCredits(DeductCreditsRequest) returns (DeductCreditsResponse);
  rpc AddCredits(AddCreditsRequest) returns (AddCreditsResponse);
  rpc CheckLimit(CheckLimitRequest) returns (CheckLimitResponse);
}

message GetBalanceRequest {
  string user_id = 1;
}

message GetBalanceResponse {
  string user_id = 1;
  int32 credits = 2;
  int32 workspace_minutes = 3;
}

message DeductCreditsRequest {
  string user_id = 1;
  int32 amount = 2;
  string service = 3;
  string description = 4;
}

message DeductCreditsResponse {
  bool success = 1;
  int32 new_balance = 2;
  string error = 3;
}

message AddCreditsRequest {
  string user_id = 1;
  int32 amount = 2;
  string source = 3;
  string description = 4;
}

message AddCreditsResponse {
  bool success = 1;
  int32 new_balance = 2;
}

message CheckLimitRequest {
  string user_id = 1;
  int32 requested_amount = 2;
  string service = 3;
}

message CheckLimitResponse {
  bool allowed = 1;
  int32 current_usage = 2;
  int32 limit = 3;
}
```

```protobuf
// proto/aicafe/user.proto
syntax = "proto3";

package aicafe.user;

option java_package = "com.aicafe.grpc.user";
option java_multiple_files = true;

service UserService {
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc UpdateUser(UpdateUserRequest) returns (UpdateUserResponse);
  rpc GetUserProfile(GetUserProfileRequest) returns (UserProfile);
}

message GetUserRequest {
  string user_id = 1;
}

message GetUserResponse {
  User user = 1;
}

message User {
  string id = 1;
  string phone = 2;
  string name = 3;
  string email = 4;
  string avatar_url = 5;
  string tier = 6;
  int64 created_at = 7;
}

message UpdateUserRequest {
  string user_id = 1;
  string name = 2;
  string email = 3;
  string avatar_url = 4;
}

message UpdateUserResponse {
  User user = 1;
}

message GetUserProfileRequest {
  string user_id = 1;
}

message UserProfile {
  User user = 1;
  int32 total_orders = 2;
  int32 loyalty_points = 3;
  string loyalty_tier = 4;
  int64 member_since = 5;
}
```

---

## 8. Configuration Management

### 8.1 Environment Variables

```bash
# API Gateway
API_GATEWAY_PORT=8080
API_GATEWAY_ENV=development

# JWT
JWT_SECRET_KEY=your-256-bit-secret-key-here
JWT_ACCESS_EXPIRY=15m
JWT_REFRESH_EXPIRY=7d

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aicafe
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# gRPC Services
AUTH_SERVICE_URL=localhost:8081
CREDIT_SERVICE_URL=localhost:8082
SESSION_SERVICE_URL=localhost:8083
USER_SERVICE_URL=localhost:9090

# CORS
CORS_ALLOW_ORIGINS=http://localhost:3000,https://aicafe.vn

# External APIs
TWILIO_ACCOUNT_SID=your-twilio-sid
TWILIO_AUTH_TOKEN=your-twilio-token
TWILIO_PHONE_NUMBER=+1234567890

# VNPay
VNPAY_TMN_CODE=your-tmn-code
VNPAY_SECRET_KEY=your-secret-key
VNPAY_RETURN_URL=https://aicafe.vn/payment/return
VNPAY_PAYMENT_URL=https://sandbox.vnpayment.vn

# AI Providers
OPENAI_API_KEY=sk-...
ANTHROPIC_API_KEY=sk-ant-...
GOOGLE_AI_API_KEY=...
```

### 8.2 Go Configuration

```go
// internal/config/config.go
package config

import (
    "os"
    "time"

    "github.com/kelseyhightower/envconfig"
)

type Config struct {
    App       AppConfig
    Server    ServerConfig
    Database  DatabaseConfig
    Redis     RedisConfig
    JWT       JWTConfig
    CORS      CORSConfig
    Services  ServicesConfig
}

type AppConfig struct {
    Env string `envconfig:"APP_ENV" default:"development"`
}

type ServerConfig struct {
    Port int `envconfig:"API_GATEWAY_PORT" default:"8080"`
}

type DatabaseConfig struct {
    Host     string `envconfig:"DB_HOST" default:"localhost"`
    Port     int    `envconfig:"DB_PORT" default:"5432"`
    Name     string `envconfig:"DB_NAME" default:"aicafe"`
    Username string `envconfig:"DB_USERNAME" default:"postgres"`
    Password string `envconfig:"DB_PASSWORD" default:"postgres"`
}

type RedisConfig struct {
    Host     string `envconfig:"REDIS_HOST" default:"localhost"`
    Port     int    `envconfig:"REDIS_PORT" default:"6379"`
    Password string `envconfig:"REDIS_PASSWORD" default:""`
}

type JWTConfig struct {
    SecretKey     string        `envconfig:"JWT_SECRET_KEY" required:"true"`
    AccessExpiry  time.Duration `envconfig:"JWT_ACCESS_EXPIRY" default:"15m"`
    RefreshExpiry time.Duration `envconfig:"JWT_REFRESH_EXPIRY" default:"168h"` // 7 days
}

type CORSConfig struct {
    AllowOrigins string `envconfig:"CORS_ALLOW_ORIGINS" default:"*"`
}

type ServicesConfig struct {
    AuthServiceURL    string `envconfig:"AUTH_SERVICE_URL" default:"localhost:8081"`
    CreditServiceURL  string `envconfig:"CREDIT_SERVICE_URL" default:"localhost:8082"`
    SessionServiceURL string `envconfig:"SESSION_SERVICE_URL" default:"localhost:8083"`
    UserServiceURL    string `envconfig:"USER_SERVICE_URL" default:"localhost:9090"`
}

func Load() *Config {
    var cfg Config
    envconfig.Process("", &cfg)
    return &cfg
}
```

---

## 9. Docker Configuration

### 9.1 API Gateway Dockerfile

```dockerfile
# services/api-gateway/Dockerfile
FROM golang:1.21-alpine AS builder

WORKDIR /app

# Install dependencies
RUN apk add --no-cache git ca-certificates

# Copy go mod files
COPY go.mod go.sum ./
RUN go mod download

# Copy source code
COPY . .

# Build
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o api-gateway ./cmd/server

# Final stage
FROM alpine:3.19

RUN apk --no-cache add ca-certificates tzdata

WORKDIR /app

# Copy binary
COPY --from=builder /app/api-gateway .

# Copy default configuration
COPY config.yaml /app/config.yaml

# Expose port
EXPOSE 8080

# Run
CMD ["./api-gateway"]
```

### 9.2 Java Service Dockerfile

```dockerfile
# services/user-service/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy gradle files
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build
RUN ./gradlew bootJar --no-daemon

# Final stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar
COPY --from=builder /build/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Expose port
EXPOSE 9090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:9090/actuator/health || exit 1

# Run
ENTRYPOINT ["java", "-jar", "-Xms256m", "-Xmx512m", "/app/app.jar"]
```

### 9.3 Docker Compose for Development

```yaml
# infrastructure/docker-compose.yml
version: '3.8'

services:
  # PostgreSQL
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: aicafe
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # API Gateway (Go)
  api-gateway:
    build:
      context: ../services/api-gateway
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: aicafe
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      REDIS_HOST: redis
      REDIS_PORT: 6379
      AUTH_SERVICE_URL: auth-service:8081
      CREDIT_SERVICE_URL: credit-service:8082
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  # Auth Service (Go)
  auth-service:
    build:
      context: ../services/auth-service
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: aicafe
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      REDIS_HOST: redis
      REDIS_PORT: 6379
      TWILIO_ACCOUNT_SID: ${TWILIO_ACCOUNT_SID}
      TWILIO_AUTH_TOKEN: ${TWILIO_AUTH_TOKEN}
      TWILIO_PHONE_NUMBER: ${TWILIO_PHONE_NUMBER}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  # Credit Service (Go)
  credit-service:
    build:
      context: ../services/credit-service
      dockerfile: Dockerfile
    ports:
      - "8082:8082"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: aicafe
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  # User Service (Java)
  user-service:
    build:
      context: ../services/user-service
      dockerfile: Dockerfile
    ports:
      - "9090:9090"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: aicafe
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy

  # AI Gateway (Python)
  ai-gateway:
    build:
      context: ../services/ai-gateway
      dockerfile: Dockerfile
    ports:
      - "7070:7070"
    environment:
      REDIS_HOST: redis
      REDIS_PORT: 6379
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      ANTHROPIC_API_KEY: ${ANTHROPIC_API_KEY}
    depends_on:
      redis:
        condition: service_healthy

volumes:
  postgres_data:
  redis_data:
```

---

## 10. Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                        AWS REGION                                     │
│                                                                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                              VPC (10.0.0.0/16)                                  │   │
│  │                                                                                 │   │
│  │  ┌──────────────────────────────────────────────────────────────────────────┐  │   │
│  │  │                     PUBLIC SUBNET (10.0.1.0/24)                            │  │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │  │   │
│  │  │  │ CloudFront  │  │   WAF       │  │  ALB        │                        │  │   │
│  │  │  │ (CDN)       │  │ (Firewall) │  │  (Load Bal) │                        │  │   │
│  │  │  └─────────────┘  └─────────────┘  └──────┬──────┘                        │  │   │
│  │  └────────────────────────────────────────────┼───────────────────────────────┘  │   │
│  │                                                   │                                │   │
│  │  ┌─────────────────────────────────────────────┼───────────────────────────────┐  │   │
│  │  │                    PRIVATE SUBNET (10.0.2.0/24)                             │  │   │
│  │  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │   │
│  │  │  │                    ECS FARGATE - API GATEWAY                         │   │  │   │
│  │  │  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐                │   │  │   │
│  │  │  │  │ Go API │ │ Go API │ │ Go API │ │ Go API │ │ Go API │                │   │  │   │
│  │  │  │  │ GW #1  │ │ GW #2  │ │ GW #3  │ │ GW #4  │ │ GW #5  │                │   │  │   │
│  │  │  │  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘                │   │  │   │
│  │  │  └─────────────────────────────────────────────────────────────────────┘   │  │   │
│  │  │                                                                           │  │   │
│  │  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │   │
│  │  │  │                    ECS FARGATE - GO SERVICES                          │   │  │   │
│  │  │  │  ┌────────────┐ ┌────────────┐ ┌────────────┐                        │   │  │   │
│  │  │  │  │Auth Svc x2 │ │Credit Svc  │ │Session Svc │                       │   │  │   │
│  │  │  │  │            │ │    x3      │ │    x2      │                       │   │  │   │
│  │  │  │  └────────────┘ └────────────┘ └────────────┘                        │   │  │   │
│  │  │  └─────────────────────────────────────────────────────────────────────┘   │  │   │
│  │  │                                                                           │  │   │
│  │  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │   │
│  │  │  │                   ECS FARGATE - JAVA SERVICES                       │   │  │   │
│  │  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐         │   │  │   │
│  │  │  │  │User Svc│ │Payment  │ │Member-  │ │Order   │ │Loyalty  │         │   │  │   │
│  │  │  │  │  x2     │ │  Svc x2  │ │ship x2  │ │Svc x2  │ │Svc x2   │         │   │  │   │
│  │  │  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘         │   │  │   │
│  │  │  └─────────────────────────────────────────────────────────────────────┘   │  │   │
│  │  │                                                                           │  │   │
│  │  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │   │
│  │  │  │                   ECS FARGATE - AI GATEWAY                          │   │  │   │
│  │  │  │  ┌────────────┐ ┌────────────┐ ┌────────────┐                        │   │  │   │
│  │  │  │  │AI Gateway │ │AI Gateway  │ │AI Gateway  │                        │   │  │   │
│  │  │  │  │   #1       │ │   #2       │ │   #3       │                        │   │  │   │
│  │  │  │  └────────────┘ └────────────┘ └────────────┘                        │   │  │   │
│  │  │  └─────────────────────────────────────────────────────────────────────┘   │  │   │
│  │  │                                                                           │  │   │
│  │  └───────────────────────────────────────────────────────────────────────────┘  │   │
│  │                                                                                 │   │
│  │  ┌──────────────────────────────────────────────────────────────────────────┐  │   │
│  │  │                         DATA SUBNET (10.0.3.0/24)                         │  │   │
│  │  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐     │  │   │
│  │  │  │   RDS PostgreSQL │  │  ElastiCache     │  │   ElastiCache    │     │  │   │
│  │  │  │   (Primary +     │  │   Redis         │  │   Redis          │     │  │   │
│  │  │  │    Replica)       │  │   (Session)     │  │   (Cache)        │     │  │   │
│  │  │  │                   │  │                  │  │                  │     │  │   │
│  │  │  │  Multi-AZ       │  │  Cluster Mode    │  │  Cluster Mode    │     │  │   │
│  │  │  └──────────────────┘  └──────────────────┘  └──────────────────┘     │  │   │
│  │  └───────────────────────────────────────────────────────────────────────────┘  │   │
│  └───────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                       │
└───────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 11. Monitoring & Observability

### 11.1 Metrics

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            METRICS TO TRACK                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  API Gateway                                                            │
│  ├── Request rate (req/sec)                                             │
│  ├── Latency (p50, p95, p99)                                           │
│  ├── Error rate (%)                                                     │
│  ├── Active connections                                                 │
│  └── JWT validation time                                               │
│                                                                          │
│  Auth Service                                                            │
│  ├── OTP send rate                                                     │
│  ├── OTP success/fail rate                                             │
│  ├── Token generation rate                                             │
│  └── Token validation rate                                             │
│                                                                          │
│  Credit Service                                                         │
│  ├── Balance check rate                                                │
│  ├── Deduction rate                                                    │
│  ├── Insufficient balance rate                                         │
│  └── Average balance                                                   │
│                                                                          │
│  Java Services                                                          │
│  ├── JVM memory usage                                                  │
│  ├── Thread pool utilization                                           │
│  ├── Database connection pool                                           │
│  └── Request latency                                                   │
│                                                                          │
│  AI Gateway                                                             │
│  ├── Request count by model                                            │
│  ├── Token usage by model                                              │
│  ├── Cost per request                                                  │
│  ├── Response time by model                                            │
│  └── Error rate by provider                                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 11.2 Logging Format

```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "level": "INFO",
  "service": "api-gateway",
  "trace_id": "abc123def456",
  "span_id": "xyz789",
  "user_id": "user-uuid",
  "method": "POST",
  "path": "/api/v1/ai/chat",
  "status_code": 200,
  "latency_ms": 1250,
  "message": "Request completed"
}
```

---

## 12. Security Considerations

### 12.1 mTLS Between Services

```yaml
# All services communicate over mTLS
# Certificates managed by AWS Certificate Manager
# Service mesh using AWS App Mesh or similar
```

### 12.2 Rate Limiting

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          RATE LIMITING TIERS                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Tier      │ Requests/Min │ AI Credits/Day │ Concurrent Connections   │
│  ──────────┼──────────────┼────────────────┼─────────────────────────  │
│  Basic     │     60       │     2,000      │           5              │
│  Developer │     180       │     5,000      │          10              │
│  Pro       │     360       │    10,000      │          20              │
│  Builder   │     720       │    18,000      │          50              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 13. Disaster Recovery

### 13.1 Backup Strategy

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           BACKUP STRATEGY                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  PostgreSQL (RDS)                                                       │
│  ├── Automated daily backups                                           │
│  ├── Point-in-time recovery (PITR)                                     │
│  ├── Retention: 30 days                                                │
│  └── Cross-region backup replication                                    │
│                                                                          │
│  Redis (ElastiCache)                                                   │
│  ├── Redis AOF persistence                                             │
│  ├── Automated snapshots every hour                                    │
│  └── Cross-region read replica                                          │
│                                                                          │
│  Configuration                                                          │
│  ├── Secrets stored in AWS Secrets Manager                             │
│  ├── Configuration in AWS Parameter Store                              │
│  └── Infrastructure as Code (Terraform)                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 14. Cost Estimation

### 14.1 Monthly Cost (AWS)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         MONTHLY COST ESTIMATION                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  COMPUTE                                                               │
│  ├── ECS Fargate (API Gateway) - 5 tasks × 1 vCPU × 0.04048/hr      │
│  │   = $291.46/month                                                    │
│  ├── ECS Fargate (Go Services) - 10 tasks × 0.5 vCPU × 0.04048/hr    │
│  │   = $145.73/month                                                    │
│  ├── ECS Fargate (Java Services) - 15 tasks × 1 vCPU × 0.04048/hr    │
│  │   = $437.18/month                                                    │
│  ├── ECS Fargate (AI Gateway) - 5 tasks × 2 vCPU × 0.04048/hr        │
│  │   = $291.46/month                                                    │
│                                                                          │
│  DATABASE                                                              │
│  ├── RDS PostgreSQL db.t3.medium (Multi-AZ)                           │
│  │   = $173.88/month                                                    │
│  ├── ElastiCache Redis cache.t3.medium (2 nodes)                      │
│  │   = $115.92/month                                                    │
│                                                                          │
│  NETWORK                                                               │
│  ├── ALB - $22.50/month + $0.008/LCU                                  │
│  ├── CloudFront - $0.02/GB out + $0.009/10k req                       │
│  └── Data transfer - varies                                            │
│                                                                          │
│  ESTIMATED TOTAL: ~$1,500-2,000/month                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```
