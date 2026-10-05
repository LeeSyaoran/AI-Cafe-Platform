# AI Café - Credit Engine

## Overview

Credit là đơn vị khách hàng nhìn thấy. Backend dùng Token/Usage để tính chi phí thực.

## Three-Layer System

### 1. Credit (Customer-facing)

Đơn vị khách hàng nhìn thấy và sử dụng.

```
5,000 AI Credits
```

**Credit dùng cho:**
- Chat
- Coding
- Reasoning
- Image
- Video
- AI services

> Credit không phải tiền mặt.

### 2. Token / Usage (Backend)

Đơn vị backend dùng để tính chi phí thực tế.

**Text:**
- Input Tokens
- Output Tokens
- Cached Tokens

**Image:**
- Resolution
- Generation Units

**Video:**
- Duration
- Resolution
- Generation Units

**Audio:**
- Duration

### 3. Workspace Time

Kiểm soát thời gian sử dụng môi trường làm việc.

| Tier | Workspace Time |
|------|----------------|
| Basic | 1h |
| Developer | 2h |
| Pro | 4h |
| Builder | 8h |

**Credit và Time độc lập:**
- Credits = AI usage
- Time = Workspace usage

## Credit Wallet

```
┌─────────────────────────┐
│       AI WALLET         │
├─────────────────────────┤
│ Credits:      8,450     │
│ Workspace:    2h 35m    │
│ Tier:         Developer │
└─────────────────────────┘
```

## Credit Ledger

Lưu lịch sử giao dịch:

```sql
credit_transactions
- id
- user_id
- type (EARN/SPEND/BONUS/REFUND/EXPIRE/ADJUSTMENT)
- amount
- source
- order_id
- service
- model
- usage
- expires_at
- created_at
```

**Ví dụ:**
```
+5,000  Developer Drink
-200    Coding
-500    Reasoning
-1,500  Video
+5,000  Developer Drink
```

## Credit Expiration

Credit có thể có thời hạn:

| Type | Expiration |
|------|------------|
| Normal Credit | 30 days |
| Promotional Credit | 7 days |

**Hiển thị:**
```
2,000 Credits  Expires: 12 Oct
6,450 Credits  Expires: 30 Oct
```

## Daily Limit

Ngoài Credit balance, cần Daily Limit.

```
Wallet:        20,000 Credits
Daily limit:    5,000 Credits

Remaining today: 200 Credits
```

Ngày tiếp theo reset.

## Soft Limit / Hard Limit

**80%**
```
You've used 80% of today's AI allowance.
```

**100%**
```
You've reached today's AI limit.
CTA: [ Buy Another Drink ]
```

## Model Multiplier

Mỗi model có hệ số Credit.

| Model | Multiplier | Request: 10 Credits |
|-------|------------|---------------------|
| Economy | x1 | 10 |
| Standard | x2 | 20 |
| Premium | x5 | 50 |
| Ultra | x10 | 100 |

## Video Economics

Video có chi phí cao - cần kiểm soát:

| Duration | Credits |
|----------|---------|
| 5s | 1,500 |
| 10s | 3,000 |
| 20s | 6,000 |

**Video Quota:**

| Tier | Daily Limit |
|------|-------------|
| Basic | 0/day |
| Developer | 1/day |
| Pro | 3/day |
| Builder | 10/day |

## Credits Issued vs AI Cost

```
100 customers x 5,000 Credits = 500,000 Credits issued
                                    │
65% active users                    │
    │                               │
274,000 Credits consumed            │
    │                               │
Actual Provider Usage                │
    │                               │
420,000đ provider cost              │
```

> Actual Provider Cost là KPI quan trọng hơn Credits Issued.
