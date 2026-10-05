# AI Café - Loyalty Program

## Overview

Loyalty program tạo retention loop thay vì chỉ thưởng bằng tiền.

## Loyalty Events

```sql
loyalty_events
- id
- user_id
- event_type (visit/order/upgrade/referral)
- points_earned
- reward_granted
- order_id
- created_at
```

## Reward Types

Thay vì chỉ giảm giá, loyalty có thể dùng:

| Type | Example | Value |
|------|---------|-------|
| Credits | +500 Credits | AI usage |
| Model upgrade | Premium Trial | Better AI |
| Workspace | +30 minutes | Extended access |
| Discount | 10% off | Money savings |
| Free drink | Complimentary | Physical reward |

## Visit-based Rewards

```
Visit 1   → +500 Credits
Visit 5   → +1,000 Credits
Visit 10  → Premium Upgrade (1 day)
Visit 20  → +1h Workspace
Visit 50  → Free Basic Drink
```

## Retention Loop

```
                  FIRST VISIT
                      │
                      ▼
                Bonus Credits
                      │
                      ▼
                   TRY AI
                      │
                      ▼
              Developer Drink
                      │
                      ▼
                  Loyalty
                      │
                      ▼
                 Membership
                      │
                      ▼
                 More Usage
                      │
                      ▼
                More Visits
                      │
                      ▼
                 ┌────┴────┐
                 │  LOOP   │
                 └─────────┘
```

## Referral Program

**Khi khách A giới thiệu khách B:**

| Person | Reward | Condition |
|--------|--------|-----------|
| A | +1,000 Credits | B mua lần đầu |
| B | +1,000 Credits | Mua lần đầu |

**Giới hạn:**
- Maximum 5 referral bonuses/month

## New Customer Offer

**Lần đầu mua đồ uống:**
```
+1,000 Bonus Credits
```

Bonus có thời hạn: 7 days

**Flow:**
```
First Visit
     │
     ▼
Try AI
     │
     ▼
Credit remains
     │
     ▼
Return
     │
     ▼
Buy Drink
```

## Off-Peak Promotion

Khuyến khích khách đến vào giờ thấp điểm:

**14:00 - 17:00: Developer Hours**

Thay vì giảm giá sâu:

| Promotion | Value |
|-----------|-------|
| +20% Credits | Extra AI |
| +30 minutes Workspace | Extra time |
| Free AI Upgrade | Better access |

## Tier-based Loyalty

```
Basic → Silver → Gold → Platinum
  │       │       │       │
  │       │       │       ├── Exclusive events
  │       │       │       ├── Priority support
  │       │       │       └── Best discounts
  │       │       └── Monthly bonus
  │       └── Quarterly bonus
  └── Welcome bonus
```

## Points Expiration

```
Points expire: 12 months from earned
Membership points: Never expire while active
```

## Dashboard

```
┌─────────────────────────────────────┐
│       YOUR LOYALTY                  │
├─────────────────────────────────────┤
│ Level: Gold                         │
│ Points: 12,500                      │
│ Visits this month: 8                 │
│                                     │
│ Progress to Platinum:               │
│ ████████████████░░░░░░  80%         │
│ 8 more visits to unlock!            │
│                                     │
│ Recent rewards:                    │
│ • +500 Credits (Visit 8)           │
│ • Premium Day (Visit 10)            │
│ • +1000 Credits (Referral)         │
└─────────────────────────────────────┘
```
