# AI Café Platform - Frontend Requirements Document

> Tài liệu mô tả các luồng nghiệp vụ và thông tin hiển thị cho Frontend

---

## Mục lục

1. [Tổng quan](#1-tổng-quan)
2. [Luồng Authentication](#2-luồng-authentication)
3. [Luồng Wallet/Credits](#3-luồng-walletcredits)
4. [Luồng AI Tools](#4-luồng-ai-tools)
5. [Luồng Orders](#5-luồng-orders)
6. [Luồng Membership](#6-luồng-membership)
7. [Luồng Workspace](#7-luồng-workspace)
8. [Luồng Admin Dashboard](#8-luồng-admin-dashboard)
9. [Bảng thông tin hiển thị](#9-bảng-thông-tin-hiển-thị)

---

## 1. Tổng quan

### 1.1 Hệ thống AI Café Platform

AI Café Platform là nền tảng kết hợp quán cà phê với dịch vụ AI, cho phép người dùng:
- Sử dụng các công cụ AI (Chat, Code, Image, Video)
- Quản lý credits và membership
- Tích điểm loyalty

### 1.2 Base URLs

```
Production: https://api.aicafe.vn/v1
Staging:    https://api-staging.aicafe.vn/v1
Local:      http://localhost:8080/api/v1
```

### 1.3 Authentication Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

### 1.4 Membership Tiers

| Tier | Credits/Tháng | Workspace Phút | Rate Limit |
|------|---------------|----------------|------------|
| **BASIC** | 2,000 | 60 | 30 req/min |
| **DEVELOPER** | 5,000 | 120 | 100 req/min |
| **PRO** | 10,000 | 240 | 300 req/min |
| **BUILDER** | 18,000 | 480 | 1000 req/min |

### 1.5 Credit Pricing

| Service | Model | Credits |
|---------|-------|---------|
| **Chat** | GPT-4o | 200/1K tokens |
| | Claude Sonnet 4 | 150/1K tokens |
| | Gemini 1.5 Pro | 100/1K tokens |
| **Image** | DALL-E 3 | 50/ảnh |
| | Stable Diffusion | 10/ảnh |
| **Video** | Sora | 100/giây |
| | Stable Video | 50/giây |

---

## 2. Luồng Authentication

### 2.1 Đăng nhập bằng OTP

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│  User   │                    │   FE    │                    │   BE    │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  Nhập số điện thoại        │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  POST /auth/login            │
     │                              │  {phone}                   │
     │                              │────────────────────────────>
     │                              │                              │
     │                              │  200: {message}            │
     │                              │<─────────────────────────────
     │                              │                              │
     │  Hiển thị input OTP         │                              │
     │<─────────────────────────────│                              │
     │                              │                              │
     │  Nhập mã OTP 6 số          │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  POST /auth/verify-otp       │
     │                              │  {phone, code}              │
     │                              │────────────────────────────>
     │                              │                              │
     │                              │  200: {tokens, user}        │
     │                              │<─────────────────────────────
     │                              │                              │
     │  Lưu tokens, chuyển trang   │                              │
     │<─────────────────────────────│                              │
```

### 2.2 Thông tin đăng nhập hiển thị

**Màn hình nhập phone:**
- Input: Số điện thoại (+84...)
- Button: "Gửi mã OTP"
- Thông báo: "Mã OTP đã được gửi đến số điện thoại của bạn"

**Màn hình nhập OTP:**
- Input: 6 ô nhập mã OTP (có thể nhập liền 1 ô)
- Countdown: 5:00 (đếm ngược)
- Button: "Xác thực"
- Link: "Gửi lại mã" (sau khi countdown = 0)

**Sau khi đăng nhập thành công:**
- Lưu tokens (access_token, refresh_token)
- Chuyển hướng đến Dashboard

### 2.3 Token Refresh

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │                    │ Storage │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  Gọi API với access_token   │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │  Response: 401 Token Expired │                              │
     │<─────────────────────────────│                              │
     │                              │                              │
     │  POST /auth/refresh          │                              │
     │  {refresh_token}            │                              │
     │─────────────────────────────>│                              │
     │                              │  Validate refresh_token       │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  200: {new_tokens}          │
     │                              │<──────────────────────────────
     │                              │                              │
     │  Lưu tokens mới              │                              │
     │──────────────────────────────┼──────────────────────────────>
     │                              │                              │
     │  Retry request gốc           │                              │
     │─────────────────────────────>│                              │
```

### 2.4 Thông tin user sau login

```typescript
interface User {
  id: string;           // UUID
  phone: string;        // +84912345678
  name: string;
  email?: string;
  avatar_url?: string;
  tier: 'BASIC' | 'DEVELOPER' | 'PRO' | 'BUILDER';
  status: 'ACTIVE' | 'SUSPENDED' | 'DELETED';
  created_at: string;   // ISO timestamp
}
```

---

## 3. Luồng Wallet/Credits

### 3.1 Xem số dư Credits

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │                    │   DB    │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  GET /api/v1/users/me/wallet│                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  Query wallet balance         │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  Return: {credits, minutes}  │
     │                              │<───────────────────────────────
     │                              │                              │
     │  200: Wallet data           │                              │
     │<─────────────────────────────│                              │
```

### 3.2 Thông tin Wallet hiển thị

```typescript
interface Wallet {
  credits: number;             // Số dư credits hiện tại
  workspace_minutes: number;   // Phút workspace còn lại
  tier: string;                // BASIC | DEVELOPER | PRO | BUILDER
  daily_limit_remaining: number; // Credits còn lại trong ngày
}
```

**Màn hình Wallet (Dashboard Card):**

```
┌─────────────────────────────────────────┐
│  💰 Credits                    🎯 Tier  │
│  ─────────────────────────────────────  │
│                                         │
│      1,500          →        4,500      │
│     Total               Daily Limit      │
│                                         │
│  ████████████████░░░░░░░░░░░░░  Used   │
│  Remaining today: 4,500                 │
└─────────────────────────────────────────┘
```

### 3.3 Xem lịch sử giao dịch Credits

```
┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │
└────┬────┘                    └────┬────┘
     │                              │
     │  GET /api/v1/credits/transactions│
     │  ?type=&from=&to=&page=       │
     │───────────────────────────────>
     │                              │
     │  200: {transactions, pagination}│
     │<───────────────────────────────
```

**Dữ liệu transactions:**

```typescript
interface CreditTransaction {
  id: string;
  type: 'PURCHASE' | 'DEDUCT' | 'REFUND' | 'BONUS' | 'EXPIRE';
  amount: number;            // Số credits (+/-)
  balance_after: number;    // Số dư sau giao dịch
  source: string;           // ORDER | SUBSCRIPTION | PROMO
  service?: string;        // CHAT_GPT4 | CLAUDE | DALL_E | SORA
  description: string;
  created_at: string;
}
```

**Màn hình Transaction History:**

```
┌─────────────────────────────────────────┐
│  Lịch sử giao dịch                     │
│  ─────────────────────────────────────  │
│                                         │
│  Hôm nay                                │
│  ┌───────────────────────────────────┐  │
│  │ 🟢 +500      Mua credits       10:30│
│  │ 🔴 -150     Chat GPT-4o       10:25│
│  │ 🔴 -200     Code Generation    10:20│
│  │ 🔴 -50      Image DALL-E 3     10:15│
│  └───────────────────────────────────┘  │
│                                         │
│  Hôm qua                                │
│  ┌───────────────────────────────────┐  │
│  │ 🔴 -100     Chat Claude       15:30│
│  │ 🟢 +1000    Mua credits        09:00│
│  └───────────────────────────────────┘  │
│                                         │
│  [Xem thêm]                            │
└─────────────────────────────────────────┘
```

### 3.4 Credits sắp hết hạn

```
GET /api/v1/credits/expiration
Response:
  expiring: [{
    credits: number;
    expires_at: timestamp;
  }]
```

**Màn hình cảnh báo:**

```
┌─────────────────────────────────────────┐
│  ⚠️ Credits sắp hết hạn                 │
│  ─────────────────────────────────────  │
│                                         │
│  Cảnh báo: 500 credits sẽ hết hạn      │
│  trong 7 ngày tới                      │
│                                         │
│  [Xem chi tiết]                        │
└─────────────────────────────────────────┘
```

---

## 4. Luồng AI Tools

### 4.1 Chat với AI

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │                    │  AI     │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  Chọn model (tùy chọn)       │                              │
     │  Nhập tin nhắn              │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  POST /api/v1/ai/chat         │
     │                              │  {message, model?}           │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  Route to optimal AI model    │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  AI Response                 │
     │                              │<───────────────────────────────
     │                              │                              │
     │                              │  Deduct credits              │
     │                              │───────────────────────────────
     │                              │                              │
     │  200: {message, credits_used}│
     │<───────────────────────────────│                              │
```

**Request:**
```typescript
interface ChatRequest {
  message: string;
  model?: string;      // optional, auto-select if omitted
  stream?: boolean;    // true for streaming
}
```

**Response:**
```typescript
interface ChatResponse {
  id: string;
  message: string;
  model: string;
  credits_used: number;
  tokens: {
    input: number;
    output: number;
  };
}
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  AI Chat                                           [Model ▼]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 👤 User                                               │   │
│  │ Viết code React component cho button                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 🤖 AI                                    12:34         │   │
│  │ Đây là code button component:                          │   │
│  │                                                        │   │
│  │ ```tsx                                                  │   │
│  │ const Button = ({...}) => {                           │   │
│  │   return <button>{children}</button>                   │   │
│  │ };                                                     │   │
│  │ ```                                                    │   │
│  │ Credits: 150                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────┐ [Send]              │
│  │ Type your message...                  │                      │
│  └─────────────────────────────────────┘                      │
│                                                                 │
│  Model: [GPT-4o ▼]  │  Stream: [ON]                          │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Code Generation

```
POST /api/v1/ai/code
Body:
  prompt: string;
  language?: string;
  model?: string;

Response:
  code: string;
  language: string;
  credits_used: number;
```

### 4.3 Image Generation

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │                    │  AI     │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  Nhập prompt                │                              │
     │  Chọn size, quality         │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  POST /api/v1/ai/image      │
     │                              │  {prompt, size, quality}    │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  Call DALL-E / SDXL          │
     │                              │───────────────────────────────
     │                              │                              │
     │                              │  Return image URL            │
     │                              │<───────────────────────────────
     │                              │                              │
     │                              │  Deduct credits              │
     │                              │───────────────────────────────
     │                              │                              │
     │  200: {url, revised_prompt} │                              │
     │<───────────────────────────────│                              │
```

**Request:**
```typescript
interface ImageRequest {
  prompt: string;
  size?: '1024x1024' | '1792x1024' | '1024x1792';
  quality?: 'standard' | 'hd';
}
```

**Response:**
```typescript
interface ImageResponse {
  id: string;
  model: string;
  images: [{
    url: string;
    revised_prompt: string;
  }];
  credits_used: number;
  credits_remaining: number;
}
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  AI Image Generator                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │                    [Generated Image]                    │   │
│  │                      1024 x 1024                        │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Prompt: "A beautiful sunset over the ocean..."               │
│  Revised: "A realistic photograph of a sunset..."              │
│                                                                 │
│  Credits: 50 | Remaining: 1,450                               │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  Prompt:                                                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ A beautiful sunset over the ocean with palm trees...   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Size: [1024x1024 ▼]   Quality: [Standard ▼]   [Generate]      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.4 Video Generation

```
POST /api/v1/ai/video
Body:
  prompt: string;
  duration?: 5 | 10 | 20;    // seconds
  resolution?: '720p' | '1080p';

Response:
  id: string;
  status: 'processing' | 'ready';
  url?: string;
  credits_used: number;
  quota_remaining: number;
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  AI Video Generator                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Status: ⏳ Đang xử lý...                                      │
│  [████████████░░░░░░░░░░░░] 60%                               │
│                                                                 │
│  Hoặc:                                                          │
│                                                                 │
│  Status: ✅ Sẵn sàng                                            │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    [Video Player]                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│  [Download]  [Share]                                           │
│                                                                 │
│  Credits: 500 | Remaining: 1,000                               │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  Prompt:                                                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ A beautiful sunset over the ocean...                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Duration: [10s ▼]   Resolution: [720p ▼]   [Generate]        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.5 Model Selection

```
GET /api/v1/ai/models
Response:
  chat: [{
    id: string;
    name: string;
    provider: string;
    description: string;
    credits_per_1k_tokens: number;
    max_tokens: number;
    is_available: boolean;
  }]
  image: [...]
  video: [...]
```

---

## 5. Luồng Orders

### 5.1 Tạo đơn hàng mua Credits

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │                    │ Payment │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  Chọn gói credits           │                              │
     │  Chọn phương thức thanh toán│                              │
     │─────────────────────────────>│                              │
     │                              │                              │
     │                              │  POST /api/v1/orders         │
     │                              │  {items, payment_method}   │
     │                              │──────────────────────────────>
     │                              │                              │
     │                              │  Create order               │
     │                              │  Generate payment URL       │
     │                              │──────────────────────────────>
     │                              │                              │
     │  200: {order_id, payment_url}│                              │
     │<───────────────────────────────│                              │
     │                              │                              │
     │  Redirect to payment          │                              │
     │─────────────────────────────>│                              │
     │                              │                              │
```

**Request:**
```typescript
interface CreateOrderRequest {
  items: [{
    product_id: string;
    quantity: number;
  }];
  payment_method: 'VNPAY' | 'MOMO' | 'ZALOPAY' | 'CASH';
  coupon_code?: string;
  idempotency_key: string;  // để tránh tạo duplicate order
}
```

**Response:**
```typescript
interface CreateOrderResponse {
  order_id: string;
  order_number: string;
  status: 'PENDING';
  items: [{
    product_id: string;
    name: string;
    quantity: number;
    unit_price: number;
    total: number;
  }];
  subtotal: number;
  discount: number;
  tax: number;
  total: number;
  payment_url?: string;     // URL thanh toán VNPay/MoMo
  expires_at: string;       // Order hết hạn sau 15 phút
}
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Mua Credits                                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Chọn gói:                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ 💰 500     │  │ 💰 1,000    │  │ 💰 2,000    │           │
│  │ Credits    │  │ Credits ★   │  │ Credits     │           │
│  │            │  │ (Phổ biến) │  │            │           │
│  │ 150,000đ  │  │ 280,000đ   │  │ 520,000đ   │           │
│  │            │  │            │  │            │           │
│  │ [Chọn]    │  │ [Chọn]     │  │ [Chọn]     │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
│                                                                 │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  Thanh toán:                                                    │
│  ○ VNPay (QR)        ○ MoMo           ○ Thanh toán sau        │
│                                                                 │
│  Coupon: [____________] [Áp dụng]                             │
│                                                                 │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  Tạm tính:                                    280,000đ        │
│  Giảm giá:                                      -28,000đ      │
│  VAT (10%):                                     25,200đ        │
│  ─────────────────────────────────────────────────────────────  │
│  Tổng cộng:                                   277,200đ       │
│                                                                 │
│  Credits nhận được: 1,150 (+150 bonus)                        │
│                                                                 │
│  [Thanh toán]                                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 Xem lịch sử đơn hàng

```
GET /api/v1/orders
Query: ?status=&page=&limit=

Response:
  orders: [{
    order_id: string;
    order_number: string;
    type: 'CREDIT_PURCHASE';
    status: 'PENDING' | 'PAID' | 'CANCELLED' | 'REFUNDED';
    total: number;
    credits_added?: number;    // credits nhận được
    payment_method: string;
    paid_at?: string;
    created_at: string;
  }]
  pagination: {total, page, limit, total_pages}
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Đơn hàng của tôi                                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [Tất cả] [Đang xử lý] [Đã thanh toán] [Đã hủy]            │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ #AICA20240115001                    15/01/2024 10:30  │   │
│  │ Mua 1,000 Credits                    Thanh toán thành công│   │
│  │ Thanh toán: VNPay QR                                   │   │
│  │                                    Tổng: 280,000đ      │   │
│  │ [+1,150 Credits]                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ #AICA20240114002                    14/01/2024 09:15  │   │
│  │ Mua 500 Credits                      Đã hủy              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  [< Trang 1 / 3 >]                                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.3 Thanh toán VNPay/MoMo Callback

```
Payment Provider                │    BE
────────────────────────────────┼─────────────────────────────────
                                │
Webhook POST /webhooks/payment   │
{vnp_ResponseCode, vnp_TxnRef}  │
───────────────────────────────>│
                                │
                                │ Validate signature
                                │ Update order status
                                │ Add credits to wallet
                                │───────────────────────────> DB
                                │
                                │ Return RspCode: "00"
                                │<────────────────────────────
                                │
Redirect to FE: /payment/return │
{order_id, status, vnp_ResponseCode}
```

---

## 6. Luồng Membership

### 6.1 Xem Membership hiện tại

```
GET /api/v1/membership
Response:
  tier: string;
  status: 'active' | 'paused' | 'cancelled';
  next_billing: date;
  credits_remaining: number;
  benefits: string[];
  upgrade_available: boolean;
  plans: [{
    id: string;
    name: string;
    price: number;
    interval: 'month';
    benefits: string[];
  }]
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Membership của tôi                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  🎯 DEVELOPER                                           │   │
│  │  ────────────────────────────────────────────────────  │   │
│  │                                                         │   │
│  │  Trạng thái: Active                                    │   │
│  │  Ngày thanh toán tiếp theo: 15/02/2024               │   │
│  │  Credits còn lại: 3,500 / 5,000                      │   │
│  │                                                         │   │
│  │  ████████████████░░░░░░░░░ 70%                        │   │
│  │                                                         │   │
│  │  Quyền lợi:                                           │   │
│  │  ✓ Truy cập tất cả models                             │   │
│  │  ✓ 5,000 credits/tháng                                 │   │
│  │  ✓ Image generation                                    │   │
│  │  ✓ Priority support                                    │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Nâng cấp lên PRO                                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  💎 PRO                          199,000đ/tháng         │   │
│  │                                                         │   │
│  │  • 10,000 credits/tháng (+100%)                       │   │
│  │  • Video generation                                    │   │
│  │  • Priority support                                     │   │
│  │                                                         │   │
│  │                              [Nâng cấp ngay]           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  [Hủy membership]                                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 Đăng ký/Nâng cấp Membership

```
POST /api/v1/membership/subscribe
Body:
  tier: 'DEVELOPER' | 'PRO' | 'BUILDER';
  payment_method: 'VNPAY' | 'MOMO';

Response:
  membership_id: string;
  status: 'active';
  payment_url?: string;
```

### 6.3 Hủy Membership

```
DELETE /api/v1/membership
Response:
  cancelled: true;
  effective_date: date;   // Vẫn dùng được đến cuối chu kỳ
```

---

## 7. Luồng Workspace

### 7.1 Workspace Sessions

Workspace cho phép user sử dụng AI trong thời gian thực với minutes còn lại.

```
┌─────────┐                    ┌─────────┐
│   FE    │                    │   BE    │
└────┬────┘                    └────┬────┘
     │                              │
     │  POST /api/v1/workspace/sessions
     │───────────────────────────────>
     │                              │
     │  200: {                      │
     │    session_id: string;      │
     │    expires_at: timestamp;   │
     │    minutes_remaining: number │
     │  }                          │
     │<───────────────────────────────
     │                              │
     │  Bắt đầu session            │
     │──────────────────────────────>│
     │                              │
     │  Deduct minutes theo thời gian│
     │<───────────────────────────────│
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  AI Workspace                                      [Kết thúc]   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ⏱️ Thời gian còn lại: 2h 35m                                 │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ┌─────────────────────────────────────────────────┐ │   │
│  │  │                                                 │ │   │
│  │  │              Code Editor                        │ │   │
│  │  │                                                 │ │   │
│  │  │  const App = () => {                           │ │   │
│  │  │    return <div>Hello AI Café</div>;            │ │   │
│  │  │  };                                             │ │   │
│  │  │                                                 │ │   │
│  │  └─────────────────────────────────────────────────┘ │   │
│  │                                                         │   │
│  │  ┌─────────────────────────────────────────────────┐ │   │
│  │  │  🤖 AI Assistant                                │ │   │
│  │  │  Tôi đã thêm code cho component của bạn.        │ │   │
│  │  │  [Apply] [Copy] [Ask more]                      │ │   │
│  │  └─────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 Kết thúc Session

```
DELETE /api/v1/workspace/sessions/{id}
Response:
  session_closed: true;
  minutes_used: number;
```

---

## 8. Luồng Admin Dashboard

### 8.1 Dashboard Overview

```
GET /api/v1/admin/dashboard
Response:
  today: {
    customers: number;       // Số khách hàng hôm nay
    revenue: number;          // Doanh thu hôm nay
    ai_cost: number;          // Chi phí AI hôm nay
  }
  this_month: {
    revenue: number;
    ai_cost: number;
  }
  credits: {
    issued: number;           // Credits đã phát hành
    used: number;             // Credits đã sử dụng
    expired: number;          // Credits đã hết hạn
  }
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Admin Dashboard                                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  HÔM NAY                        THÁNG NÀY                      │
│  ┌──────────────────────┐      ┌──────────────────────┐        │
│  │ 👥 Khách hàng        │      │ 💰 Doanh thu         │        │
│  │      156             │      │   45,230,000đ       │        │
│  │   (+12% vs hôm qua)  │      │  (+8% vs tháng trước)│        │
│  └──────────────────────┘      └──────────────────────┘        │
│  ┌──────────────────────┐      ┌──────────────────────┐        │
│  │ 💵 Doanh thu         │      │ 🤖 Chi phí AI       │        │
│  │   2,340,000đ        │      │   12,450,000đ       │        │
│  │   (+5% vs hôm qua)   │      │                      │        │
│  └──────────────────────┘      └──────────────────────┘        │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  CREDITS                                                        │
│  ┌──────────────┬──────────────┬──────────────┐               │
│  │  Đã phát hành│  Đã sử dụng  │  Đã hết hạn   │               │
│  │   1,234,000  │    890,000   │    45,000    │               │
│  └──────────────┴──────────────┴──────────────┘               │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  BIỂU ĐỒ DOANH THU                                            │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │     ▁   ▂   ▅   ▇   █   ▇   ▅   ▂   ▁               │   │
│  │  ────────────────────────────────────────────────      │   │
│  │  T2   T3   T4   T5   T6   T7   CN   T2              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 User Management

```
GET /api/v1/admin/users
Query: ?tier=&page=&limit=

Response:
  users: [{
    id: string;
    phone: string;
    name: string;
    tier: string;
    credits: number;
    created_at: string;
    last_login: string;
  }]
  pagination: {total, page, limit}
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Quản lý người dùng                    [+ Thêm user]          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Filter: [Tier ▼] [Search...]                                  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 👤 Nguyễn Văn A     │ BASIC  │ 1,500 │ 15/01/2024 │    │
│  │    +84912345678      │         │       │             │    │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 👤 Trần Thị B        │DEVELOPER│ 4,200 │ 14/01/2024 │    │
│  │    +84987654321      │         │       │             │    │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 👤 Lê Văn C          │  PRO   │ 8,900 │ 13/01/2024 │    │
│  │    +84123456789      │         │       │             │    │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  [< Trang 1 / 15 >]                                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 8.3 Credit Adjustment

```
POST /api/v1/admin/credits/adjust
Body:
  user_id: string;
  amount: number;     // +/- số credits
  reason: string;

Response:
  success: boolean;
  new_balance: number;
  transaction_id: string;
```

**UI Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Điều chỉnh Credits                                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  User: [Tìm kiếm...]                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ 👤 Nguyễn Văn A (+84912345678)                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Số dư hiện tại: 1,500 credits                               │
│                                                                 │
│  Điều chỉnh:                                                   │
│  ○ Cộng (+)    ○ Trừ (-)                                      │
│  Số lượng: [____________] credits                               │
│                                                                 │
│  Lý do: [________________________________]                      │
│                                                                 │
│                                    [Xác nhận điều chỉnh]        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 8.4 AI Usage Analytics

```
GET /api/v1/admin/ai/usage
Query: ?period=daily|weekly|monthly

Response:
  by_model: [{
    model: string;
    requests: number;
    tokens_used: number;
    cost: number;
  }]
  by_user: [{
    user_id: string;
    requests: number;
    credits_used: number;
  }]
  by_provider: [{
    provider: string;
    cost: number;
  }]
```

---

## 9. Bảng thông tin hiển thị

### 9.1 User Profile

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | User identifier |
| phone | string | Phone number (+84...) |
| name | string | Display name |
| email | string? | Email address |
| avatar_url | string? | Profile image URL |
| tier | enum | BASIC, DEVELOPER, PRO, BUILDER |
| status | enum | ACTIVE, SUSPENDED, DELETED |
| created_at | timestamp | Registration date |

### 9.2 Wallet

| Field | Type | Description |
|-------|------|-------------|
| credits | number | Current balance |
| workspace_minutes | number | Minutes remaining |
| tier | string | Current membership tier |
| daily_limit_remaining | number | Credits left today |

### 9.3 Credit Transaction

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Transaction ID |
| type | enum | PURCHASE, DEDUCT, REFUND, BONUS, EXPIRE |
| amount | number | Credits (+/-) |
| balance_after | number | Balance after transaction |
| source | string | ORDER, SUBSCRIPTION, PROMO, ADMIN |
| service | string? | CHAT_GPT4, CLAUDE, DALL_E, SORA |
| description | string | Human readable description |
| created_at | timestamp | Transaction time |

### 9.4 Order

| Field | Type | Description |
|-------|------|-------------|
| order_id | UUID | Order identifier |
| order_number | string | Human readable (AICA20240115001) |
| type | enum | CREDIT_PURCHASE, MEMBERSHIP_SUBSCRIPTION |
| status | enum | PENDING, PAID, CANCELLED, REFUNDED |
| items | array | Order items |
| subtotal | number | Before discount |
| discount | number | Discount amount |
| tax | number | VAT |
| total | number | Final amount |
| payment_method | string | VNPAY, MOMO, CASH |
| payment_status | enum | PENDING, SUCCESS, FAILED |
| credits_added | number? | Credits received |
| created_at | timestamp | Order time |
| paid_at | timestamp? | Payment time |

### 9.5 Membership

| Field | Type | Description |
|-------|------|-------------|
| tier | enum | BASIC, DEVELOPER, PRO, BUILDER |
| status | enum | ACTIVE, PAUSED, CANCELLED, EXPIRED |
| started_at | timestamp | Subscription start |
| current_period_end | timestamp | Next billing date |
| credits_granted | number | Monthly credits |
| credits_remaining | number | Credits left this period |
| auto_renew | boolean | Auto renewal enabled |

### 9.6 AI Chat Message

| Field | Type | Description |
|-------|------|-------------|
| id | string | Message ID |
| role | enum | user, assistant |
| content | string | Message text |
| model | string | AI model used |
| credits_used | number | Credits deducted |
| tokens | object | {input, output} |
| created_at | timestamp | Message time |

### 9.7 Image Generation

| Field | Type | Description |
|-------|------|-------------|
| id | string | Image ID |
| model | string | Model used (dall-e-3, sdxl) |
| images | array | [{url, revised_prompt}] |
| credits_used | number | Credits deducted |
| credits_remaining | number | Balance after |

### 9.8 Video Generation

| Field | Type | Description |
|-------|------|-------------|
| id | string | Video ID |
| model | string | Model used (sora, stable-video) |
| status | enum | processing, ready, failed |
| url | string? | Video URL (when ready) |
| duration | number | Video length in seconds |
| credits_used | number | Credits deducted |
| quota_remaining | number | Balance after |

### 9.9 Workspace Session

| Field | Type | Description |
|-------|------|-------------|
| session_id | UUID | Session identifier |
| status | enum | active, ended, expired |
| minutes_used | number | Minutes consumed |
| minutes_remaining | number | Minutes left |
| started_at | timestamp | Session start |
| expires_at | timestamp | Session expiry |

---

## 10. Error Handling

### 10.1 Error Codes

| Code | HTTP | Message | Action |
|------|------|---------|--------|
| AUTH_OTP_INVALID | 400 | Mã OTP không đúng | Hiển thị lại input OTP |
| AUTH_OTP_EXPIRED | 400 | Mã OTP đã hết hạn | Cho gửi lại OTP |
| AUTH_TOKEN_INVALID | 401 | Token không hợp lệ | Redirect login |
| AUTH_TOKEN_EXPIRED | 401 | Token đã hết hạn | Auto refresh |
| CREDITS_INSUFFICIENT | 400 | Không đủ credits | Hiển thị mua credits |
| COUPON_INVALID | 400 | Mã giảm giá không hợp lệ | Thông báo user |
| ORDER_NOT_FOUND | 404 | Không tìm thấy đơn hàng | Hiển thị lỗi |
| RATE_LIMIT_EXCEEDED | 429 | Quá nhiều yêu cầu | Disable button, countdown |

### 10.2 Error UI Pattern

```
┌─────────────────────────────────────────────────────────────────┐
│  ⚠️ Không đủ credits                                          │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  Bạn cần thêm 200 credits để tiếp tục.                        │
│                                                                 │
│  Số dư hiện tại: 50 credits                                   │
│  Cần thêm: 200 credits                                         │
│                                                                 │
│  [Mua thêm credits]    [Nạp từ tài khoản]                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 11. Rate Limits

| Tier | AI Requests/min | Orders/min |
|------|----------------|------------|
| BASIC | 30 | 10 |
| DEVELOPER | 100 | 20 |
| PRO | 300 | 30 |
| BUILDER | 1000 | 50 |

**UI khi bị rate limit:**
```
┌─────────────────────────────────────────┐
│  ⏳ Vui lòng đợi 30 giây              │
│  Bạn đã gửi quá nhiều yêu cầu        │
│  [30s]                                  │
└─────────────────────────────────────────┘
```

---

## 12. Recommended Tech Stack

| Category | Technology |
|----------|------------|
| Framework | Next.js 14 (App Router) |
| Styling | Tailwind CSS |
| State | Zustand |
| Forms | React Hook Form + Zod |
| HTTP | Axios + React Query |
| Icons | Lucide React |
| Animation | Framer Motion |

---

*Document for AI Café Platform Frontend Team*
