# AI Café Platform - System Design

## 1. High-Level Architecture

```mermaid
flowchart TB
    subgraph Clients
        WEB[Web App<br/>React + TypeScript]
        POS[POS System<br/>Electron]
        ADMIN[Admin Dashboard<br/>React]
        WORKSPACE[Workspace IDE<br/>Monaco + React]
    end

    subgraph Backend["Backend Services"]
        API[API Gateway<br/>Node.js/Express]
        AI_GATEWAY[AI Gateway<br/>Python/FastAPI]
        WORKERS[Worker Services<br/>BullMQ]
        AUTH[Auth Service<br/>JWT]
    end

    subgraph AI_Layer["AI Layer"]
        ROUTER[Model Router]
        CLASSIFIER[Task Classifier]
        PROVIDERS[AI Providers]
    end

    subgraph Data["Data Layer"]
        POSTGRES[(PostgreSQL)]
        REDIS[(Redis Cache)]
        S3[(Object Storage)]
    end

    WEB --> API
    POS --> API
    ADMIN --> API
    WORKSPACE --> API

    API --> AUTH
    API --> WORKERS
    API --> AI_GATEWAY
    AI_GATEWAY --> ROUTER
    ROUTER --> CLASSIFIER
    CLASSIFIER --> PROVIDERS

    API --> POSTGRES
    API --> REDIS
    WORKERS --> S3
```

## 2. Frontend Architecture

### 2.1 Web Application (Customer-facing)

```
src/
├── app/                    # Next.js App Router
│   ├── (auth)/            # Auth routes
│   │   ├── login/
│   │   ├── register/
│   │   └── forgot-password/
│   ├── (main)/            # Protected routes
│   │   ├── dashboard/
│   │   ├── ai/
│   │   │   ├── chat/
│   │   │   ├── code/
│   │   │   ├── image/
│   │   │   └── video/
│   │   ├── workspace/
│   │   ├── wallet/
│   │   ├── orders/
│   │   └── settings/
│   └── page.tsx
├── components/
│   ├── ui/                # Base UI components
│   │   ├── button.tsx
│   │   ├── card.tsx
│   │   ├── input.tsx
│   │   ├── modal.tsx
│   │   └── ...
│   ├── features/         # Feature components
│   │   ├── ai-chat/
│   │   ├── code-editor/
│   │   ├── image-generator/
│   │   └── video-generator/
│   ├── layout/           # Layout components
│   └── shared/            # Shared components
├── hooks/                 # Custom hooks
│   ├── useAuth.ts
│   ├── useCredits.ts
│   ├── useAI.ts
│   └── useWebSocket.ts
├── lib/                   # Utilities
│   ├── api/
│   ├── utils/
│   └── constants/
├── stores/               # State management
│   ├── authStore.ts
│   ├── walletStore.ts
│   └── uiStore.ts
└── types/                # TypeScript types
```

### 2.2 POS System

```
pos/
├── src/
│   ├── main/             # Electron main process
│   ├── renderer/        # React frontend
│   │   ├── components/
│   │   │   ├── menu/
│   │   │   ├── cart/
│   │   │   ├── payment/
│   │   │   └── receipt/
│   │   ├── pages/
│   │   │   ├── order/
│   │   │   └── history/
│   │   └── App.tsx
│   └── preload/
├── electron-builder.json
└── package.json
```

### 2.3 Admin Dashboard

```
admin/
├── src/
│   ├── app/
│   │   ├── dashboard/
│   │   ├── users/
│   │   ├── orders/
│   │   ├── products/
│   │   ├── credits/
│   │   ├── analytics/
│   │   ├── settings/
│   │   └── reports/
│   └── layout.tsx
│   ├── components/
│   │   ├── charts/
│   │   ├── tables/
│   │   ├── forms/
│   │   └── modals/
│   ├── hooks/
│   │   ├── useAdminUsers.ts
│   │   ├── useAnalytics.ts
│   │   └── useReports.ts
│   └── lib/
│       └── api/
└── package.json
```

### 2.4 Workspace IDE

```
workspace/
├── src/
│   ├── app/
│   │   ├── [projectId]/
│   │   │   ├── page.tsx        # Main editor
│   │   │   ├── preview/
│   │   │   └── settings/
│   │   └── new/
│   └── layout.tsx
│   ├── components/
│   │   ├── editor/
│   │   │   ├── code-editor.tsx
│   │   │   ├── terminal.tsx
│   │   │   ├── file-tree.tsx
│   │   │   └── tabs.tsx
│   │   ├── ai/
│   │   │   ├── chat-panel.tsx
│   │   │   ├── completions.tsx
│   │   │   └── suggestions.tsx
│   │   ├── preview/
│   │   │   └── iframe.tsx
│   │   └── toolbar/
│   └── lib/
│       ├── websocket/
│       └── file-system/
└── package.json
```

## 3. Backend Architecture

### 3.1 API Gateway (Node.js/Express)

```
server/
├── src/
│   ├── index.ts                 # Entry point
│   ├── app.ts                   # Express app setup
│   ├── config/
│   │   ├── database.ts
│   │   ├── redis.ts
│   │   └── env.ts
│   ├── routes/
│   │   ├── v1/
│   │   │   ├── auth.routes.ts
│   │   │   ├── users.routes.ts
│   │   │   ├── orders.routes.ts
│   │   │   ├── credits.routes.ts
│   │   │   ├── ai.routes.ts
│   │   │   ├── workspace.routes.ts
│   │   │   ├── membership.routes.ts
│   │   │   └── admin.routes.ts
│   │   └── index.ts
│   ├── controllers/
│   │   ├── auth.controller.ts
│   │   ├── users.controller.ts
│   │   ├── orders.controller.ts
│   │   ├── credits.controller.ts
│   │   ├── ai.controller.ts
│   │   ├── workspace.controller.ts
│   │   ├── membership.controller.ts
│   │   └── admin.controller.ts
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── user.service.ts
│   │   ├── order.service.ts
│   │   ├── credit.service.ts
│   │   ├── wallet.service.ts
│   │   ├── membership.service.ts
│   │   └── notification.service.ts
│   ├── middleware/
│   │   ├── auth.middleware.ts
│   │   ├── rateLimit.middleware.ts
│   │   ├── creditCheck.middleware.ts
│   │   ├── admin.middleware.ts
│   │   └── error.middleware.ts
│   ├── models/
│   │   ├── User.ts
│   │   ├── Order.ts
│   │   ├── Wallet.ts
│   │   ├── CreditTransaction.ts
│   │   └── index.ts
│   ├── repositories/
│   │   ├── user.repository.ts
│   │   ├── order.repository.ts
│   │   ├── wallet.repository.ts
│   │   └── index.ts
│   ├── queues/
│   │   ├── creditExpiry.queue.ts
│   │   ├── notification.queue.ts
│   │   └── analytics.queue.ts
│   └── utils/
│       ├── logger.ts
│       ├── validator.ts
│       └── helpers.ts
├── prisma/
│   └── schema.prisma
├── tests/
└── package.json
```

### 3.2 AI Gateway Service (Python/FastAPI)

```
ai-gateway/
├── src/
│   ├── main.py                 # FastAPI entry point
│   ├── config.py
│   ├── api/
│   │   ├── routes/
│   │   │   ├── chat.py
│   │   │   ├── code.py
│   │   │   ├── image.py
│   │   │   └── video.py
│   │   └── dependencies.py
│   ├── core/
│   │   ├── router.py          # Model router
│   │   ├── classifier.py       # Task classifier
│   │   ├── limiter.py         # Rate limiter
│   │   └── tracker.py         # Usage tracker
│   ├── providers/
│   │   ├── base.py            # Base provider interface
│   │   ├── openai.py
│   │   ├── anthropic.py
│   │   ├── google.py
│   │   └── registry.py        # Provider registry
│   ├── services/
│   │   ├── chat.service.py
│   │   ├── code.service.py
│   │   ├── image.service.py
│   │   └── video.service.py
│   ├── schemas/
│   │   ├── requests.py
│   │   └── responses.py
│   └── utils/
│       ├── token_counter.py
│       └── cost_calculator.py
├── tests/
├── pyproject.toml
└── Dockerfile
```

### 3.3 Worker Services

```
workers/
├── src/
│   ├── index.ts                 # Worker entry
│   ├── queues/
│   │   ├── creditExpirer.ts    # Credit expiration job
│   │   ├── membershipBilling.ts  # Monthly billing
│   │   ├── referralProcessor.ts # Referral rewards
│   │   ├── loyaltyProcessor.ts  # Loyalty events
│   │   ├── analyticsAggregator.ts
│   │   ├── notificationSender.ts
│   │   └── videoProcessor.ts   # Video generation
│   ├── processors/
│   │   └── index.ts
│   └── utils/
│       └── helpers.ts
└── package.json
```

## 4. Database Design

### 4.1 Entity Relationship Diagram

```mermaid
erDiagram
    users ||--o| wallets : has
    users ||--o{ orders : places
    users ||--o{ memberships : has
    users ||--o{ referrals : refers
    users ||--o| employee_accounts : is

    orders ||--o{ order_items : contains
    products ||--o{ order_items : included_in

    wallets ||--o{ credit_transactions : tracks
    credit_transactions }o--|| credit_expirations : schedules

    users ||--o{ ai_requests : makes
    ai_models ||--o{ ai_requests : serves
    ai_tiers ||--o| users : tiered
    ai_tiers ||--o{ ai_models : access_to

    users ||--o{ workspace_sessions : starts
    workspace_sessions ||--o{ workspace_usage : tracked

    memberships ||--o{ membership_transactions : records
    users ||--o{ loyalty_events : earns
    users ||--o| api_keys : owns

    rate_limits ||--|| users : per_user
    daily_limits ||--|| users : per_user
    budgets }o--|| users : system_wide
```

### 4.2 Table Definitions

```sql
-- Core tables (PostgreSQL)
-- See DATABASE_SCHEMA.md for full definitions

-- Key Tables:
-- users, products, orders, order_items
-- wallets, credit_transactions, credit_expirations
-- ai_tiers, ai_models, model_pricing, ai_requests
-- provider_accounts, provider_usage
-- workspace_sessions, workspace_usage
-- memberships, membership_transactions
-- referrals, loyalty_events
-- rate_limits, daily_limits, budgets
-- api_keys, audit_logs
```

### 4.3 Redis Caching Strategy

```
┌─────────────────────────────────────────────────────────┐
│                    Redis Key Patterns                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Session & Auth                                         │
│  ─────────────────                                      │
│  session:{userId}           → JWT token data (TTL: 24h)│
│  refresh:{userId}          → Refresh token (TTL: 7d)  │
│  rate:{userId}:{endpoint}  → Request count (TTL: 1m)  │
│                                                         │
│  Wallet & Credits                                       │
│  ──────────────────                                    │
│  wallet:{userId}            → Cached balance (TTL: 5m) │
│  daily:{userId}:{date}     → Daily usage (TTL: 25h)   │
│  limit:{userId}:{type}     → Rate limit counters      │
│                                                         │
│  AI & Usage                                             │
│  ──────────                                             │
│  quota:{userId}:video:{date} → Video quota (TTL: 25h) │
│  model:health:{provider}    → Provider status (TTL: 1m)│
│  cost:daily                → Daily cost (TTL: 26h)    │
│  cost:monthly              → Monthly cost (TTL: 32d)  │
│                                                         │
│  Workspace                                              │
│  ─────────                                              │
│  workspace:{sessionId}       → Session state (TTL: 8h)│
│  files:{userId}:{project}   → File cache (TTL: 1h)   │
│                                                         │
│  Rate Limiting                                          │
│  ──────────────                                         │
│  rpm:{userId}               → Requests/min (TTL: 1m)  │
│  tpm:{userId}               → Tokens/min (TTL: 1m)   │
│  concurrent:{userId}        → Concurrent count          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 5. API Design

### 5.1 API Gateway Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐ │
│  │ Auth    │    │ Rate    │    │ Credit  │    │ Request │ │
│  │ Filter  │ → │ Limit   │ →  │ Check   │ →  │ Proxy   │ │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘ │
│       ↓                                        ↓           │
│       │                                        │           │
│       ▼                                        ▼           │
│  ┌─────────┐                           ┌─────────────┐  │
│  │ Session  │                           │ AI Gateway  │  │
│  │ Store    │                           │ (Python)    │  │
│  └─────────┘                           └─────────────┘  │
│                                                ↓           │
│                                                ▼           │
│                                        ┌─────────────┐    │
│                                        │ AI Provider │    │
│                                        │ (OpenAI,    │    │
│                                        │  Anthropic) │    │
│                                        └─────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway as API Gateway
    participant Redis
    participant CreditService as Credit Service
    participant AIGateway as AI Gateway
    participant Provider as AI Provider

    Client->>APIGateway: POST /api/v1/ai/chat
    APIGateway->>Redis: Check rate limit
    Redis-->>APIGateway: OK (under limit)
    APIGateway->>Redis: Check credits
    Redis-->>APIGateway: 8,450 credits
    APIGateway->>CreditService: Validate & Reserve
    CreditService->>Redis: Decrement temp
    CreditService-->>APIGateway: Reserved (200 credits)
    APIGateway->>AIGateway: Route request
    AIGateway->>Provider: Call API
    Provider-->>AIGateway: Response
    AIGateway-->>APIGateway: Result + actual usage
    APIGateway->>CreditService: Finalize deduction
    CreditService->>Redis: Update balance
    CreditService->>DB: Log transaction
    APIGateway-->>Client: Response
```

## 6. Component Diagrams

### 6.1 AI Gateway Components

```mermaid
flowchart LR
    subgraph Input
        REQ[AI Request]
    end

    subgraph Middleware
        AUTH[Auth Check]
        CREDIT[Credit Check]
        RATE[Rate Limit]
        TIER[Tier Check]
    end

    subgraph Router
        CLASS[Task Classifier]
        SELECT[Model Selector]
        COST[Cost Optimizer]
    end

    subgraph Providers
        OA[OpenAI]
        AN[Anthropic]
        GG[Google]
        ST[Stability]
    end

    subgraph Output
        RESP[Response]
        TRACK[Usage Tracker]
    end

    REQ --> AUTH
    AUTH --> CREDIT
    CREDIT --> RATE
    RATE --> TIER
    TIER --> CLASS
    CLASS --> SELECT
    SELECT --> COST
    COST --> OA
    COST --> AN
    COST --> GG
    COST --> ST
    OA --> RESP
    AN --> RESP
    GG --> RESP
    ST --> RESP
    RESP --> TRACK
```

### 6.2 Order Flow Components

```mermaid
flowchart TB
    subgraph POS
        QR[QR Scan]
        MENU[Menu Display]
        CART[Cart]
        PAY[Payment]
    end

    subgraph API
        CREATE[Create Order]
        PAYMENT[Process Payment]
        INVENTORY[Update Inventory]
    end

    subgraph Fulfillment
        KITCHEN[Barista]
        COMPLETE[Complete Order]
    end

    subgraph PostOrder
        CREDITS[Add Credits]
        WALLET[Update Wallet]
        LOYALTY[Record Loyalty]
        NOTIFY[Notify User]
    end

    QR --> MENU
    MENU --> CART
    CART --> PAY
    PAY --> CREATE
    CREATE --> PAYMENT
    PAYMENT --> INVENTORY
    INVENTORY --> KITCHEN
    KITCHEN --> COMPLETE
    COMPLETE --> CREDITS
    CREDITS --> WALLET
    WALLET --> LOYALTY
    LOYALTY --> NOTIFY
```

## 7. Infrastructure

### 7.1 Deployment Architecture

```mermaid
flowchart TB
    subgraph CDN
        CLOUDFRONT[CloudFront]
    end

    subgraph Hosting
        ECS[ECS Fargate<br/>API Server]
        LAMBDA[Lambda<br/>AI Gateway]
        EC2[EC2<br/>Workspace IDE]
    end

    subgraph Data
        RDS[(RDS PostgreSQL<br/>Primary)]
        READ_REPLICA[(RDS PostgreSQL<br/>Read Replica)]
        ELASTICACHE[(ElastiCache<br/>Redis)]
        S3[(S3<br/>File Storage)]
    end

    subgraph Monitoring
        CLOUDWATCH[CloudWatch]
        DATADOG[Datadog]
        SENTRY[Sentry]
    end

    CLOUDFRONT --> ECS
    CLOUDFRONT --> LAMBDA
    ECS --> RDS
    ECS --> READ_REPLICA
    ECS --> ELASTICACHE
    ECS --> S3
    LAMBDA --> ELASTICACHE
    LAMBDA --> RDS
    EC2 --> RDS

    ECS --> CLOUDWATCH
    LAMBDA --> CLOUDWATCH
    EC2 --> CLOUDWATCH
    ECS --> DATADOG
    ECS --> SENTRY
```

### 7.2 Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Frontend** | | |
| Web App | Next.js 14, React, TypeScript | Customer-facing app |
| POS | Electron, React | Point of Sale |
| Admin | Next.js 14, React, TypeScript | Admin dashboard |
| Workspace | Monaco Editor, React | Code editor |
| **Backend** | | |
| API Server | Node.js, Express, Fastify | REST API |
| AI Gateway | Python, FastAPI | AI processing |
| Workers | Node.js, BullMQ | Background jobs |
| Auth | JWT, Redis | Authentication |
| **Data** | | |
| Primary DB | PostgreSQL 15 | Transactional data |
| Cache | Redis 7 | Session, cache |
| File Storage | S3 | Images, files |
| Search | PostgreSQL (full-text) | Search |
| **Infrastructure** | | |
| Hosting | AWS ECS, Lambda | Compute |
| CDN | CloudFront | Static assets |
| Monitoring | CloudWatch, Datadog | Observability |
| CI/CD | GitHub Actions | Deployment |

## 8. Security Architecture

### 8.1 Security Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Security Layers                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Layer 1: Network Security                            │
│  ────────────────────────────                            │
│  • VPC with private subnets                            │
│  • Security groups (least privilege)                    │
│  • WAF for API protection                              │
│  • DDoS protection (CloudFront)                        │
│                                                         │
│  Layer 2: Application Security                        │
│  ─────────────────────────────                          │
│  • JWT with short expiry (15m)                        │
│  • Refresh token rotation                              │
│  • Rate limiting per user/IP                           │
│  • Input validation (Zod)                             │
│  • CSRF protection                                     │
│                                                         │
│  Layer 3: Data Security                               │
│  ───────────────────────                               │
│  • Encryption at rest (AES-256)                       │
│  • Encryption in transit (TLS 1.3)                     │
│  • API keys encrypted (KMS)                           │
│  • No AI prompts stored (privacy)                     │
│  • PII data encrypted                                  │
│                                                         │
│  Layer 4: Monitoring                                   │
│  ──────────────────                                   │
│  • Anomaly detection                                   │
│  • Fraud signals monitoring                            │
│  • Audit logging                                       │
│  • Alert on suspicious activity                        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 9. Data Flow Diagrams

### 9.1 AI Request Flow

```mermaid
flowchart TB
    subgraph Client
        USER[User]
        FE[Frontend App]
    end

    subgraph Backend
        API[API Gateway]
        CACHE[Redis]
        DB[(PostgreSQL)]
    end

    subgraph AIService
        GATEWAY[AI Gateway]
        CLASSIFY[Classifier]
        ROUTER[Model Router]
        TRACKER[Usage Tracker]
    end

    subgraph Providers
        OPENAI[OpenAI]
        ANTHROPIC[Anthropic]
        GOOGLE[Google]
    end

    USER --> FE
    FE --> API
    API --> CACHE
    API --> DB
    API --> GATEWAY
    GATEWAY --> CLASSIFY
    CLASSIFY --> ROUTER
    ROUTER --> OPENAI
    ROUTER --> ANTHROPIC
    ROUTER --> GOOGLE
    OPENAI --> TRACKER
    ANTHROPIC --> TRACKER
    GOOGLE --> TRACKER
    TRACKER --> CACHE
    TRACKER --> DB
    GATEWAY --> API
    API --> FE
    FE --> USER
```

### 9.2 Order Processing Flow

```mermaid
flowchart TB
    subgraph POS
        QR[Scan QR]
        SELECT[Select Items]
        PAY[Payment]
    end

    subgraph OrderService
        CREATE[Create Order]
        VALIDATE[Validate]
        RESERVE[Reserve Credits]
    end

    subgraph PaymentGateway
        PG[Payment Provider]
        CONFIRM[Confirm]
    end

    subgraph Fulfillment
        QUEUE[Order Queue]
        PROCESS[Process]
        COMPLETE[Complete]
    end

    subgraph PostProcessing
        CREDIT[Add Credits]
        LOYALTY[Loyalty Event]
        NOTIFY[Notify]
    end

    QR --> SELECT
    SELECT --> PAY
    PAY --> CREATE
    CREATE --> VALIDATE
    VALIDATE --> RESERVE
    RESERVE --> PG
    PG --> CONFIRM
    CONFIRM --> QUEUE
    QUEUE --> PROCESS
    PROCESS --> COMPLETE
    COMPLETE --> CREDIT
    CREDIT --> LOYALTY
    LOYALTY --> NOTIFY
```

## 10. Workspace Architecture

### 10.1 Workspace Components

```mermaid
flowchart TB
    subgraph IDE["Workspace IDE"]
        EDITOR[Monaco Editor]
        TERMINAL[Terminal]
        PREVIEW[Live Preview]
        FILE_TREE[File Tree]
        AI_PANEL[AI Chat Panel]
    end

    subgraph Backend["Backend Services"]
        WS[WebSocket Server]
        FS[File Service]
        SYNC[Sync Service]
        AI[AI Gateway]
    end

    subgraph Storage["Storage"]
        DB[(Database)]
        S3[(S3 Files)]
        REDIS[(Redis Cache)]
    end

    EDITOR --> WS
    TERMINAL --> WS
    FILE_TREE --> FS
    FS --> DB
    FS --> S3
    AI_PANEL --> AI
    WS --> SYNC
    SYNC --> REDIS
    SYNC --> DB
    AI --> WS
```

### 10.2 Session Management

```
┌─────────────────────────────────────────────────────────┐
│               Workspace Session Flow                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. Start Session                                       │
│     POST /workspace/sessions                           │
│     → Check workspace time remaining                   │
│     → Create session record                            │
│     → Initialize Redis state                           │
│     → Return session credentials                       │
│                                                         │
│  2. Active Session                                      │
│     WebSocket connection established                    │
│     Heartbeat every 30s                                │
│     Track active time                                  │
│     Auto-save every 30s                               │
│                                                         │
│  3. AI Usage                                           │
│     Deduct from credits pool                           │
│     Track per-task usage                               │
│     Apply rate limits                                  │
│                                                         │
│  4. End Session                                         │
│     Manual: User clicks End                           │
│     Auto: Time expires                                 │
│     Save final state                                   │
│     Calculate total usage                              │
│     Return remaining time                              │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 11. Real-time Features

### 11.1 WebSocket Architecture

```
┌─────────────────────────────────────────────────────────┐
│                 WebSocket Architecture                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────┐                                           │
│  │ Clients │  React App, Workspace IDE, POS          │
│  └────┬────┘                                           │
│       │                                                │
│       ▼                                                │
│  ┌─────────────┐    ┌─────────────┐                   │
│  │ Load Balancer│    │   Redis     │                   │
│  │ (ALB)       │    │  Pub/Sub    │                   │
│  └──────┬──────┘    └──────┬──────┘                   │
│         │                   │                          │
│         ▼                   ▼                          │
│  ┌─────────────────────────────────────┐             │
│  │         WebSocket Server             │             │
│  │  ┌─────────┐  ┌─────────┐  ┌─────┐ │             │
│  │  │ Chat    │  │ Workspace│  │Order│ │             │
│  │  │ Handler │  │ Handler │  │Handler│ │             │
│  │  └─────────┘  └─────────┘  └─────┘ │             │
│  └─────────────────────────────────────┘             │
│                      │                                │
│                      ▼                                │
│               ┌─────────────┐                         │
│               │   Redis     │                         │
│               │  Channels   │                         │
│               └─────────────┘                         │
│                      │                                │
│                      ▼                                │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐             │
│  │  Chat   │  │Workspace │  │  Order   │             │
│  │ Service │  │ Service  │  │ Service  │             │
│  └────┬────┘  └────┬────┘  └────┬────┘             │
│       │            │            │                    │
└───────┼────────────┼────────────┼────────────────────┘
        │            │            │
        ▼            ▼            ▼
    ┌───────┐    ┌───────┐    ┌───────┐
    │ AI    │    │ File  │    │ Order │
    │ GW    │    │ Store │    │ Queue │
    └───────┘    └───────┘    └───────┘
```

## 12. Monitoring & Observability

### 12.1 Metrics Dashboard

```mermaid
flowchart LR
    subgraph Collectors
        APM[APM Agent]
        LOGS[Log Agent]
        METRICS[Metrics Agent]
    end

    subgraph Store
        CLOUDWATCH[CloudWatch]
        DATADOG[Datadog]
        SENTRY[Sentry]
    end

    subgraph Dashboards
        ALERT[Alerts]
        DASHBOARD[Dashboards]
        EXPLORE[Explore]
    end

    APM --> CLOUDWATCH
    LOGS --> CLOUDWATCH
    METRICS --> DATADOG
    CLOUDWATCH --> ALERT
    DATADOG --> DASHBOARD
    SENTRY --> EXPLORE
```

### 12.2 Key Metrics

| Category | Metrics | Alert Threshold |
|----------|---------|-----------------|
| **Business** | | |
| Orders | count, revenue | < 50/day |
| Active Users | DAU, MAU | < 50 DAU |
| Credits | issued, used, expired | > 90% expired |
| **Technical** | | |
| API | latency p50/p95/p99, error rate | p99 > 2s, error > 1% |
| AI | cost/day, cost/user, latency | cost > budget |
| Database | connections, query time | connections > 80% |
| **Security** | | |
| Fraud | suspicious activity rate | > 5% |
| Rate Limit | blocked requests | > 100/min |
| Auth | failed logins | > 10/min |

## 13. File Structure Summary

```
AI-Cafe-Platform/
├── frontend/
│   ├── web/                 # Customer web app (Next.js)
│   ├── pos/                 # POS system (Electron)
│   ├── admin/               # Admin dashboard (Next.js)
│   └── workspace/           # IDE (React + Monaco)
├── backend/
│   ├── api-gateway/         # REST API (Node.js)
│   ├── ai-gateway/          # AI processing (Python)
│   └── workers/             # Background jobs (Node.js)
├── infrastructure/
│   ├── terraform/           # Infrastructure as Code
│   ├── docker/              # Docker configs
│   └── k8s/                 # Kubernetes configs
├── docs/
│   ├── SYSTEM_DESIGN.md     # This file
│   ├── *.md                 # Other documentation
└── README.md
```

## 14. Implementation Priority

### Phase 1: Core MVP
1. **Database Schema** - PostgreSQL setup
2. **Auth System** - JWT, session management
3. **User & Wallet** - Core user flows
4. **Order System** - Basic ordering
5. **Credit Engine** - Credits management
6. **AI Gateway** - Single provider (OpenAI)
7. **Chat Interface** - Basic AI chat
8. **Web Frontend** - Customer app

### Phase 2: Growth
1. **POS System** - In-store ordering
2. **Model Router** - Multi-provider
3. **Image Generation** - Add image AI
4. **Workspace** - Basic workspace
5. **Membership** - Subscription system
6. **Loyalty** - Rewards program
7. **Admin Dashboard** - Management tools

### Phase 3: Scale
1. **Video Generation** - Video AI
2. **Advanced Workspace** - Full IDE features
3. **Public API** - Developer access
4. **Analytics** - Business intelligence
5. **Multi-tenant** - Organization accounts
