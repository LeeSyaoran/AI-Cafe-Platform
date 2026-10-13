# AI Café Platform - Go API Gateway Refactor Plan

## Mục lục
1. [Tổng quan](#1-tổng-quan)
2. [Refactor Security Issues](#2-refactor-security-issues)
3. [Refactor Layered Architecture](#3-refactor-layered-architecture)
4. [Response Format Standardization](#4-response-format-standardization)
5. [Service Layer Implementation](#5-service-layer-implementation)
6. [Database & Redis Setup](#6-database--redis-setup)
7. [Proto Files & gRPC Setup](#7-proto-files--grpc-setup)

---

## 1. Tổng quan

### Mục tiêu
Refactor Go API Gateway để:
- ✅ Sửa security issues (OTP, password hashing)
- ✅ Implement proper layered architecture (Handler → Service → Repository)
- ✅ Standardize response format
- ✅ Add proper service layer
- ✅ Setup Redis cho OTP
- ✅ Chuẩn bị cho gRPC migration

### File Structure Target
```
BackEnd/go/api-gateway/
├── cmd/server/main.go
├── internal/
│   ├── config/
│   │   ├── config.go           # Main config
│   │   └── jwt.go             # JWT config
│   ├── handler/
│   │   ├── auth_handler.go    # Auth endpoints
│   │   ├── product_handler.go # Product endpoints
│   │   ├── order_handler.go   # Order endpoints
│   │   ├── cart_handler.go    # Cart endpoints
│   │   └── common_handler.go  # Health, version
│   ├── service/               # ✅ BUSINESS LOGIC HERE
│   │   ├── auth_service.go    # Login, register, OTP
│   │   ├── product_service.go # Products, categories
│   │   ├── order_service.go  # Order operations
│   │   ├── cart_service.go   # Cart operations
│   │   └── credit_service.go # Credit management
│   ├── repository/
│   │   ├── postgres.go       # Database access
│   │   └── redis.go          # ✅ Redis for OTP
│   ├── middleware/
│   │   ├── auth.go          # JWT auth
│   │   ├── rate_limiter.go   # Rate limiting
│   │   └── tracing.go       # ✅ OpenTelemetry
│   └── dto/                  # ✅ REQUEST/RESPONSE DTOs
│       ├── auth.go
│       ├── product.go
│       ├── order.go
│       └── cart.go
├── pkg/
│   ├── response/             # Unified response
│   └── validator/           # Input validation
└── proto/                   # ✅ Proto files
    ├── auth.proto
    ├── credit.proto
    └── order.proto
```

---

## 2. Refactor Security Issues

### 2.1 Remove OTP Debug Exposure

**File:** `internal/handler/auth_handler.go`

**TRƯỚC:**
```go
// ❌ DANGER: Exposes OTP in production
return c.JSON(response.Success(fiber.Map{
    "message":              "OTP sent successfully",
    "expires_in":          300,
    "_debug_otp":         otp,  // REMOVE THIS
}))
```

**SAU:**
```go
// ✅ Secure: No OTP exposure
return c.JSON(response.Success(fiber.Map{
    "message":               "OTP sent successfully",
    "expires_in":           300,
    "resend_available_in":  60,
}))
```

### 2.2 Move OTP to Redis

**File:** `internal/repository/redis.go` (NEW)

```go
package repository

import (
    "context"
    "fmt"
    "time"

    "github.com/redis/go-redis/v9"
)

type RedisRepo struct {
    client *redis.Client
}

func NewRedisRepo(addr, password string, db int) *RedisRepo {
    return &RedisRepo{
        client: redis.NewClient(&redis.Options{
            Addr:     addr,
            Password: password,
            DB:       db,
        }),
    }
}

// OTP Operations
func (r *RedisRepo) SetOTP(ctx context.Context, phone, otp string, ttl time.Duration) error {
    key := fmt.Sprintf("otp:%s", phone)
    return r.client.Set(ctx, key, otp, ttl).Err()
}

func (r *RedisRepo) GetOTP(ctx context.Context, phone string) (string, error) {
    key := fmt.Sprintf("otp:%s", phone)
    return r.client.Get(ctx, key).Result()
}

func (r *RedisRepo) DeleteOTP(ctx context.Context, phone string) error {
    key := fmt.Sprintf("otp:%s", phone)
    return r.client.Del(ctx, key).Err()
}

func (r *RedisRepo) IncrementOTPAttempts(ctx context.Context, phone string) (int64, error) {
    key := fmt.Sprintf("otp_attempts:%s", phone)
    return r.client.Incr(ctx, key).Result()
}

func (r *RedisRepo) SetOTPAttemptLockout(ctx context.Context, phone string, ttl time.Duration) error {
    key := fmt.Sprintf("otp_lockout:%s", phone)
    return r.client.Set(ctx, key, "1", ttl).Err()
}

func (r *RedisRepo) IsOTPAttemptLocked(ctx context.Context, phone string) (bool, error) {
    key := fmt.Sprintf("otp_lockout:%s", phone)
    exists, err := r.client.Exists(ctx, key).Result()
    return exists > 0, err
}

// Token Blacklist (for logout)
func (r *RedisRepo) AddTokenToBlacklist(ctx context.Context, tokenID string, ttl time.Duration) error {
    key := fmt.Sprintf("blacklist:%s", tokenID)
    return r.client.Set(ctx, key, "1", ttl).Err()
}

func (r *RedisRepo) IsTokenBlacklisted(ctx context.Context, tokenID string) (bool, error) {
    key := fmt.Sprintf("blacklist:%s", tokenID)
    exists, err := r.client.Exists(ctx, key).Result()
    return exists > 0, err
}
```

### 2.3 Fix Password Hashing (SHA256 → bcrypt)

**File:** `internal/service/auth_service.go`

**TRƯỚC:**
```go
// ❌ WEAK: SHA256 is not suitable for password hashing
import "crypto/sha256"

hash := sha256.Sum256([]byte(password))
passwordHash := hex.EncodeToString(hash[:])
```

**SAU:**
```go
// ✅ SECURE: Use bcrypt
import "golang.org/x/crypto/bcrypt"

func hashPassword(password string) (string, error) {
    bytes, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    return string(bytes), err
}

func verifyPassword(password, hash string) bool {
    err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
    return err == nil
}
```

### 2.4 Fix OTP Randomness

**TRƯỚC:**
```go
// ❌ PREDICTABLE: Using time-based seed
rand.Seed(time.Now().UnixNano())
return fmt.Sprintf("%06d", rand.Intn(1000000))
```

**SAU:**
```go
// ✅ SECURE: Use crypto/rand
import "crypto/rand"
import "math/big"

func generateOTP() string {
    const digits = "0123456789"
    otp := make([]byte, 6)
    for i := range otp {
        num, _ := rand.Int(rand.Reader, big.NewInt(10))
        otp[i] = digits[num.Int64()]
    }
    return string(otp)
}
```

---

## 3. Refactor Layered Architecture

### 3.1 DTOs (Data Transfer Objects)

**File:** `internal/dto/auth.go` (NEW)

```go
package dto

import "time"

// ============ REQUEST DTOs ============

type SendOTPRequest struct {
    Phone string `json:"phone" validate:"required,e164"`
}

type VerifyOTPRequest struct {
    Phone string `json:"phone" validate:"required,e164"`
    Code  string `json:"code" validate:"required,len=6"`
}

type RefreshTokenRequest struct {
    RefreshToken string `json:"refresh_token" validate:"required"`
}

type RegisterRequest struct {
    Email    string `json:"email" validate:"required,email"`
    Password string `json:"password" validate:"required,min=8"`
    Name     string `json:"name" validate:"required,min=2"`
}

type LoginRequest struct {
    Email    string `json:"email" validate:"required,email"`
    Password string `json:"password" validate:"required"`
}

// ============ RESPONSE DTOs ============

type AuthResponse struct {
    Tokens *TokensResponse `json:"tokens"`
    User   *UserResponse   `json:"user"`
}

type TokensResponse struct {
    AccessToken  string `json:"access_token"`
    RefreshToken string `json:"refresh_token"`
    ExpiresIn   int64  `json:"expires_in"`
    TokenType    string `json:"token_type"`
}

type UserResponse struct {
    ID        string `json:"id"`
    Email     string `json:"email"`
    Phone     string `json:"phone,omitempty"`
    Name      string `json:"name"`
    Role      string `json:"role"`
    Tier      string `json:"tier,omitempty"`
    CreatedAt time.Time `json:"createdAt,omitempty"`
}

type OTPSentResponse struct {
    Message           string `json:"message"`
    ExpiresIn        int    `json:"expires_in"`
    ResendAvailableIn int    `json:"resend_available_in"`
}

type ErrorResponse struct {
    Code    string      `json:"code"`
    Message string      `json:"message"`
    Details interface{} `json:"details,omitempty"`
}
```

### 3.2 Update Handler - Move Business Logic to Service

**File:** `internal/handler/auth_handler.go`

**TRƯỚC (Business logic in handler):**
```go
// ❌ VIOLATION: Handler contains business logic
func (h *AuthHandler) VerifyOTP(c *fiber.Ctx) error {
    // Find or create user - BUSINESS LOGIC
    user, err := h.repo.FindOrCreateUserByPhone(c.Context(), req.Phone)
    if err != nil {
        return c.Status(500).JSON(response.Error("USER_ERROR", "..."))
    }
    
    // Generate JWT - BUSINESS LOGIC  
    claims := config.JWTClaims{...}
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, &claims)
    ...
}
```

**SAU (Handler delegates to Service):**
```go
// ✅ CLEAN: Handler only handles HTTP
type AuthHandler struct {
    authService  *service.AuthService
    creditService *service.CreditService  // ✅ Add credit service
}

func NewAuthHandler(
    authService *service.AuthService,
    creditService *service.CreditService,
) *AuthHandler {
    return &AuthHandler{
        authService:   authService,
        creditService: creditService,
    }
}

func (h *AuthHandler) VerifyOTP(c *fiber.Ctx) error {
    var req dto.VerifyOTPRequest
    if err := c.BodyParser(&req); err != nil {
        return c.Status(400).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
    }
    
    // Validate
    if err := validator.Validate.Struct(req); err != nil {
        return c.Status(400).JSON(response.Error("VALIDATION_ERROR", err.Error()))
    }
    
    // ✅ Delegate to service
    result, err := h.authService.VerifyOTP(c.Context(), req.Phone, req.Code)
    if err != nil {
        return handleServiceError(c, err)
    }
    
    return c.JSON(response.Success(result))
}
```

### 3.3 Update Service Layer

**File:** `internal/service/auth_service.go`

```go
package service

import (
    "context"
    "errors"
    "time"

    "github.com/google/uuid"
    "aicafe-api-gateway/internal/config"
    "aicafe-api-gateway/internal/dto"
    "aicafe-api-gateway/internal/repository"
    "golang.org/x/crypto/bcrypt"
)

var (
    ErrOTPNotFound      = errors.New("otp not found")
    ErrOTPExpired       = errors.New("otp expired")
    ErrOTPInvalid       = errors.New("invalid otp code")
    ErrOTPAttemptsExceeded = errors.New("too many attempts")
    ErrUserExists       = errors.New("user already exists")
)

type AuthService struct {
    repo          *repository.PostgresRepo
    redis         *repository.RedisRepo
    jwtConfig     config.JWTConfig
    otpExpiry     time.Duration
    maxOTPAttempts int
}

func NewAuthService(
    repo *repository.PostgresRepo,
    redis *repository.RedisRepo,
    jwtConfig config.JWTConfig,
) *AuthService {
    return &AuthService{
        repo:           repo,
        redis:          redis,
        jwtConfig:       jwtConfig,
        otpExpiry:       5 * time.Minute,
        maxOTPAttempts:  3,
    }
}

// ============ OTP Operations ============

func (s *AuthService) SendOTP(ctx context.Context, phone string) error {
    // Check rate limit
    locked, _ := s.redis.IsOTPAttemptLocked(ctx, phone)
    if locked {
        return ErrOTPAttemptsExceeded
    }

    // Generate secure OTP
    otp := generateSecureOTP()
    
    // Store in Redis with expiry
    if err := s.redis.SetOTP(ctx, phone, otp, s.otpExpiry); err != nil {
        return err
    }
    
    // TODO: Send SMS via external service
    // smsService.Send(phone, otp)
    
    return nil
}

func (s *AuthService) VerifyOTP(ctx context.Context, phone, code string) (*dto.AuthResponse, error) {
    // Get stored OTP from Redis
    storedOTP, err := s.redis.GetOTP(ctx, phone)
    if err != nil {
        return nil, ErrOTPNotFound
    }
    
    // Check attempts
    attempts, _ := s.redis.IncrementOTPAttempts(ctx, phone)
    if attempts > int64(s.maxOTPAttempts) {
        s.redis.SetOTPAttemptLockout(ctx, phone, 15*time.Minute)
        s.redis.DeleteOTP(ctx, phone)
        return nil, ErrOTPAttemptsExceeded
    }
    
    // Verify code
    if storedOTP != code {
        return nil, ErrOTPInvalid
    }
    
    // Delete OTP after successful verification
    s.redis.DeleteOTP(ctx, phone)
    
    // Find or create user
    user, err := s.repo.FindOrCreateUserByPhone(ctx, phone)
    if err != nil {
        return nil, err
    }
    
    // Generate tokens
    accessToken, err := s.generateAccessToken(user)
    if err != nil {
        return nil, err
    }
    
    refreshToken, err := s.generateRefreshToken(user)
    if err != nil {
        return nil, err
    }
    
    return &dto.AuthResponse{
        Tokens: &dto.TokensResponse{
            AccessToken:  accessToken,
            RefreshToken: refreshToken,
            ExpiresIn:    s.jwtConfig.AccessTokenExpiration,
            TokenType:    "Bearer",
        },
        User: &dto.UserResponse{
            ID:    user.ID,
            Email: user.Email,
            Phone: user.Phone,
            Name:  user.Name,
            Role:  user.Role,
            Tier:  user.Tier,
        },
    }, nil
}

// ============ Token Operations ============

func (s *AuthService) RefreshToken(ctx context.Context, refreshToken string) (*dto.TokensResponse, error) {
    // Parse and validate refresh token
    claims, err := s.ValidateToken(refreshToken)
    if err != nil {
        return nil, err
    }
    
    // Check if token is blacklisted
    blacklisted, _ := s.redis.IsTokenBlacklisted(ctx, claims.JTI)
    if blacklisted {
        return nil, errors.New("token has been revoked")
    }
    
    // Get user
    user, err := s.repo.GetUserByIDString(ctx, claims.UserID)
    if err != nil {
        return nil, err
    }
    
    // Generate new access token
    accessToken, err := s.generateAccessToken(user)
    if err != nil {
        return nil, err
    }
    
    return &dto.TokensResponse{
        AccessToken: accessToken,
        ExpiresIn:   s.jwtConfig.AccessTokenExpiration,
        TokenType:   "Bearer",
    }, nil
}

func (s *AuthService) Logout(ctx context.Context, token string) error {
    // Parse token to get JTI
    claims, err := s.ValidateToken(token)
    if err != nil {
        return err
    }
    
    // Add to blacklist with remaining TTL
    if claims.Exp > 0 {
        remaining := time.Until(time.Unix(claims.Exp, 0))
        return s.redis.AddTokenToBlacklist(ctx, claims.JTI, remaining)
    }
    
    return nil
}

// ============ User Operations ============

func (s *AuthService) Register(ctx context.Context, req *dto.RegisterRequest) error {
    // Check if user exists
    existing, err := s.repo.GetUserByEmail(ctx, req.Email)
    if err != nil {
        return err
    }
    if existing != nil {
        return ErrUserExists
    }
    
    // Hash password
    passwordHash, err := hashPassword(req.Password)
    if err != nil {
        return err
    }
    
    // Create user
    user := &model.User{
        ID:           uuid.New(),
        Email:        req.Email,
        PasswordHash: passwordHash,
        Name:         req.Name,
        Role:         "customer",
        Status:       "active",
        Tier:         "free",
        CreatedAt:    time.Now(),
        UpdatedAt:    time.Now(),
    }
    
    return s.repo.CreateUser(ctx, user)
}

func (s *AuthService) Login(ctx context.Context, req *dto.LoginRequest) (*dto.AuthResponse, error) {
    user, err := s.repo.GetUserByEmail(ctx, req.Email)
    if err != nil {
        return nil, err
    }
    if user == nil {
        return nil, ErrInvalidCredentials
    }
    
    // Verify password
    if !verifyPassword(req.Password, user.PasswordHash) {
        return nil, ErrInvalidCredentials
    }
    
    // Generate tokens
    accessToken, _ := s.generateAccessToken(user)
    refreshToken, _ := s.generateRefreshToken(user)
    
    return &dto.AuthResponse{
        Tokens: &dto.TokensResponse{
            AccessToken:  accessToken,
            RefreshToken: refreshToken,
            ExpiresIn:    s.jwtConfig.AccessTokenExpiration,
            TokenType:    "Bearer",
        },
        User: &dto.UserResponse{
            ID:    user.ID,
            Email: user.Email,
            Phone: user.Phone,
            Name:  user.Name,
            Role:  user.Role,
            Tier:  user.Tier,
        },
    }, nil
}

// ============ Private Methods ============

func generateSecureOTP() string {
    const digits = "0123456789"
    otp := make([]byte, 6)
    for i := range otp {
        num, _ := rand.Int(rand.Reader, big.NewInt(10))
        otp[i] = digits[num.Int64()]
    }
    return string(otp)
}

func hashPassword(password string) (string, error) {
    bytes, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    return string(bytes), err
}

func verifyPassword(password, hash string) bool {
    err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
    return err == nil
}
```

---

## 4. Response Format Standardization

### 4.1 Update Response Package

**File:** `pkg/response/response.go`

```go
package response

import "net/http"

// ============ Unified API Response ============

type APIResponse struct {
    Success bool        `json:"success"`
    Data    interface{} `json:"data,omitempty"`
    Error   *APIError  `json:"error,omitempty"`
    Meta    *Meta      `json:"meta,omitempty"`
}

type APIError struct {
    Code       string      `json:"code"`
    Message    string      `json:"message"`
    Details    interface{} `json:"details,omitempty"`
    TraceID    string      `json:"traceId,omitempty"`
}

type Meta struct {
    Page       int `json:"page,omitempty"`
    Limit      int `json:"limit,omitempty"`
    Total      int `json:"total,omitempty"`
    TotalPages int `json:"totalPages,omitempty"`
}

// ============ Factory Methods ============

func Success(data interface{}) APIResponse {
    return APIResponse{
        Success: true,
        Data:    data,
    }
}

func SuccessWithMeta(data interface{}, meta *Meta) APIResponse {
    return APIResponse{
        Success: true,
        Data:    data,
        Meta:    meta,
    }
}

func Error(code, message string) *APIError {
    return &APIError{
        Code:    code,
        Message: message,
    }
}

func ErrorWithDetails(code, message string, details interface{}) *APIError {
    return &APIError{
        Code:    code,
        Message: message,
        Details: details,
    }
}

func ErrorWithTrace(code, message, traceID string) *APIError {
    return &APIError{
        Code:    code,
        Message: message,
        TraceID: traceID,
    }
}

// ============ HTTP Status Mapping ============

func ErrorToStatus(err error) int {
    switch err {
    case ErrOTPNotFound, ErrOTPExpired:
        return http.StatusBadRequest
    case ErrOTPInvalid:
        return http.StatusBadRequest
    case ErrOTPAttemptsExceeded:
        return http.StatusTooManyRequests
    case ErrUserExists:
        return http.StatusConflict
    case ErrInvalidCredentials:
        return http.StatusUnauthorized
    default:
        return http.StatusInternalServerError
    }
}

// ============ Service Error Types ============

type ServiceError struct {
    Code    string
    Message string
    Err     error
}

func (e *ServiceError) Error() string {
    if e.Err != nil {
        return e.Err.Error()
    }
    return e.Message
}

func NewServiceError(code, message string, err error) *ServiceError {
    return &ServiceError{
        Code:    code,
        Message: message,
        Err:     err,
    }
}
```

### 4.2 Update All Handlers

**Before (Inconsistent):**
```go
// product_handler.go - Direct fiber.Map
return c.Status(200).JSON(fiber.Map{
    "products": products,
    "total":    total,
})

// order_handler.go - Mixed format
return c.Status(201).JSON(response.Success(fiber.Map{
    "orderId": order.ID,
    "status":  order.Status,
}))
```

**After (Consistent):**
```go
// product_handler.go - Unified format
return c.JSON(response.SuccessWithMeta(fiber.Map{
    "products": products,
}, &response.Meta{
    Limit:  limit,
    Total:  total,
    Page:   page,
    TotalPages: (total + limit - 1) / limit,
}))

// order_handler.go - Same format
return c.Status(201).JSON(response.Success(fiber.Map{
    "orderId":     order.ID,
    "orderNumber": order.OrderNumber,
    "status":      order.Status,
}))
```

---

## 5. Service Layer Implementation

### 5.1 Order Service

**File:** `internal/service/order_service.go` (NEW)

```go
package service

import (
    "context"
    "errors"
    "fmt"
    "time"

    "github.com/google/uuid"
    "aicafe-api-gateway/internal/dto"
    "aicafe-api-gateway/internal/model"
    "aicafe-api-gateway/internal/repository"
)

var (
    ErrOrderNotFound      = errors.New("order not found")
    ErrOrderCannotCancel  = errors.New("order cannot be cancelled")
    ErrInsufficientCredit = errors.New("insufficient credit")
)

type OrderService struct {
    repo           *repository.PostgresRepo
    creditService  *CreditService  // ✅ Dependency
    promotionSvc   *PromotionService
}

func NewOrderService(
    repo *repository.PostgresRepo,
    creditService *CreditService,
) *OrderService {
    return &OrderService{
        repo:          repo,
        creditService: creditService,
    }
}

func (s *OrderService) CreateOrder(ctx context.Context, userID uuid.UUID, req *dto.CreateOrderRequest) (*dto.OrderResponse, error) {
    // 1. Validate cafe exists
    cafe, err := s.repo.GetCafeByID(ctx, req.CafeID)
    if err != nil || cafe == nil {
        return nil, errors.New("cafe not found")
    }
    
    // 2. Get cart with items
    cart, err := s.repo.GetCart(ctx, userID, req.CafeID)
    if err != nil || cart == nil || len(cart.Items) == 0 {
        return nil, errors.New("cart is empty")
    }
    
    // 3. Calculate totals
    subtotal := s.calculateSubtotal(cart.Items)
    discountAmount := 0.0
    
    // 4. Apply promotion if provided
    if req.PromoCode != "" {
        discountAmount, err = s.promotionSvc.ApplyPromotion(ctx, req.PromoCode, userID, subtotal)
        if err != nil {
            return nil, err
        }
    }
    
    // 5. Calculate tax
    taxAmount := (subtotal - discountAmount) * cafe.TaxRate
    totalAmount := subtotal - discountAmount + taxAmount
    
    // 6. Check credit balance for credit payment
    if req.PaymentMethod == "credit" {
        balance, _ := s.creditService.GetBalance(ctx, userID)
        if balance < totalAmount {
            return nil, ErrInsufficientCredit
        }
    }
    
    // 7. Create order
    order := &model.Order{
        ID:             uuid.New(),
        OrderNumber:    s.generateOrderNumber(),
        UserID:         userID,
        CompanyID:      cafe.CompanyID,
        CafeID:         req.CafeID,
        OrderType:      req.OrderType,
        Status:         "pending",
        Subtotal:       subtotal,
        DiscountAmount: discountAmount,
        TaxAmount:      taxAmount,
        TotalAmount:    totalAmount,
        PaymentStatus:  "pending",
        PaymentMethod:  req.PaymentMethod,
        CustomerNote:   req.CustomerNote,
        CreatedAt:      time.Now(),
        UpdatedAt:      time.Now(),
    }
    
    // 8. Create order items
    for _, item := range cart.Items {
        orderItem := &model.OrderItem{
            ID:          uuid.New(),
            OrderID:     order.ID,
            ProductID:   item.ProductID,
            VariantID:   item.VariantID,
            ProductName: item.ProductName,
            Quantity:    item.Quantity,
            UnitPrice:   item.UnitPrice,
            LineTotal:   item.LineTotal,
            OptionsJSON: item.OptionsJSON,
            Notes:       item.Notes,
            ItemStatus:  "pending",
        }
        order.Items = append(order.Items, orderItem)
    }
    
    // 9. Save order
    if err := s.repo.CreateOrder(ctx, order); err != nil {
        return nil, err
    }
    
    // 10. Clear cart
    s.repo.DeleteCart(ctx, cart.ID)
    
    // 11. Deduct credit if using credit payment
    if req.PaymentMethod == "credit" {
        s.creditService.Deduct(ctx, userID, totalAmount)
    }
    
    return s.toOrderResponse(order), nil
}

func (s *OrderService) GetOrder(ctx context.Context, orderID uuid.UUID, userID uuid.UUID) (*dto.OrderResponse, error) {
    order, err := s.repo.GetOrderByID(ctx, orderID)
    if err != nil || order == nil {
        return nil, ErrOrderNotFound
    }
    
    // Authorization check
    if order.UserID != userID {
        return nil, ErrOrderNotFound  // Don't reveal order exists
    }
    
    return s.toOrderResponse(order), nil
}

func (s *OrderService) ListOrders(ctx context.Context, userID uuid.UUID, page, limit int) (*dto.OrderListResponse, error) {
    if page < 1 {
        page = 1
    }
    if limit < 1 || limit > 100 {
        limit = 20
    }
    offset := (page - 1) * limit
    
    orders, total, err := s.repo.GetOrdersByUser(ctx, userID, limit, offset)
    if err != nil {
        return nil, err
    }
    
    items := make([]*dto.OrderResponse, len(orders))
    for i, order := range orders {
        items[i] = s.toOrderResponse(order)
    }
    
    return &dto.OrderListResponse{
        Orders: items,
        Meta: &dto.PaginationMeta{
            Page:       page,
            Limit:      limit,
            Total:      total,
            TotalPages: (total + limit - 1) / limit,
        },
    }, nil
}

func (s *OrderService) CancelOrder(ctx context.Context, orderID uuid.UUID, userID uuid.UUID, reason string) (*dto.OrderResponse, error) {
    order, err := s.repo.GetOrderByID(ctx, orderID)
    if err != nil || order == nil {
        return nil, ErrOrderNotFound
    }
    
    if order.UserID != userID {
        return nil, ErrOrderNotFound
    }
    
    // Business rule: Can only cancel pending/confirmed orders
    if order.Status != "pending" && order.Status != "confirmed" {
        return nil, ErrOrderCannotCancel
    }
    
    // Refund credit if paid with credit
    if order.PaymentMethod == "credit" && order.PaymentStatus == "paid" {
        s.creditService.AddCredit(ctx, userID, order.TotalAmount, "refund")
    }
    
    // Update status
    order.Status = "cancelled"
    order.CancelledAt = time.Now()
    order.CancellationReason = reason
    
    if err := s.repo.UpdateOrder(ctx, order); err != nil {
        return nil, err
    }
    
    return s.toOrderResponse(order), nil
}

// ============ Private Methods ============

func (s *OrderService) calculateSubtotal(items []*model.CartItem) float64 {
    var total float64
    for _, item := range items {
        total += item.LineTotal
    }
    return total
}

func (s *OrderService) generateOrderNumber() string {
    return fmt.Sprintf("AIC%s%d", time.Now().Format("20060102150405"), time.Now().Nanosecond()%1000)
}

func (s *OrderService) toOrderResponse(order *model.Order) *dto.OrderResponse {
    items := make([]*dto.OrderItemResponse, len(order.Items))
    for i, item := range order.Items {
        items[i] = &dto.OrderItemResponse{
            ID:          item.ID,
            ProductID:   item.ProductID,
            ProductName: item.ProductName,
            Quantity:    item.Quantity,
            UnitPrice:   item.UnitPrice,
            LineTotal:   item.LineTotal,
        }
    }
    
    return &dto.OrderResponse{
        ID:             order.ID,
        OrderNumber:    order.OrderNumber,
        Status:         order.Status,
        OrderType:      order.OrderType,
        Subtotal:       order.Subtotal,
        DiscountAmount: order.DiscountAmount,
        TaxAmount:      order.TaxAmount,
        TotalAmount:    order.TotalAmount,
        PaymentStatus:  order.PaymentStatus,
        Items:          items,
        CreatedAt:      order.CreatedAt,
    }
}
```

### 5.2 Credit Service

**File:** `internal/service/credit_service.go`

```go
package service

import (
    "context"
    "time"

    "github.com/google/uuid"
    "aicafe-api-gateway/internal/repository"
)

type CreditService struct {
    repo *repository.PostgresRepo
}

func NewCreditService(repo *repository.PostgresRepo) *CreditService {
    return &CreditService{repo: repo}
}

func (s *CreditService) GetBalance(ctx context.Context, userID uuid.UUID) (float64, error) {
    credit, err := s.repo.GetUserCredit(ctx, userID)
    if err != nil {
        return 0, err
    }
    if credit == nil {
        // Return default balance for new users
        return 1000.0, nil
    }
    return credit.Balance, nil
}

func (s *CreditService) Deduct(ctx context.Context, userID uuid.UUID, amount float64) error {
    credit, err := s.repo.GetUserCredit(ctx, userID)
    if err != nil {
        return err
    }
    
    if credit == nil {
        return ErrInsufficientCredit
    }
    
    if credit.Balance < amount {
        return ErrInsufficientCredit
    }
    
    credit.Balance -= amount
    credit.UpdatedAt = time.Now()
    
    // Record transaction
    tx := &model.CreditTransaction{
        ID:        uuid.New(),
        UserID:    userID,
        Amount:    -amount,
        Type:      "deduct",
        Balance:   credit.Balance,
        CreatedAt: time.Now(),
    }
    
    return s.repo.UpdateCreditAndLog(ctx, credit, tx)
}

func (s *CreditService) AddCredit(ctx context.Context, userID uuid.UUID, amount float64, reason string) error {
    credit, err := s.repo.GetUserCredit(ctx, userID)
    if err != nil {
        return err
    }
    
    if credit == nil {
        credit = &model.UserCredit{
            ID:        uuid.New(),
            UserID:    userID,
            Balance:   amount,
            CreatedAt: time.Now(),
        }
    } else {
        credit.Balance += amount
        credit.UpdatedAt = time.Now()
    }
    
    tx := &model.CreditTransaction{
        ID:        uuid.New(),
        UserID:    userID,
        Amount:    amount,
        Type:      reason,
        Balance:   credit.Balance,
        CreatedAt: time.Now(),
    }
    
    return s.repo.UpdateCreditAndLog(ctx, credit, tx)
}
```

---

## 6. Database & Redis Setup

### 6.1 Config Update

**File:** `internal/config/config.go`

```go
package config

import (
    "os"
    "strconv"
    "time"
)

type Config struct {
    Port        string
    Environment string
    
    // Database
    Database DatabaseConfig
    
    // Redis
    Redis RedisConfig
    
    // JWT
    JWT JWTConfig
    
    // CORS
    CORS CORSConfig
    
    // Rate Limiting
    RateLimit RateLimitConfig
    
    // External Services
    SMS SMSConfig
}

type DatabaseConfig struct {
    Host     string
    Port     int
    User     string
    Password string
    Name     string
    SSLMode  string
}

func (d *DatabaseConfig) DSN() string {
    return "postgres://" + d.User + ":" + d.Password + "@" + d.Host + ":" +
        strconv.Itoa(d.Port) + "/" + d.Name + "?sslmode=" + d.SSLMode
}

type RedisConfig struct {
    Host     string
    Port     int
    Password string
    DB       int
}

func (r *RedisConfig) Addr() string {
    return r.Host + ":" + strconv.Itoa(r.Port)
}

type JWTConfig struct {
    Secret                string
    AccessTokenExpiration int64  // seconds
    RefreshTokenExpiration int64 // seconds
    Issuer               string
}

func (c *JWTConfig) GetSigningKey() []byte {
    return []byte(c.Secret)
}

func (c *JWTConfig) AccessTokenDuration() time.Duration {
    return time.Duration(c.AccessTokenExpiration) * time.Second
}

func (c *JWTConfig) RefreshTokenDuration() time.Duration {
    return time.Duration(c.RefreshTokenExpiration) * time.Second
}

type CORSConfig struct {
    AllowedOrigins []string
    AllowedMethods []string
    AllowedHeaders []string
    AllowCredentials bool
    MaxAge         int
}

type RateLimitConfig struct {
    Enabled  bool
    Capacity int
    Duration time.Duration
}

type SMSConfig struct {
    Provider   string
    APIKey     string
    APISecret  string
    FromNumber string
}

func Load() *Config {
    return &Config{
        Port:        getEnv("PORT", "3000"),
        Environment: getEnv("ENVIRONMENT", "development"),
        
        Database: DatabaseConfig{
            Host:     getEnv("DB_HOST", "localhost"),
            Port:     getEnvInt("DB_PORT", 5432),
            User:     getEnv("DB_USER", "aicafe"),
            Password: getEnv("DB_PASSWORD", "aicafe"),
            Name:     getEnv("DB_NAME", "aicafe"),
            SSLMode:  getEnv("DB_SSLMODE", "disable"),
        },
        
        Redis: RedisConfig{
            Host:     getEnv("REDIS_HOST", "localhost"),
            Port:     getEnvInt("REDIS_PORT", 6379),
            Password: getEnv("REDIS_PASSWORD", ""),
            DB:       getEnvInt("REDIS_DB", 0),
        },
        
        JWT: JWTConfig{
            Secret:                 getEnv("JWT_SECRET", "change-me-in-production"),
            AccessTokenExpiration:  getEnvInt("JWT_ACCESS_EXPIRATION", 3600),
            RefreshTokenExpiration: getEnvInt("JWT_REFRESH_EXPIRATION", 604800),
            Issuer:                 getEnv("JWT_ISSUER", "aicafe-api"),
        },
        
        CORS: CORSConfig{
            AllowedOrigins: []string{
                getEnv("CORS_ORIGIN", "http://localhost:3001"),
            },
            AllowedMethods: []string{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"},
            AllowedHeaders: []string{"Origin", "Content-Type", "Accept", "Authorization"},
            MaxAge:         86400,
        },
        
        RateLimit: RateLimitConfig{
            Enabled:  getEnvBool("RATE_LIMIT_ENABLED", true),
            Capacity: getEnvInt("RATE_LIMIT_CAPACITY", 100),
            Duration: 1 * time.Minute,
        },
        
        SMS: SMSConfig{
            Provider:   getEnv("SMS_PROVIDER", "twilio"),
            APIKey:     getEnv("SMS_API_KEY", ""),
            APISecret:  getEnv("SMS_API_SECRET", ""),
            FromNumber: getEnv("SMS_FROM_NUMBER", ""),
        },
    }
}

func getEnv(key, defaultValue string) string {
    if value := os.Getenv(key); value != "" {
        return value
    }
    return defaultValue
}

func getEnvInt(key string, defaultValue int) int {
    if value := os.Getenv(key); value != "" {
        if intValue, err := strconv.Atoi(value); err == nil {
            return intValue
        }
    }
    return defaultValue
}

func getEnvBool(key string, defaultValue bool) bool {
    if value := os.Getenv(key); value != "" {
        return value == "true" || value == "1"
    }
    return defaultValue
}
```

### 6.2 Main.go Update

**File:** `cmd/server/main.go`

```go
package main

import (
    "context"
    "fmt"
    "log"
    "os"
    "os/signal"
    "syscall"
    "time"

    "github.com/gofiber/fiber/v2"
    "github.com/gofiber/fiber/v2/middleware/cors"
    "github.com/gofiber/fiber/v2/middleware/logger"
    "github.com/gofiber/fiber/v2/middleware/recover"
    "github.com/gofiber/fiber/v2/middleware/requestid"
    "github.com/gofiber/adaptor/v2"
    
    "aicafe-api-gateway/internal/config"
    "aicafe-api-gateway/internal/handler"
    "aicafe-api-gateway/internal/middleware"
    "aicafe-api-gateway/internal/repository"
    "aicafe-api-gateway/internal/service"
    "aicafe-api-gateway/pkg/response"
)

func main() {
    // Load configuration
    cfg := config.Load()
    
    // Initialize repositories
    postgresRepo := repository.NewPostgresRepo(cfg.Database.DSN())
    if err := postgresRepo.Ping(); err != nil {
        log.Fatalf("Failed to connect to database: %v", err)
    }
    
    redisRepo := repository.NewRedisRepo(
        cfg.Redis.Addr(),
        cfg.Redis.Password,
        cfg.Redis.DB,
    )
    
    // Initialize services
    authService := service.NewAuthService(postgresRepo, redisRepo, cfg.JWT)
    creditService := service.NewCreditService(postgresRepo)
    productService := service.NewProductService(postgresRepo)
    orderService := service.NewOrderService(postgresRepo, creditService)
    cartService := service.NewCartService(postgresRepo)
    
    // Initialize handlers
    authHandler := handler.NewAuthHandler(authService)
    productHandler := handler.NewProductHandler(productService)
    orderHandler := handler.NewOrderHandler(orderService)
    cartHandler := handler.NewCartHandler(cartService)
    
    // Initialize middleware
    authMiddleware := middleware.NewAuthMiddleware(authService, redisRepo, cfg.JWT)
    rateLimiter := middleware.NewRateLimiter(cfg.RateLimit)
    
    // Create Fiber app
    app := fiber.New(fiber.Config{
        ErrorHandler: handler.ErrorHandler,
        ReadTimeout:  30 * time.Second,
        WriteTimeout: 30 * time.Second,
    })
    
    // Global middleware
    app.Use(recover.New())
    app.Use(requestid.New())
    app.Use(logger.New(logger.Config{
        Format: "[${time}] ${status} ${latency} ${method} ${path}\n",
    }))
    app.Use(cors.New(cors.Config{
        AllowOrigins:     cfg.CORS.AllowedOrigins,
        AllowMethods:     cfg.CORS.AllowedMethods,
        AllowHeaders:     cfg.CORS.AllowedHeaders,
        AllowCredentials: cfg.CORS.AllowCredentials,
        MaxAge:           cfg.CORS.MaxAge,
    }))
    
    // Health check
    app.Get("/health", handler.HealthCheck)
    app.Get("/v1", handler.APIVersion)
    
    // API v1 routes
    v1 := app.Group("/v1")
    
    // Public routes (no auth required)
    v1.Post("/auth/send-otp", authHandler.SendOTP)
    v1.Post("/auth/verify-otp", authHandler.VerifyOTP)
    v1.Post("/auth/register", authHandler.Register)
    v1.Post("/auth/login", authHandler.Login)
    v1.Post("/auth/refresh", authHandler.RefreshToken)
    
    // Public product routes
    v1.Get("/products", productHandler.ListProducts)
    v1.Get("/products/:id", productHandler.GetProduct)
    v1.Get("/categories", productHandler.GetCategories)
    
    // Protected routes (auth required)
    protected := v1.Group("", authMiddleware.JWTAuth())
    
    // Auth
    protected.Post("/auth/logout", authHandler.Logout)
    protected.Get("/me", authHandler.GetProfile)
    protected.Put("/me", authHandler.UpdateProfile)
    
    // Cart
    protected.Get("/cart", cartHandler.GetCart)
    protected.Post("/cart/items", cartHandler.AddItem)
    protected.Put("/cart/items/:id", cartHandler.UpdateItem)
    protected.Delete("/cart/items/:id", cartHandler.RemoveItem)
    protected.Delete("/cart", cartHandler.ClearCart)
    protected.Post("/cart/checkout", cartHandler.Checkout)
    
    // Orders
    protected.Get("/orders", orderHandler.ListOrders)
    protected.Post("/orders", orderHandler.CreateOrder)
    protected.Get("/orders/:id", orderHandler.GetOrder)
    protected.Post("/orders/:id/cancel", orderHandler.CancelOrder)
    
    // Credits
    protected.Get("/credits/balance", creditHandler.GetBalance)
    protected.Get("/credits/transactions", creditHandler.GetTransactions)
    
    // Start server
    go func() {
        addr := fmt.Sprintf(":%s", cfg.Port)
        log.Printf("Starting server on %s", addr)
        if err := app.Listen(addr); err != nil {
            log.Fatalf("Failed to start server: %v", err)
        }
    }()
    
    // Graceful shutdown
    quit := make(chan os.Signal, 1)
    signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
    <-quit
    
    log.Println("Shutting down server...")
    
    ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()
    
    if err := app.ShutdownWithContext(ctx); err != nil {
        log.Fatalf("Server forced to shutdown: %v", err)
    }
    
    // Close connections
    postgresRepo.Close()
    redisRepo.Close()
    
    log.Println("Server exited")
}
```

---

## 7. Proto Files & gRPC Setup

### 7.1 Auth Proto

**File:** `proto/auth.proto`

```protobuf
syntax = "proto3";

package aicafe.auth;

option go_package = "github.com/aicafe-api-gateway/pkg/grpc/auth";

service AuthService {
    rpc ValidateToken(ValidateTokenRequest) returns (ValidateTokenResponse);
    rpc GenerateToken(GenerateTokenRequest) returns (GenerateTokenResponse);
    rpc RefreshToken(RefreshTokenRequest) returns (RefreshTokenResponse);
    rpc RevokeToken(RevokeTokenRequest) returns (RevokeTokenResponse);
}

message ValidateTokenRequest {
    string token = 1;
}

message ValidateTokenResponse {
    bool valid = 1;
    string user_id = 2;
    string email = 3;
    string role = 4;
    string tier = 5;
    int64 expires_at = 6;
}

message GenerateTokenRequest {
    string user_id = 1;
    string email = 2;
    string role = 3;
    string tier = 4;
}

message GenerateTokenResponse {
    string access_token = 1;
    string refresh_token = 2;
    int64 access_token_expires_at = 3;
    int64 refresh_token_expires_at = 4;
}

message RefreshTokenRequest {
    string refresh_token = 1;
}

message RefreshTokenResponse {
    string access_token = 1;
    int64 access_token_expires_at = 2;
}

message RevokeTokenRequest {
    string token_id = 1;
}

message RevokeTokenResponse {
    bool success = 1;
}
```

### 7.2 Credit Proto

**File:** `proto/credit.proto`

```protobuf
syntax = "proto3";

package aicafe.credit;

option go_package = "github.com/aicafe-api-gateway/pkg/grpc/credit";

service CreditService {
    rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
    rpc Deduct(DeductRequest) returns (DeductResponse);
    rpc AddCredit(AddCreditRequest) returns (AddCreditResponse);
    rpc GetTransactions(GetTransactionsRequest) returns (GetTransactionsResponse);
    rpc CheckBalance(CheckBalanceRequest) returns (CheckBalanceResponse);
}

message GetBalanceRequest {
    string user_id = 1;
}

message GetBalanceResponse {
    string user_id = 1;
    double balance = 2;
    double pending_balance = 3;
    int64 updated_at = 4;
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

message AddCreditRequest {
    string user_id = 1;
    double amount = 2;
    string source = 3;  // "topup", "refund", "promotion"
    string transaction_id = 4;
}

message AddCreditResponse {
    bool success = 1;
    double new_balance = 2;
}

message GetTransactionsRequest {
    string user_id = 1;
    int32 limit = 2;
    int32 offset = 3;
}

message CreditTransaction {
    string id = 1;
    string user_id = 2;
    double amount = 3;
    string type = 4;
    string reference_id = 5;
    double balance_after = 6;
    string description = 7;
    int64 created_at = 8;
}

message GetTransactionsResponse {
    repeated CreditTransaction transactions = 1;
    int32 total = 2;
}

message CheckBalanceRequest {
    string user_id = 1;
    double required_amount = 2;
}

message CheckBalanceResponse {
    bool sufficient = 1;
    double current_balance = 2;
    double required_amount = 3;
}
```

### 7.3 gRPC Client Setup

**File:** `internal/grpc/client.go`

```go
package grpc

import (
    "context"
    "time"

    "github.com/google/uuid"
    "aicafe-api-gateway/internal/config"
    authpb "aicafe-api-gateway/pkg/grpc/auth"
    creditpb "aicafe-api-gateway/pkg/grpc/credit"
    "google.golang.org/grpc"
    "google.golang.org/grpc/credentials/insecure"
)

type GRPCClients struct {
    Auth   authpb.AuthServiceClient
    Credit creditpb.CreditServiceClient
}

func NewGRPCClients(cfg *config.GRPCConfig) (*GRPCClients, error) {
    conn, err := grpc.Dial(
        cfg.Addr,
        grpc.WithTransportCredentials(insecure.NewCredentials()),
        grpc.WithUnaryInterceptor(timeoutInterceptor(30*time.Second)),
    )
    if err != nil {
        return nil, err
    }

    return &GRPCClients{
        Auth:   authpb.NewAuthServiceClient(conn),
        Credit: creditpb.NewCreditServiceClient(conn),
    }, nil
}

func timeoutInterceptor(timeout time.Duration) grpc.UnaryClientInterceptor {
    return func(ctx context.Context, method string, req, reply interface{}, cc *grpc.ClientConn, invoker grpc.UnaryInvoker, opts ...grpc.CallOption) error {
        ctx, cancel := context.WithTimeout(ctx, timeout)
        defer cancel()
        return invoker(ctx, method, req, reply, cc, opts...)
    }
}

// Credit Service Methods
func (c *GRPCClients) CheckCreditBalance(ctx context.Context, userID uuid.UUID, amount float64) (bool, float64, error) {
    resp, err := c.Credit.CheckBalance(ctx, &creditpb.CheckBalanceRequest{
        UserId:         userID.String(),
        RequiredAmount: amount,
    })
    if err != nil {
        return false, 0, err
    }
    return resp.Sufficient, resp.CurrentBalance, nil
}

func (c *GRPCClients) DeductCredit(ctx context.Context, userID uuid.UUID, amount float64, orderID, reason string) error {
    _, err := c.Credit.Deduct(ctx, &creditpb.DeductRequest{
        UserId:   userID.String(),
        Amount:   amount,
        OrderId:  orderID,
        Reason:   reason,
    })
    return err
}
```

---

## 8. Migration Checklist

### Phase 1: Security Fixes (Day 1)
- [ ] Remove `_debug_otp` from response
- [ ] Implement Redis OTP storage
- [ ] Change SHA256 to bcrypt
- [ ] Fix OTP randomness (crypto/rand)
- [ ] Add input validation (go-playground/validator)

### Phase 2: Architecture (Day 2-3)
- [ ] Create DTO package
- [ ] Extract business logic to Service layer
- [ ] Update handlers to delegate to services
- [ ] Standardize response format
- [ ] Update error handling

### Phase 3: Dependencies (Day 4)
- [ ] Add Redis client
- [ ] Update config for Redis
- [ ] Update main.go wiring
- [ ] Add graceful shutdown

### Phase 4: gRPC Preparation (Day 5)
- [ ] Create proto files
- [ ] Generate Go code
- [ ] Implement gRPC client
- [ ] Update CreditService to use gRPC

---

## 9. Testing Strategy

### Unit Tests
```bash
# Test services
go test ./internal/service/... -v

# Test handlers
go test ./internal/handler/... -v

# Test repository
go test ./internal/repository/... -v
```

### Integration Tests
```bash
# Test with real Redis
docker-compose up -d redis postgres
go test ./... -tags=integration
```

### Security Tests
- [ ] OTP expiration
- [ ] Rate limiting
- [ ] Password strength
- [ ] Token blacklist
- [ ] SQL injection
- [ ] Input validation
