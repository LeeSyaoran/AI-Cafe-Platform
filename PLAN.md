# AI Café Platform - Implementation Plan

## Tổng quan
Refactor Go API Gateway từ current state (MVP với security issues) → Production-ready với proper layered architecture và microservices-ready design.

## 3 Tasks đã tạo tài liệu:

### 📋 Task 1: Security Fixes
**File:** `docs/REFACTOR_GO_API_GATEWAY.md`  
**Mục tiêu:** Fix critical security vulnerabilities

**Changes required:**
| File | Change |
|------|--------|
| `internal/handler/auth_handler.go` | Remove `_debug_otp` from response |
| `internal/service/auth_service.go` | Change SHA256 → bcrypt, crypto/rand for OTP |
| `internal/repository/redis.go` | **NEW** - Redis for OTP storage |
| `cmd/server/main.go` | Add Redis initialization |

**Commands to verify:**
```bash
go test ./internal/service/... -v
grep -r "_debug_otp" internal/
```

### 📋 Task 2: Service Layer Implementation
**File:** `docs/REFACTOR_GO_API_GATEWAY.md` (Section 5)  
**Mục tiêu:** Extract business logic from handlers

**Files to create/modify:**
| File | Action | Description |
|------|--------|-------------|
| `internal/dto/auth.go` | CREATE | Request/Response DTOs |
| `internal/dto/order.go` | CREATE | Order DTOs |
| `internal/service/auth_service.go` | REFACTOR | Move auth logic from handler |
| `internal/service/order_service.go` | CREATE | Order business logic |
| `internal/service/credit_service.go` | CREATE | Credit operations |
| `internal/handler/auth_handler.go` | REFACTOR | Delegate to services |
| `internal/handler/order_handler.go` | REFACTOR | Delegate to services |

**Verification:**
```bash
go test ./... -coverprofile=coverage.out
go tool cover -html=coverage.out -o coverage.html
```

### 📋 Task 3: Microservices Preparation
**File:** `docs/UPDATED_BACKEND_DESIGN.md` + `docs/MIGRATION_PLAN.md`  
**Mục tiêu:** Document target architecture và migration path

**Artifacts:**
- `proto/auth.proto` - Auth service proto
- `proto/credit.proto` - Credit service proto
- `proto/order.proto` - Order service proto
- `internal/grpc/client.go` - gRPC client setup

---

## Implementation Order

```
Week 1-2: Phase 1 - MVP Stabilization
├── Day 1-2: Create DTOs
├── Day 3-5: Extract to Service Layer
└── Day 6-10: Standardize responses, add tests

Week 3-4: Phase 2 - Security Hardening
├── Day 11-12: Redis OTP storage
├── Day 13-14: bcrypt + crypto/rand
└── Day 15-20: Rate limiting, validation

Week 5-12: Phase 3 - Service Extraction
├── Week 5-6: Credit Service (Go)
├── Week 7-8: Auth Service (Go)
└── Week 9-12: User & Order Services (Java)

Week 13-20: Phase 4 - AI Integration
├── Week 13-14: Chat enhancement
├── Week 15-16: Image generation
└── Week 17-20: Real-time features
```

---

## Key Files Reference

| File | Purpose |
|------|---------|
| `internal/handler/auth_handler.go` | Auth endpoints - NEEDS REFACTOR |
| `internal/handler/order_handler.go` | Order endpoints - NEEDS REFACTOR |
| `internal/repository/postgres.go` | DB access - ADD METHODS |
| `cmd/server/main.go` | App entry - ADD REDIS |

---

## Success Criteria

- ✅ All security issues fixed
- ✅ Handler → Service → Repository pattern implemented
- ✅ Unified response format across all endpoints
- ✅ Redis used for OTP storage
- ✅ bcrypt for password hashing
- ✅ Unit tests for core services
- ✅ Proto files ready for microservices
