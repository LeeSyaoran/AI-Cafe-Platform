# AI Café - Membership

## Overview

Membership tạo retention bằng cách cung cấp giá trị liên tục, không chỉ đơn giản là bán Credit rẻ hơn.

## Membership Principle

**Không nên có:**
```
Unlimited AI
```

**Nên có:**
```
Monthly Credits
+ Discount
+ Workspace
+ AI Access
```

## Membership Tiers

### Student - 99k/month

| Benefit | Value |
|---------|-------|
| Credits/month | 2,000 |
| Drink discount | 5% |
| AI tier | Economy |
| Workspace | 1h/day |

### Developer - 199k/month

| Benefit | Value |
|---------|-------|
| Credits/month | 5,000 |
| Drink discount | 10% |
| AI tier | Standard |
| Workspace | 2h/day |
| AI upgrades | Included |

### Pro - 399k/month

| Benefit | Value |
|---------|-------|
| Credits/month | 15,000 |
| Drink discount | 15% |
| AI tier | Premium |
| Workspace | 4h/day |
| Video quota | 3/day |
| Priority queue | Yes |

## Membership vs Drink

| Aspect | Single Drink | Membership |
|--------|-------------|------------|
| Commitment | One-time | Monthly |
| Price | 30-120k | 99-399k/month |
| Best for | Occasional | Regular customers |
| Flexibility | High | Medium |
| Value | Burst of AI | Continuous access |

## Membership Flow

```
Subscribe
   │
   ▼
Payment
   │
   ▼
Activate Membership
   │
   ▼
Monthly Credits
   │
   ▼
Discount on drinks
   │
   ▼
Workspace access
   │
   ▼
AI Access
   │
   ▼
Renew or Expire
```

## Subscription Management

```sql
memberships
- id
- user_id
- tier (student/developer/pro)
- started_at
- next_billing_date
- status (active/paused/cancelled)
- auto_renew

membership_transactions
- id
- membership_id
- type (payment/refund/upgrade/downgrade)
- amount
- credits_added
- discount_percent
- created_at
```

## Conversion Metrics

| Metric | Target |
|--------|--------|
| Drink → Membership | 10-15% |
| Member retention | 80%+ |
| Average membership duration | 6+ months |
| Upgrade rate | 5%/year |

## Membership Benefits Beyond Credits

1. **Consistent AI access** - Không phải suy nghĩ mỗi lần
2. **Habit formation** - Dễ tạo thói quen
3. **Community** - Member-only features
4. **Priority** - Early access to new features
5. **Feedback** - Direct line to product team

## Member Experience

```
┌─────────────────────────────────────┐
│       DEVELOPER MEMBER              │
├─────────────────────────────────────┤
│ Status: Active                      │
│ Renews: October 15, 2024             │
│                                     │
│ This month:                         │
│ ████████████░░░░  4,250 / 5,000     │
│ Credits used                        │
│                                     │
│ ┌─────────────────────────────┐    │
│ │ 10% off all drinks           │    │
│ │ 2h workspace daily          │    │
│ │ Standard AI access          │    │
│ └─────────────────────────────┘    │
└─────────────────────────────────────┘
```
