# AI Café - API Specification

## Overview

Public API cho developers và internal services.

## Authentication

### Customer Authentication
```
POST /auth/login
POST /auth/refresh
POST /auth/logout
```

### Developer API Key
```
Authorization: Bearer <AI_CAFE_API_KEY>
```

## Core Endpoints

### Users

```yaml
GET /api/v1/users/me
  Response:
    id: string
    phone: string
    email: string
    tier: basic | developer | pro | builder
    created_at: timestamp

GET /api/v1/users/me/wallet
  Response:
    credits: number
    workspace_minutes: number
    tier: string
    daily_limit_remaining: number
```

### Orders

```yaml
POST /api/v1/orders
  Body:
    items: [{product_id, quantity}]
    payment_method: cash | qr | card
  Response:
    order_id: string
    total: number
    status: pending | paid

GET /api/v1/orders
  Query: ?status=&page=&limit=
  Response:
    orders: []
    pagination: {total, page, limit}

GET /api/v1/orders/{id}
  Response:
    order: {id, items, total, status, ...}
```

### Credit System

```yaml
GET /api/v1/credits/balance
  Response:
    credits: number
    workspace_minutes: number
    tier: string

GET /api/v1/credits/transactions
  Query: ?type=&from=&to=&page=
  Response:
    transactions: [{
      id, type, amount, source, 
      service, model, created_at
    }]

GET /api/v1/credits/expiration
  Response:
    expiring: [{
      credits: number
      expires_at: timestamp
    }]
```

## AI Endpoints

### Chat

```yaml
POST /api/v1/ai/chat
  Body:
    message: string
    model?: string (optional, auto-select if omitted)
    stream?: boolean
  Response:
    id: string
    message: string
    model: string
    credits_used: number
    tokens: {input, output}
```

### Code

```yaml
POST /api/v1/ai/code
  Body:
    prompt: string
    language?: string
    model?: string
  Response:
    code: string
    language: string
    credits_used: number
```

### Image

```yaml
POST /api/v1/ai/image
  Body:
    prompt: string
    size?: 1024x1024 | 1792x1024 | 1024x1792
    quality?: standard | hd
  Response:
    url: string
    revised_prompt: string
    credits_used: number
```

### Video

```yaml
POST /api/v1/ai/video
  Body:
    prompt: string
    duration?: 5 | 10 | 20
    resolution?: 720p | 1080p
  Response:
    id: string
    status: processing | ready
    url?: string
    credits_used: number
    quota_remaining: number

GET /api/v1/ai/video/{id}
  Response:
    id, status, url, credits_used
```

## AI Gateway (Internal)

### Model Routing

```yaml
POST /api/internal/ai/route
  Headers: X-Internal-Key: {key}
  Body:
    user_id: string
    task_type: chat | code | image | video | reasoning
    prompt: string
    tier: string
  Response:
    model: string
    provider: string
    estimated_cost: number
```

### Usage Tracking

```yaml
POST /api/internal/ai/usage
  Headers: X-Internal-Key: {key}
  Body:
    request_id: string
    user_id: string
    model: string
    input_tokens: number
    output_tokens: number
    credits_used: number
    provider_cost: number
```

## Workspace

```yaml
POST /api/v1/workspace/sessions
  Response:
    session_id: string
    expires_at: timestamp
    minutes_remaining: number

GET /api/v1/workspace/sessions/{id}
  Response:
    status: active | ended | expired
    minutes_used: number
    minutes_remaining: number

DELETE /api/v1/workspace/sessions/{id}
  Response:
    session_closed: true
    minutes_used: number
```

## Membership

```yaml
GET /api/v1/membership
  Response:
    tier: string
    status: active | paused | cancelled
    next_billing: date
    credits_remaining: number

POST /api/v1/membership/subscribe
  Body:
    tier: student | developer | pro
    payment_method: ...
  Response:
    membership_id: string
    status: active

DELETE /api/v1/membership
  Response:
    cancelled: true
    effective_date: date
```

## Admin Endpoints

```yaml
GET /api/v1/admin/dashboard
  Response:
    today: {customers, revenue, ai_cost}
    this_month: {revenue, ai_cost}
    credits: {issued, used, expired}

GET /api/v1/admin/users
  Query: ?tier=&page=&limit=

POST /api/v1/admin/credits/adjust
  Body:
    user_id: string
    amount: number
    reason: string

GET /api/v1/admin/ai/usage
  Query: ?period=daily|weekly|monthly
  Response:
    by_model: []
    by_user: []
    by_provider: []
```

## Webhooks

```yaml
POST /api/v1/webhooks/payment
  Events: payment.success, payment.failed
  Body:
    event: string
    order_id: string
    amount: number
    status: string

POST /api/v1/webhooks/usage
  Events: daily_limit_reached, monthly_budget_warning
  Body:
    event: string
    user_id: string
    usage: number
```

## Rate Limits

| Endpoint | Limit |
|----------|-------|
| /auth/* | 10/min |
| /api/v1/ai/* | By tier |
| /api/v1/orders | 30/min |
| /api/v1/credits/* | 60/min |
| Admin endpoints | 100/min |

## Error Responses

```yaml
400 Bad Request
{
  error: "invalid_request"
  message: "Description"
  details: {}
}

401 Unauthorized
{
  error: "unauthorized"
  message: "Invalid or expired token"
}

403 Forbidden
{
  error: "insufficient_credits"
  message: "Not enough credits"
  balance: 100
  required: 500
}

429 Too Many Requests
{
  error: "rate_limit_exceeded"
  message: "Rate limit reached"
  retry_after: 60
}
```
