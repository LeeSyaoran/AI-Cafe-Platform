# AI Café Platform - Implementation Plan

## Tổng quan
Refactor Go API Gateway từ current state (MVP với security issues) → Production-ready với proper layered architecture và microservices-ready design.

## ✅ Current Status: Phase 1 Complete

## 3 Tasks đã tạo tài liệu:

### 📋 Task 1: Security Fixes
**File:** `docs/REFACTOR_GO_API_GATEWAY.md`  
**Status:** Documented - pending implementation  
**Mục tiêu:** Fix critical security vulnerabilities

**Changes required:**
| File | Change |
|------|--------|
| `internal/handler/auth_handler.go` | Remove `_debug_otp` from response |
| `internal/service/auth_service.go` | Already uses bcrypt, crypto/rand ✅ |
| `internal/repository/redis.go` | **NEW** - Redis for OTP storage |
| `cmd/server/main.go` | Add Redis initialization |

**Commands to verify:**
```bash
go test ./internal/service/... -v
grep -r "_debug_otp" internal/
```

### 📋 Task 2: Service Layer Implementation
**Status:** ✅ COMPLETED  
**Mục tiêu:** Extract business logic from handlers

**Files created:**
| File | Action | Status |
|------|--------|--------|
| `internal/dto/auth.go` | CREATE | ✅ Done |
| `internal/dto/order.go` | CREATE | ✅ Done |
| `internal/dto/cart.go` | CREATE | ✅ Done |
| `internal/dto/product.go` | CREATE | ✅ Done |
| `internal/dto/credit.go` | CREATE | ✅ Done |
| `internal/errors/errors.go` | CREATE | ✅ Done |
| `internal/service/auth_service.go` | CREATE | ✅ Done |
| `internal/service/order_service.go` | CREATE | ✅ Done |
| `internal/service/product_service.go` | CREATE | ✅ Done |
| `internal/service/cart_service.go` | CREATE | ✅ Done |
| `internal/service/credit_service.go` | CREATE | ✅ Done |
| `internal/model/models.go` | UPDATE | ✅ Done |
| `pkg/response/response.go` | UPDATE | ✅ Done |
| `internal/repository/postgres.go` | UPDATE | ✅ Done |

### 📋 Task 4: Handler Refactoring (Phase 1.3)
**Status:** ✅ COMPLETED  
**Mục tiêu:** Update handlers to delegate to services

**Files refactored:**
| File | Status |
|------|--------|
| `internal/handler/auth_handler.go` | ✅ Done |
| `internal/handler/order_handler.go` | ✅ Done |
| `internal/handler/product_handler.go` | ✅ Done |
| `internal/handler/cart_handler.go` | ✅ Done |
| `cmd/server/main.go` | ✅ Done |

**Verification:**
```bash
go test ./... -coverprofile=coverage.out
go tool cover -html=coverage.out -o coverage.html
```

### 📋 Task 3: Microservices Preparation
**File:** `docs/UPDATED_BACKEND_DESIGN.md` + `docs/MIGRATION_PLAN.md`  
**Status:** Documented - pending implementation  
**Mục tiêu:** Document target architecture và migration path

**Artifacts:**
- `proto/auth.proto` - Auth service proto (documented)
- `proto/credit.proto` - Credit service proto (documented)
- `proto/order.proto` - Order service proto (documented)
- `internal/grpc/client.go` - gRPC client setup (TODO)

### 📋 Task 5: Redis OTP Storage
**Status:** ✅ COMPLETED  
**Mục tiêu:** Move OTP storage from in-memory to Redis

**Files created:**
| File | Action | Status |
|------|--------|--------|
| `internal/repository/redis.go` | CREATE | ✅ Done |

### 📋 Task 6: Password Hashing (bcrypt)
**Status:** ✅ COMPLETED  
**Mục tiêu:** Add bcrypt for password hashing

**Changes:**
- `generateSecureOTP()` - crypto/rand for secure OTP
- `hashPassword()` - bcrypt hashing
- `verifyPassword()` - bcrypt verification

### 📋 Task 7: Token Blacklist (Redis)
**Status:** ✅ COMPLETED  
**Mục tiêu:** Add logout token blacklist

**Changes:**
- Redis blacklist operations added
- Logout now invalidates token

---

## Implementation Order

```
✅ Week 1-2: Phase 1 - MVP Stabilization
├── Day 1-2: Create DTOs ✅
├── Day 3-5: Extract to Service Layer ✅
├── Day 6-10: Standardize responses, add tests ✅
└── Day 6-10: Refactor Handlers to use services ✅

✅ Week 3-4: Phase 2 - Security Hardening (COMPLETE)
├── Redis OTP storage ✅
├── bcrypt + crypto/rand ✅
├── Token blacklist ✅
└── Remove debug OTP ✅

⏳ Week 5-12: Phase 3 - Service Extraction
├── Week 5-6: Credit Service (Go)
├── Week 7-8: Auth Service (Go)
└── Week 9-12: User & Order Services (Java)

📋 Week 13-20: Phase 4 - AI Integration
├── Week 13-14: Chat enhancement
├── Week 15-16: Image generation
└── Week 17-20: Real-time features
```

---

## Key Files Reference

| File | Purpose | Status |
|------|---------|--------|
| `internal/handler/auth_handler.go` | Auth endpoints | ✅ DONE |
| `internal/handler/order_handler.go` | Order endpoints | ✅ DONE |
| `internal/handler/product_handler.go` | Product endpoints | ✅ DONE |
| `internal/handler/cart_handler.go` | Cart endpoints | ✅ DONE |
| `internal/repository/postgres.go` | DB access | ✅ READY |
| `internal/repository/redis.go` | Redis access | ✅ READY |
| `internal/service/*.go` | Business logic | ✅ CREATED |
| `cmd/server/main.go` | App entry | ✅ UPDATED |

---

## Success Criteria

- ✅ DTOs created for all modules
- ✅ Service layer with business logic
- ✅ Typed error handling
- ✅ Unified response format
- ✅ Handler refactoring to use services
- ✅ Redis OTP storage
- ✅ Password hashing (bcrypt)
- ✅ Token blacklist
- ✅ Debug OTP removed
- ⏳ Unit tests for core services
