# API Reference

## Base URL

```
Production: https://api.aicafe.vn
Staging:    https://api-staging.aicafe.vn
```

## Authentication

### Send OTP
```http
POST /api/v1/auth/send-otp
Content-Type: application/json

{
  "phone": "+84912345678"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "expires_in": 300,
    "resend_available_in": 60
  }
}
```

### Verify OTP
```http
POST /api/v1/auth/verify-otp
Content-Type: application/json

{
  "phone": "+84912345678",
  "code": "123456"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "tokens": {
      "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
      "expires_in": 900,
      "token_type": "Bearer"
    },
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "phone": "+84912345678",
      "name": "Nguyễn Văn A",
      "tier": "BASIC"
    }
  }
}
```

### Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
}
```

### Logout
```http
POST /api/v1/auth/logout
Authorization: Bearer <access_token>
```

---

## User

### Get Current User
```http
GET /api/v1/users/me
Authorization: Bearer <access_token>
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "phone": "+84912345678",
    "name": "Nguyễn Văn A",
    "email": "nguyen.van.a@email.com",
    "avatar_url": "https://assets.aicafe.vn/avatars/user-123.jpg",
    "tier": "BASIC",
    "status": "ACTIVE",
    "membership": {
      "type": "BASIC",
      "expires_at": null,
      "monthly_credits": 0
    },
    "created_at": "2024-01-15T10:30:00Z",
    "updated_at": "2024-01-15T10:30:00Z"
  }
}
```

### Update Profile
```http
PATCH /api/v1/users/me
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "Nguyễn Văn B",
  "email": "nguyen.van.b@email.com"
}
```

---

## Credits

### Get Balance
```http
GET /api/v1/users/me/credits
Authorization: Bearer <access_token>
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "credits": 1500,
    "workspace_minutes": 45,
    "daily_usage": {
      "date": "2024-01-15",
      "credits_used": 50,
      "requests_count": 12
    },
    "monthly_usage": {
      "month": "2024-01",
      "credits_used": 1500,
      "requests_count": 350
    }
  }
}
```

### Get Credit History
```http
GET /api/v1/users/me/credits/history
Authorization: Bearer <access_token>

Query Parameters:
- page (int, default: 1)
- limit (int, default: 20, max: 100)
- type (string: ALL, EARNED, DEDUCTED, EXPIRED)
- start_date (ISO date)
- end_date (ISO date)
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "tx-001",
        "type": "DEDUCTED",
        "amount": 50,
        "balance_after": 1450,
        "source": "AI_CHAT",
        "description": "GPT-4o request",
        "created_at": "2024-01-15T14:30:00Z"
      },
      {
        "id": "tx-002",
        "type": "EARNED",
        "amount": 500,
        "balance_after": 1500,
        "source": "PURCHASE",
        "description": "Purchase: 500 Credits Pack",
        "created_at": "2024-01-14T09:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 45,
      "total_pages": 3
    }
  }
}
```

---

## AI Chat

### Send Chat Request
```http
POST /api/v1/ai/chat
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "model": "gpt-4o",
  "messages": [
    {
      "role": "system",
      "content": "Bạn là một trợ lý AI hữu ích."
    },
    {
      "role": "user",
      "content": "Xin chào, bạn có thể giúp tôi viết một email xin nghỉ phép không?"
    }
  ],
  "temperature": 0.7,
  "max_tokens": 1000,
  "stream": false
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "chatcmpl-123",
    "model": "gpt-4o",
    "choices": [
      {
        "index": 0,
        "message": {
          "role": "assistant",
          "content": "Dưới đây là mẫu email xin nghỉ phép bạn có thể tham khảo..."
        },
        "finish_reason": "stop"
      }
    ],
    "usage": {
      "prompt_tokens": 150,
      "completion_tokens": 250,
      "total_tokens": 400,
      "credits_used": 80
    },
    "credits_remaining": 1420,
    "processing_time_ms": 1250
  }
}
```

### Stream Chat Response
```http
POST /api/v1/ai/chat
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "model": "gpt-4o",
  "messages": [
    {
      "role": "user",
      "content": "Viết một bài văn 500 từ về bảo vệ môi trường"
    }
  ],
  "stream": true
}
```

**Response (200 OK) - Server-Sent Events**
```
event: message
data: {"delta": "Bảo", "credits_used": 0}

event: message
data: {"delta": " vệ", "credits_used": 0}

event: message
data: {"delta": " môi", "credits_used": 0}

event: done
data: {"total_tokens": 500, "credits_used": 100, "credits_remaining": 1400}
```

### Get Available Models
```http
GET /api/v1/ai/models
Authorization: Bearer <access_token>
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "chat": [
      {
        "id": "gpt-4o",
        "name": "GPT-4o",
        "provider": "openai",
        "description": "Mô hình GPT-4o mạnh nhất",
        "credits_per_1k_tokens": 200,
        "max_tokens": 128000,
        "is_available": true
      },
      {
        "id": "claude-sonnet-4-20250514",
        "name": "Claude Sonnet 4",
        "provider": "anthropic",
        "description": "Mô hình Claude mới nhất",
        "credits_per_1k_tokens": 150,
        "max_tokens": 200000,
        "is_available": true
      }
    ],
    "image": [
      {
        "id": "dall-e-3",
        "name": "DALL-E 3",
        "provider": "openai",
        "credits_per_image": 50,
        "sizes": ["1024x1024", "1024x1792", "1792x1024"],
        "is_available": true
      }
    ],
    "video": [
      {
        "id": "sora-1",
        "name": "Sora",
        "provider": "openai",
        "credits_per_second": 100,
        "max_duration_seconds": 20,
        "is_available": true
      }
    ]
  }
}
```

---

## Image Generation

### Generate Image
```http
POST /api/v1/ai/image
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "model": "dall-e-3",
  "prompt": "A beautiful sunset over the ocean with palm trees, digital art style",
  "size": "1024x1024",
  "quality": "standard",
  "n": 1
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "img-001",
    "model": "dall-e-3",
    "images": [
      {
        "url": "https://assets.aicafe.vn/ai-images/img-001-abc123.png",
        "revised_prompt": "A beautiful sunset over the ocean with palm trees, digital art style, vibrant colors, golden hour lighting"
      }
    ],
    "credits_used": 50,
    "credits_remaining": 1350
  }
}
```

---

## Orders

### Create Order
```http
POST /api/v1/orders
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "type": "CREDIT_PURCHASE",
  "items": [
    {
      "product_id": "credits-1000",
      "quantity": 1
    }
  ],
  "payment_method": "VNPAY",
  "coupon_code": "WELCOME10",
  "idempotency_key": "order-abc123"
}
```

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "order_id": "ord-550e8400",
    "order_number": "AICA20240115001",
    "status": "PENDING",
    "items": [
      {
        "product_id": "credits-1000",
        "name": "1000 Credits",
        "quantity": 1,
        "unit_price": 250000,
        "total": 250000
      }
    ],
    "subtotal": 250000,
    "discount": 25000,
    "coupon_applied": "WELCOME10",
    "tax": 22500,
    "total": 247500,
    "payment_url": "https://vnpay.vn/payment?order=...",
    "expires_at": "2024-01-15T15:00:00Z"
  }
}
```

### Get Orders
```http
GET /api/v1/orders
Authorization: Bearer <access_token>

Query Parameters:
- page (int, default: 1)
- limit (int, default: 20)
- status (string: ALL, PENDING, PAID, CANCELLED, REFUNDED)
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "order_id": "ord-550e8400",
        "order_number": "AICA20240115001",
        "type": "CREDIT_PURCHASE",
        "status": "PAID",
        "total": 247500,
        "credits_added": 1125,
        "payment_method": "VNPAY",
        "paid_at": "2024-01-15T10:35:00Z",
        "created_at": "2024-01-15T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 5,
      "total_pages": 1
    }
  }
}
```

### Get Order Details
```http
GET /api/v1/orders/{order_id}
Authorization: Bearer <access_token>
```

---

## Membership

### Get Current Membership
```http
GET /api/v1/users/me/membership
Authorization: Bearer <access_token>
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "type": "BASIC",
    "status": "ACTIVE",
    "benefits": [
      "Truy cập GPT-4o Mini miễn phí",
      "1000 credits/tháng",
      "Hỗ trợ qua email"
    ],
    "upgrade_available": true,
    "plans": [
      {
        "id": "DEVELOPER",
        "name": "Developer",
        "price": 99000,
        "interval": "month",
        "benefits": [
          "Truy cập tất cả models",
          "5000 credits/tháng",
          "Image generation",
          "Priority support"
        ]
      }
    ]
  }
}
```

### Subscribe to Plan
```http
POST /api/v1/users/me/membership
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "plan_id": "DEVELOPER",
  "payment_method": "VNPAY",
  "idempotency_key": "sub-abc123"
}
```

---

## Coupons

### Validate Coupon
```http
POST /api/v1/coupons/validate
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "code": "WELCOME10",
  "order_total": 250000
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "valid": true,
    "code": "WELCOME10",
    "type": "PERCENTAGE",
    "value": 10,
    "max_discount": 50000,
    "discount_amount": 25000,
    "final_total": 225000,
    "expires_at": "2024-12-31T23:59:59Z"
  }
}
```

---

## Payment

### Payment Webhook (VNPay)
```http
POST /api/v1/webhooks/payment
Content-Type: application/json
X-Vnp-Hash: <signature>

{
  "vnp_Amount": 24750000,
  "vnp_BankCode": "NCB",
  "vnp_CardType": "ATM",
  "vnp_OrderInfo": "AICA20240115001",
  "vnp_PayDate": "20240115103500",
  "vnp_ResponseCode": "00",
  "vnp_TmnCode": "AICAFE01",
  "vnp_TransactionNo": 12345678,
  "vnp_TxnRef": "ord-550e8400"
}
```

**Response (200 OK)**
```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

---

## Usage Statistics

### Get Daily Usage
```http
GET /api/v1/users/me/usage/daily
Authorization: Bearer <access_token>

Query Parameters:
- start_date (ISO date, required)
- end_date (ISO date, required)
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "period": {
      "start": "2024-01-01",
      "end": "2024-01-15"
    },
    "total": {
      "credits_used": 5500,
      "requests_count": 1250
    },
    "by_model": [
      {
        "model": "gpt-4o",
        "credits_used": 3000,
        "requests_count": 800
      },
      {
        "model": "dall-e-3",
        "credits_used": 2500,
        "requests_count": 450
      }
    ],
    "daily_breakdown": [
      {
        "date": "2024-01-15",
        "credits_used": 500,
        "requests_count": 120
      }
    ]
  }
}
```

### Get Monthly Usage
```http
GET /api/v1/users/me/usage/monthly
Authorization: Bearer <access_token>

Query Parameters:
- year (int, required)
- month (int, required)
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `AUTH_OTP_INVALID` | 400 | Invalid OTP code |
| `AUTH_OTP_EXPIRED` | 400 | OTP code has expired |
| `AUTH_OTP_ATTEMPTS_EXCEEDED` | 429 | Too many OTP attempts |
| `AUTH_TOKEN_INVALID` | 401 | Invalid access token |
| `AUTH_TOKEN_EXPIRED` | 401 | Access token has expired |
| `AUTH_REFRESH_FAILED` | 401 | Failed to refresh token |
| `USER_NOT_FOUND` | 404 | User not found |
| `CREDITS_INSUFFICIENT` | 400 | Not enough credits |
| `CREDITS_EXPIRED` | 400 | Credits have expired |
| `MODEL_UNAVAILABLE` | 400 | AI model not available |
| `ORDER_NOT_FOUND` | 404 | Order not found |
| `ORDER_EXPIRED` | 400 | Order has expired |
| `ORDER_ALREADY_PAID` | 400 | Order already paid |
| `COUPON_INVALID` | 400 | Invalid coupon code |
| `COUPON_EXPIRED` | 400 | Coupon has expired |
| `PAYMENT_FAILED` | 400 | Payment processing failed |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Internal server error |

---

## Rate Limits

| Tier | Requests/minute | Requests/day |
|------|-----------------|--------------|
| BASIC | 30 | 500 |
| DEVELOPER | 100 | 2000 |
| ENTERPRISE | 500 | 10000 |

Rate limit headers included in every response:
```
X-RateLimit-Limit: 30
X-RateLimit-Remaining: 25
X-RateLimit-Reset: 1705396200
```
