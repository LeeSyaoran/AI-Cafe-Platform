# API Protocols Documentation

## 1. Authentication Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              OTP AUTHENTICATION FLOW                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  User                          App                          Backend                    │
│   │                             │                              │                      │
│   │  1. Enter phone             │                              │                      │
│   │────────────────────────────►│                              │                      │
│   │                             │                              │                      │
│   │                             │  2. POST /api/v1/auth/send-otp               │
│   │                             │  {phone: "+84..."}           │                      │
│   │                             │─────────────────────────────►│                      │
│   │                             │                              │                      │
│   │                             │                              │  3. Validate phone   │
│   │                             │                              │     Generate OTP     │
│   │                             │                              │     Send SMS         │
│   │                             │                              │  Store in Redis      │
│   │                             │                              ◄─────────────────────│
│   │                             │                              │                      │
│   │  4. OTP sent to SMS         │  5. Success response        │                      │
│   │◄────────────────────────────│◄────────────────────────────│                      │
│   │                             │                              │                      │
│   │  6. Enter OTP               │                              │                      │
│   │────────────────────────────►│                              │                      │
│   │                             │                              │                      │
│   │                             │  7. POST /api/v1/auth/verify-otp             │
│   │                             │  {phone, code}              │                      │
│   │                             │─────────────────────────────►│                      │
│   │                             │                              │                      │
│   │                             │                              │  8. Verify OTP       │
│   │                             │                              │     Check attempts   │
│   │                             │                              │  9. Create/Search   │
│   │                             │                              │     user             │
│   │                             │                              │  10. Generate JWT    │
│   │                             │                              │     tokens           │
│   │                             │  11. {access_token,         │                      │
│   │                             │       refresh_token,         │                      │
│   │                             │       user}                  │                      │
│   │                             │◄────────────────────────────│                      │
│   │                             │                              │                      │
│   │  12. Authenticated!         │  13. Store tokens,           │                      │
│   │◄────────────────────────────│     redirect to app          │                      │
│   │                             │                              │                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. AI Request Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                 AI REQUEST FLOW                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  User                          App                          AI Gateway                  │
│   │                             │                              │                      │
│   │  1. Send chat message      │                              │                      │
│   │────────────────────────────►│                              │                      │
│   │                             │                              │                      │
│   │                             │  2. Check rate limit        │                      │
│   │                             │     Check balance           │                      │
│   │                             │◄────────────────────────────│                      │
│   │                             │                              │                      │
│   │                             │  3. Deduct credits          │                      │
│   │                             │     (async via queue)       │                      │
│   │                             │─────────────────────────────►│                      │
│   │                             │                              │                      │
│   │                             │                              │  4. Route to model  │
│   │                             │                              │     (GPT-4o/Claude) │
│   │                             │                              │                      │
│   │                             │                              │  5. Stream response │
│   │                             │  6. SSE stream             │                      │
│   │                             │◄────────────────────────────│                      │
│   │  7. Receive streamed       │                              │                      │
│   │     response               │                              │                      │
│   │◄────────────────────────────│                              │                      │
│   │                             │                              │                      │
│   │                             │  8. Log usage,             │                      │
│   │                             │     update balance          │                      │
│   │                             │─────────────────────────────►│                      │
│   │                             │                              │                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. WebSocket Protocol

### 3.1 Connection

```javascript
// Connect to WebSocket
const ws = new WebSocket('wss://api.aicafe.vn/ws?token={access_token}');

// On successful connection
ws.onopen = () => {
  console.log('Connected to AI Cafe');
  
  // Send presence ping
  ws.send(JSON.stringify({
    type: 'ping',
    timestamp: Date.now()
  }));
};

// Handle incoming messages
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  
  switch (data.type) {
    case 'pong':
      // Heartbeat response
      break;
    case 'ai_response':
      handleAIResponse(data);
      break;
    case 'error':
      handleError(data);
      break;
  }
};
```

### 3.2 Message Types

```typescript
// Outgoing messages (client → server)
interface WSClientMessage {
  type: 'chat' | 'image' | 'ping' | 'subscribe' | 'unsubscribe';
  payload: Record<string, any>;
  request_id?: string;
  timestamp: number;
}

// Incoming messages (server → client)
interface WSServerMessage {
  type: 'ai_response' | 'ai_chunk' | 'error' | 'pong' | 'event';
  request_id: string;
  timestamp: number;
  payload: Record<string, any>;
}

// Chat message
interface ChatRequest {
  type: 'chat';
  payload: {
    model: string;           // 'gpt-4o' | 'claude-3-5-sonnet'
    messages: ChatMessage[];
    temperature?: number;
    stream?: boolean;
  };
}

interface ChatMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

// AI response (server → client)
interface AIResponse {
  type: 'ai_response';
  request_id: string;
  payload: {
    id: string;
    model: string;
    choices: [{
      message: {
        role: 'assistant';
        content: string;
      };
      finish_reason: 'stop' | 'length';
    }];
    usage: {
      prompt_tokens: number;
      completion_tokens: number;
      total_tokens: number;
    };
    credits_used: number;
  };
}

// AI chunk (for streaming)
interface AIChunk {
  type: 'ai_chunk';
  request_id: string;
  payload: {
    delta: string;
    index: number;
    done: boolean;
  };
}
```

---

## 4. REST API Endpoints

### 4.1 Authentication

#### POST /api/v1/auth/send-otp
Send OTP to phone number.

**Request:**
```json
{
  "phone": "+84XXXXXXXXX"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "expires_in": 300
}
```

**Errors:**
- `429` - Rate limited (wait {retry_after} seconds)

---

#### POST /api/v1/auth/verify-otp
Verify OTP and get tokens.

**Request:**
```json
{
  "phone": "+84XXXXXXXXX",
  "code": "123456"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "phone": "+84XXXXXXXXX",
      "name": null,
      "tier": "BASIC",
      "is_new_user": true
    },
    "tokens": {
      "access_token": "eyJhbGciOiJIUzI1NiIs...",
      "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
      "expires_in": 900,
      "token_type": "Bearer"
    }
  }
}
```

**Errors:**
- `400` - Invalid OTP
- `429` - Too many attempts

---

#### POST /api/v1/auth/refresh
Refresh access token.

**Request:**
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 900
  }
}
```

---

### 4.2 User

#### GET /api/v1/users/me
Get current user profile.

**Headers:** `Authorization: Bearer {access_token}`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "phone": "+84XXXXXXXXX",
    "name": "Nguyen Van A",
    "email": "email@example.com",
    "avatar_url": "https://...",
    "tier": "DEVELOPER",
    "status": "ACTIVE",
    "created_at": "2024-01-15T10:30:00Z"
  }
}
```

---

#### PATCH /api/v1/users/me
Update user profile.

**Request:**
```json
{
  "name": "Nguyen Van B",
  "email": "newemail@example.com",
  "avatar_url": "https://..."
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Nguyen Van B",
    "email": "newemail@example.com",
    ...
  }
}
```

---

### 4.3 Credits

#### GET /api/v1/users/me/credits
Get current balance.

**Response (200):**
```json
{
  "success": true,
  "data": {
    "credits": 1500,
    "workspace_minutes": 45,
    "daily_usage": {
      "chat": 50,
      "image": 100,
      "video": 0
    },
    "limits": {
      "rate_limit_rpm": 30,
      "concurrent_connections": 5
    }
  }
}
```

---

### 4.4 AI Chat

#### POST /api/v1/ai/chat
Send chat message (non-streaming).

**Request:**
```json
{
  "model": "gpt-4o",
  "messages": [
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Hello!"}
  ],
  "temperature": 0.7
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "chatcmpl-xxx",
    "model": "gpt-4o",
    "choices": [{
      "message": {
        "role": "assistant",
        "content": "Hello! How can I help you?"
      },
      "finish_reason": "stop"
    }],
    "usage": {
      "prompt_tokens": 20,
      "completion_tokens": 15,
      "total_tokens": 35
    },
    "credits_used": 1,
    "remaining_balance": 1499
  }
}
```

---

#### POST /api/v1/ai/chat/stream
Send chat message (streaming).

**Request:**
```json
{
  "model": "gpt-4o",
  "messages": [
    {"role": "user", "content": "Write a story..."}
  ],
  "stream": true
}
```

**Response (200):** Server-Sent Events

```
event: chunk
data: {"delta": "Once", "index": 0, "done": false}

event: chunk
data: {"delta": " upon", "index": 1, "done": false}

event: chunk
data: {"delta": " a time", "index": 2, "done": false}

...

event: done
data: {"total_tokens": 500, "credits_used": 2, "finish_reason": "stop"}
```

---

#### POST /api/v1/ai/image
Generate image.

**Request:**
```json
{
  "model": "dall-e-3",
  "prompt": "A beautiful sunset over mountains",
  "size": "1024x1024",
  "quality": "standard",
  "n": 1
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "img-xxx",
    "model": "dall-e-3",
    "url": "https://...",
    "credits_used": 50,
    "remaining_balance": 1449
  }
}
```

---

#### POST /api/v1/ai/video
Generate video.

**Request:**
```json
{
  "model": "sora",
  "prompt": "A flowing river through a forest",
  "duration": 10,
  "resolution": "1080p"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "vid-xxx",
    "model": "sora",
    "status": "processing",
    "estimated_completion": "2024-01-15T11:00:00Z",
    "credits_used": 1000,
    "remaining_balance": 449
  }
}
```

---

#### GET /api/v1/ai/models
Get available AI models.

**Response (200):**
```json
{
  "success": true,
  "data": {
    "chat": [
      {"id": "gpt-4o", "name": "GPT-4o", "provider": "OpenAI", "credits_per_1k": 1},
      {"id": "claude-3-5-sonnet", "name": "Claude 3.5 Sonnet", "provider": "Anthropic", "credits_per_1k": 1},
      {"id": "gemini-1.5-pro", "name": "Gemini 1.5 Pro", "provider": "Google", "credits_per_1k": 0}
    ],
    "image": [
      {"id": "dall-e-3", "name": "DALL-E 3", "provider": "OpenAI", "credits_per_image": 50},
      {"id": "stable-diffusion-xl", "name": "SDXL", "provider": "Stability AI", "credits_per_image": 10}
    ],
    "video": [
      {"id": "sora", "name": "Sora", "provider": "OpenAI", "credits_per_second": 100},
      {"id": "stable-video", "name": "Stable Video", "provider": "Stability AI", "credits_per_second": 50}
    ]
  }
}
```

---

### 4.5 Orders

#### POST /api/v1/orders
Create order (credit purchase).

**Request:**
```json
{
  "type": "CREDIT_PURCHASE",
  "items": [{
    "package_id": "uuid",
    "quantity": 1
  }],
  "payment_method": "VNPAY",
  "coupon_code": "SAVE10"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "order_id": "uuid",
    "order_number": "ORD-20240115-001",
    "payment_url": "https://sandbox.vnpayment.vn/...",
    "total": 99000,
    "discount": 9900,
    "credits": 1100
  }
}
```

---

#### GET /api/v1/orders
List user orders.

**Query params:** `?page=1&limit=20&status=COMPLETED`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": "uuid",
        "order_number": "ORD-20240115-001",
        "type": "CREDIT_PURCHASE",
        "status": "COMPLETED",
        "total": 99000,
        "credits": 1100,
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

---

### 4.6 Membership

#### GET /api/v1/membership
Get membership info.

**Response (200):**
```json
{
  "success": true,
  "data": {
    "tier": "DEVELOPER",
    "display_name": "Developer",
    "benefits": ["chat", "image", "code"],
    "current_period": {
      "start": "2024-01-01T00:00:00Z",
      "end": "2024-02-01T00:00:00Z"
    },
    "credits": {
      "included": 5000,
      "used": 1500,
      "remaining": 3500
    },
    "auto_renew": true
  }
}
```

---

#### POST /api/v1/membership/subscribe
Subscribe to membership.

**Request:**
```json
{
  "tier": "PRO",
  "payment_method": "VNPAY"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "membership_id": "uuid",
    "tier": "PRO",
    "payment_url": "https://...",
    "monthly_amount": 299000
  }
}
```

---

### 4.7 Loyalty

#### GET /api/v1/loyalty/points
Get loyalty points.

**Response (200):**
```json
{
  "success": true,
  "data": {
    "points": 2500,
    "lifetime_points": 15000,
    "tier": "SILVER",
    "tier_progress": {
      "current": 15000,
      "next": 20000,
      "percentage": 75
    }
  }
}
```

---

#### GET /api/v1/loyalty/rewards
Get available rewards.

**Response (200):**
```json
{
  "success": true,
  "data": {
    "rewards": [
      {
        "id": "uuid",
        "name": "100 Credits",
        "type": "CREDIT",
        "points_cost": 500,
        "image_url": "https://..."
      },
      {
        "id": "uuid",
        "name": "10% Off Coupon",
        "type": "VOUCHER",
        "points_cost": 1000,
        "image_url": "https://..."
      }
    ]
  }
}
```

---

#### POST /api/v1/loyalty/redeem
Redeem reward.

**Request:**
```json
{
  "reward_id": "uuid"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "redemption_id": "uuid",
    "points_spent": 500,
    "code": "CREDIT100-ABC123",
    "expires_at": "2024-02-15T23:59:59Z",
    "credits_added": 100
  }
}
```

---

## 5. Payment Integration

### 5.1 VNPay Payment Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    VNPAY FLOW                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  1. User selects payment method VNPay                                              │
│                                                                                      │
│  2. App creates order & requests payment URL                                        │
│     POST /api/v1/orders                                                             │
│                                                                                      │
│  3. Backend creates order, generates VNPay URL                                      │
│     Returns payment_url to app                                                      │
│                                                                                      │
│  4. App opens VNPay payment page (in-app browser/webview)                          │
│                                                                                      │
│  5. User completes payment on VNPay                                                 │
│                                                                                      │
│  6. VNPay returns to app via return_url with result                                 │
│     ?vnp_ResponseCode=00&vnp_TxnRef=ORD-xxx                                        │
│                                                                                      │
│  7. App shows success/failure                                                       │
│                                                                                      │
│  8. VNPay sends IPN (Instant Payment Notification) to backend                       │
│     POST /api/v1/payments/vnpay/ipn                                                 │
│     Backend confirms payment & credits account                                       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Payment Return Handling

```javascript
// App handles return URL
const handleVNPayReturn = async (queryParams) => {
  const { vnp_ResponseCode, vnp_TxnRef, vnp_TransactionNo } = queryParams;
  
  if (vnp_ResponseCode === '00') {
    // Payment successful
    // Verify with backend
    const response = await fetch('/api/v1/payments/verify', {
      method: 'POST',
      body: JSON.stringify({
        order_id: vnp_TxnRef,
        transaction_no: vnp_TransactionNo,
        provider: 'VNPAY'
      })
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Update balance, show success
      dispatch(updateBalance(result.data.new_balance));
      showSuccessModal();
    }
  } else {
    // Payment failed
    showErrorModal(getVNPayErrorMessage(vnp_ResponseCode));
  }
};
```

---

## 6. Error Codes

### 6.1 Authentication Errors

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| `AUTH_PHONE_INVALID` | 400 | Invalid phone number | Phone format is invalid |
| `AUTH_OTP_NOT_FOUND` | 404 | OTP expired or not found | OTP has expired or never existed |
| `AUTH_OTP_INVALID` | 400 | Invalid OTP | OTP code is incorrect |
| `AUTH_OTP_EXPIRED` | 400 | OTP expired | OTP has expired (5 min) |
| `AUTH_OTP_MAX_ATTEMPTS` | 429 | Too many attempts | 3 attempts exceeded |
| `AUTH_TOKEN_INVALID` | 401 | Invalid token | JWT token is invalid |
| `AUTH_TOKEN_EXPIRED` | 401 | Token expired | JWT token has expired |
| `AUTH_REFRESH_FAILED` | 401 | Refresh failed | Refresh token invalid/expired |

### 6.2 Credit Errors

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| `CREDIT_INSUFFICIENT` | 402 | Insufficient credits | Not enough credits for operation |
| `CREDIT_LIMIT_EXCEEDED` | 429 | Rate limit exceeded | Daily/monthly limit reached |
| `CREDIT_DEDUCTION_FAILED` | 500 | Deduction failed | Internal error |

### 6.3 AI Errors

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| `AI_MODEL_UNAVAILABLE` | 400 | Model unavailable | Selected model not available |
| `AI_PROVIDER_ERROR` | 502 | Provider error | External AI provider error |
| `AI_RATE_LIMIT` | 429 | Rate limited | Too many requests to AI |
| `AI_CONTENT_FILTERED` | 400 | Content filtered | Request violates policy |

### 6.4 Order Errors

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| `ORDER_NOT_FOUND` | 404 | Order not found | Order ID does not exist |
| `ORDER_ALREADY_PAID` | 400 | Already paid | Order has been paid |
| `ORDER_PAYMENT_FAILED` | 402 | Payment failed | Payment processing failed |
| `COUPON_INVALID` | 400 | Invalid coupon | Coupon code not valid |
| `COUPON_EXPIRED` | 400 | Coupon expired | Coupon has expired |
| `COUPON_MAX_USED` | 400 | Coupon limit reached | Coupon usage limit exceeded |

---

## 7. Rate Limits

### 7.1 Limits by Tier

| Tier | Chat (RPM) | Image (RPM) | Video (RPH) | Concurrent |
|------|------------|-------------|-------------|------------|
| Basic | 10 | 5 | 2 | 2 |
| Developer | 30 | 15 | 6 | 5 |
| Pro | 60 | 30 | 12 | 10 |
| Builder | 120 | 60 | 24 | 20 |

### 7.2 Rate Limit Headers

```http
HTTP/1.1 200 OK
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1705312260
X-RateLimit-Window: 60
```

### 7.3 Rate Limit Exceeded Response

```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please wait 30 seconds.",
    "retry_after": 30
  }
}
```

---

## 8. Webhook Events

### 8.1 Payment Webhook

**Endpoint:** POST /api/v1/webhooks/payment

```json
{
  "event": "payment.completed",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "payment_id": "uuid",
    "order_id": "uuid",
    "provider": "VNPAY",
    "amount": 99000,
    "currency": "VND",
    "status": "SUCCESS",
    "transaction_no": "123456",
    "paid_at": "2024-01-15T10:29:55Z"
  },
  "signature": "sha256=..."
}
```

### 8.2 Credit Webhook

**Endpoint:** POST /api/v1/webhooks/credit

```json
{
  "event": "credit.low",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "user_id": "uuid",
    "balance": 100,
    "threshold": 500,
    "tier": "DEVELOPER"
  },
  "signature": "sha256=..."
}
```

### 8.3 Subscription Webhook

**Endpoint:** POST /api/v1/webhooks/subscription

```json
{
  "event": "subscription.renewed",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "user_id": "uuid",
    "membership_id": "uuid",
    "tier": "PRO",
    "period_start": "2024-02-01T00:00:00Z",
    "period_end": "2024-03-01T00:00:00Z",
    "credits_granted": 10000
  },
  "signature": "sha256=..."
}
```

---

## 9. SDK Examples

### 9.1 JavaScript/TypeScript SDK

```typescript
import { AICafeClient } from '@aicafe/sdk';

const client = new AICafeClient({
  apiKey: 'your-api-key',
  baseUrl: 'https://api.aicafe.vn'
});

// Authentication
await client.auth.sendOTP('+84XXXXXXXXX');
const result = await client.auth.verifyOTP('+84XXXXXXXXX', '123456');

// Chat
const response = await client.ai.chat({
  model: 'gpt-4o',
  messages: [
    { role: 'user', content: 'Hello!' }
  ]
});

// Streaming chat
const stream = await client.ai.chatStream({
  model: 'gpt-4o',
  messages: [{ role: 'user', content: 'Write a story...' }]
});

for await (const chunk of stream) {
  if (chunk.type === 'chunk') {
    process.stdout.write(chunk.delta);
  } else if (chunk.type === 'done') {
    console.log('\nDone! Used', chunk.credits_used, 'credits');
  }
}

// Image generation
const image = await client.ai.image({
  model: 'dall-e-3',
  prompt: 'A sunset',
  size: '1024x1024'
});

// Credits
const balance = await client.credits.getBalance();
console.log('Balance:', balance.credits);

// Orders
const orders = await client.orders.list({ status: 'COMPLETED' });

// Membership
const membership = await client.membership.get();

// Loyalty
const loyalty = await client.loyalty.getPoints();
const rewards = await client.loyalty.getRewards();
await client.loyalty.redeem({ reward_id: 'uuid' });
```

### 9.2 Python SDK

```python
from aicafe import AICafeClient

client = AICafeClient(api_key="your-api-key")

# Authentication
client.auth.send_otp("+84XXXXXXXXX")
result = client.auth.verify_otp("+84XXXXXXXXX", "123456")

# Chat
response = client.ai.chat(
    model="gpt-4o",
    messages=[{"role": "user", "content": "Hello!"}]
)

# Streaming
for chunk in client.ai.chat_stream(
    model="gpt-4o",
    messages=[{"role": "user", "content": "Write a story..."}]
):
    if chunk["type"] == "chunk":
        print(chunk["delta"], end="", flush=True)
    elif chunk["type"] == "done":
        print(f"\nUsed {chunk['credits_used']} credits")

# Image
image = client.ai.image(
    model="dall-e-3",
    prompt="A sunset",
    size="1024x1024"
)

# Credits
balance = client.credits.get_balance()
print(f"Balance: {balance.credits}")
```

### 9.3 Go SDK

```go
package main

import (
    "fmt"
    "github.com/aicafe/go-sdk"
)

func main() {
    client := aicafe.NewClient("your-api-key")
    
    // Authentication
    client.Auth.SendOTP("+84XXXXXXXXX")
    result, err := client.Auth.VerifyOTP("+84XXXXXXXXX", "123456")
    
    // Chat
    resp, err := client.AI.Chat(&aicafe.ChatRequest{
        Model: "gpt-4o",
        Messages: []aicafe.Message{
            {Role: "user", Content: "Hello!"},
        },
    })
    fmt.Println(resp.Choices[0].Message.Content)
    
    // Stream
    stream, err := client.AI.ChatStream(&aicafe.ChatRequest{
        Model: "gpt-4o",
        Messages: []aicafe.Message{
            {Role: "user", Content: "Write a story"},
        },
    })
    for chunk := range stream.Chunks() {
        fmt.Print(chunk.Delta)
    }
    
    // Balance
    balance, _ := client.Credits.GetBalance()
    fmt.Printf("Balance: %d\n", balance.Credits)
}
```

---

## 10. API Versioning

### 10.1 Version Header

```http
GET /api/v1/users/me HTTP/1.1
Host: api.aicafe.vn
Authorization: Bearer {token}
API-Version: 2024-01-15
```

### 10.2 Deprecation Headers

```http
HTTP/1.1 200 OK
X-API-Deprecation: true
X-API-Sunset: Sat, 01 Jun 2024 00:00:00 GMT
X-API-Removal: Use /api/v2/users/me instead
```

### 10.3 Changelog Headers

```http
HTTP/1.1 200 OK
X-API-Changelog: https://docs.aicafe.vn/changelog
```
