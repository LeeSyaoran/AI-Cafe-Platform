# AI Café - Employee Benefits

## Overview

Nhân viên cũng được cung cấp AI Wallet như một phần của benefits package.

## Employee AI Wallet

**Không nên:**
```
Unlimited AI
```

**Nên:**
```
Employee AI Wallet
+ Monthly Budget
+ Daily Limit
```

**Ví dụ:**
```
5,000 Credits/month
500 Credits/day
```

## Employee Levels

| Level | AI/month | Workspace |
|-------|----------|-----------|
| Staff | 3k | 10h |
| Developer | 5k | 20h |
| Senior | 8k | 30h |
| AI Specialist | 12k | 40h |

## Tenure-based Growth

AI benefit tăng theo thâm niên:

```
0-3 months   → 3k Credits
3-12 months  → 5k Credits
12+ months  → 8k Credits
```

## Beyond AI

Employee benefits không chỉ là AI:

```
AI Wallet
    │
    ├── Training
    ├── Workspace
    ├── Discount đồ uống
    ├── AI tool access
    ├── Bonus
    └── Career progression
```

## Development Wallet

Tách hai loại wallet:

### Personal AI Wallet
AI cá nhân, dùng cho mục đích riêng.

### Work AI Wallet
AI phục vụ công việc của quán:
- Marketing
- Coding
- Design
- Video
- Automation
- Operations

> Work AI không trừ vào Personal AI Wallet.

## AI Employee Testing Team

Nhân viên có thể được sử dụng model mới trong budget.

**Sau đó đánh giá:**
- Quality
- Speed
- Cost
- Use Case
- Reliability

Nhân viên trở thành một phần của quá trình đánh giá AI platform.

## Employee Retention Loop

```
Employee
   │
   ▼
Employee AI Wallet
   │
   ▼
Use AI for work
   │
   ▼
Learn AI
   │
   ▼
Test new models
   │
   ▼
Improve productivity
   │
   ▼
Level up
   │
   ▼
Higher AI allowance
   │
   ▼
Higher retention
```

## Employee Usage Tracking

```sql
employee_accounts
- id
- user_id
- level
- department
- started_at

employee_wallets
- id
- employee_id
- personal_credits
- work_credits
- monthly_budget
- daily_limit

employee_ai_usage
- id
- employee_id
- date
- personal_credits_used
- work_credits_used
- task_type
- model_used
- purpose (personal/work)
```

## Budget Allocation

```
Monthly AI Budget (Employees)
        │
        ▼
┌───────────────┬───────────────┐
│ Personal      │ Work          │
│ 40%           │ 60%           │
└───────────────┴───────────────┘
```

## Policy

- Personal AI dùng cho học tập, phát triển bản thân
- Work AI dùng cho công việc được giao
- Không chia sẻ AI access với người khác
- Report unusual usage
