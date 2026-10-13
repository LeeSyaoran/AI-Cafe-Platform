# AI Café Platform - Sơ đồ Kiến trúc Hệ thống

## 1. Tổng quan Kiến trúc

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                     AI CAFÉ PLATFORM - ARCHITECTURE                            │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                              │
│  ┌───────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                                    CLIENTS                                              │  │
│  │   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐              │  │
│  │   │   Web    │  │  Mobile  │  │BackOffice│  │   KDS    │  │ Staff App│              │  │
│  │   │ Next.js  │  │  Expo    │  │  Next.js │  │ React +  │  │   Expo   │              │  │
│  │   │    14    │  │ React N. │  │  + AntD  │  │  Vite    │  │ React N. │              │  │
│  │   └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘              │  │
│  └───────────────────────────────────────────────────────────────────────────────────────┘  │
│                                              │                                              │
│                                              ▼                                              │
│  ┌───────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                          🚪 API GATEWAY (GO) 🔥                                      │  │
│  │   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────────────┐ │  │
│  │   │ REST API │  │WebSocket │  │ gRPC     │  │ Rate     │  │ JWT Auth / Routing     │ │  │
│  │   │ Handler  │  │ Handler  │  │ Gateway  │  │ Limiter  │  │ / Load Balancing       │ │  │
│  │   └──────────┘  └──────────┘  └──────────┘  └──────────┘  └────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────────────────────────────────────┘  │
│                                              │                                              │
│         ┌───────────────────────────────────┼───────────────────────────────────┐          │
│         │                                   │                                   │          │
│         ▼                                   ▼                                   ▼          │
│  ┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐   │
│  │   GO SERVICES   │              │  JAVA SERVICES  │              │ PYTHON SERVICES │   │
│  │     ⚡          │              │      ☕          │              │       🧠       │   │
│  ├─────────────────┤              ├─────────────────┤              ├─────────────────┤   │
│  │ • Auth Service  │              │ • User Service  │              │ • AI Gateway    │   │
│  │ • Credit Svc   │              │ • Payment Svc   │              │ • Chat Handler  │   │
│  │ • Session Svc  │              │ • Order Service │              │ • Image Gen     │   │
│  │                 │              │ • Member Svc    │              │ • Video Gen     │   │
│  │                 │              │ • Loyalty Svc   │              │                 │   │
│  │                 │              │ • Notification  │              │                 │   │
│  │                 │              │ • Admin Service │              │                 │   │
│  └─────────────────┘              └─────────────────┘              └─────────────────┘   │
│                                              │                                              │
└──────────────────────────────────────────────┼──────────────────────────────────────────────┘
                                               │
                                               ▼
                    ┌──────────────────────────────────────────────────────────────────┐
                    │                      DATA LAYER 📊                                │
                    │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
                    │  │ PostgreSQL  │  │    Redis   │  │    MinIO    │  │  BullMQ    │ │
                    │  │     16      │  │     7      │  │   (S3)      │  │   Queue    │ │
                    │  │  103 Tables │  │   Cache    │  │   Storage   │  │   Jobs     │ │
                    │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │
                    └──────────────────────────────────────────────────────────────────┘
                                               │
                                               ▼
                    ┌──────────────────────────────────────────────────────────────────┐
                    │                 EXTERNAL AI PROVIDERS 🌐                          │
                    │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │
                    │  │ OpenAI  │  │Anthropic│  │ Google  │  │Stability│  │Replicate│  │
                    │  │ GPT-4o  │  │Claude 3.5│  │ Gemini  │  │   AI   │  │         │  │
                    │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │
                    └──────────────────────────────────────────────────────────────────┘
```

## 2. Chi tiết từng Layer

### 2.1 Client Layer (Frontend Apps)

```mermaid
graph TD
    A[Client Layer] --> B[Web App]
    A --> C[Mobile App]
    A --> D[BackOffice]
    A --> E[KDS]
    A --> F[Staff App]
    
    B --> B1[Next.js 14]
    C --> C1[Expo / React Native]
    D --> D1[Next.js + AntD]
    E --> E1[React + Vite]
    F --> F1[Expo / React Native]
    
    B1 --> B2[Zustand + TanStack Query]
    C1 --> C2[Zustand + Axios]
    D1 --> D2[Ant Design + React Query]
```

### 2.2 API Gateway Layer

```mermaid
graph LR
    subgraph "API Gateway - Go Fiber"
        A[REST Requests] --> B[JWT Validator]
        B --> C[Rate Limiter]
        C --> D[Router]
        D --> E[gRPC Client]
        D --> F[HTTP Client]
        D --> G[WebSocket Hub]
    end
```

### 2.3 Go Services Layer

```mermaid
graph TD
    subgraph "Go Services ⚡"
        A[Auth Service] --> A1[OTP Generation]
        A --> A2[JWT Issue/Validate]
        A --> A3[Session Mgmt]
        
        B[Credit Service] --> B1[Balance Check]
        B --> B2[Deduct/Refund]
        B --> B3[Usage Limits]
        B --> B4[Expiry]
        
        C[Session Service] --> C1[WebSocket Hub]
        C --> C2[Presence]
        C --> C3[Real-time Events]
    end
```

### 2.4 Java Services Layer

```mermaid
graph TD
    subgraph "Java Services - Spring Boot ☕"
        subgraph "User Service"
            U1[Profile CRUD]
            U2[Preferences]
            U3[Avatar Upload]
        end
        
        subgraph "Payment Service"
            P1[VNPay]
            P2[MoMo]
            P3[ZaloPay]
            P4[Transaction Mgmt]
        end
        
        subgraph "Order Service"
            O1[Create Order]
            O2[Cart Mgmt]
            O3[Invoice Gen]
            O4[Order History]
        end
        
        subgraph "Member Service"
            M1[Tier Management]
            M2[Subscription]
            M3[Billing]
        end
        
        subgraph "Loyalty Service"
            L1[Points Earning]
            L2[Reward Redemption]
            L3[Referral System]
        end
        
        subgraph "Notification Service"
            N1[Email]
            N2[SMS]
            N3[Push Notification]
        end
        
        subgraph "Admin Service"
            AD1[Dashboard]
            AD2[Product Mgmt]
            AD3[Report/Analytics]
        end
    end
```

### 2.5 AI Gateway Layer

```mermaid
graph TD
    subgraph "AI Gateway - Python FastAPI 🧠"
        A[Request Router] --> B[Chat Handler]
        A --> C[Image Handler]
        A --> D[Video Handler]
        
        B --> E[Cost Controller]
        C --> E
        D --> E
        
        E --> F[Response Cache]
        
        F --> G1[OpenAI]
        F --> G2[Anthropic]
        F --> G3[Google AI]
        F --> G4[Stability AI]
        F --> G5[Replicate]
    end
```

### 2.6 Data Layer

```mermaid
graph TD
    subgraph "Data Layer 📊"
        subgraph "PostgreSQL 16"
            DB1[Users / Auth]
            DB2[Orders / Cart]
            DB3[Products / Menu]
            DB4[Payments]
            DB5[Memberships]
            DB6[AI Sessions]
            DB7[CRM / Loyalty]
            DB8[Inventory]
            DB9[HR / Staff]
            DB10[Marketing]
        end
        
        subgraph "Redis 7"
            R1[Session Cache]
            R2[API Cache]
            R3[Rate Limit]
            R4[Queue]
        end
        
        subgraph "MinIO (S3)"
            M1[File Storage]
            M2[Image CDN]
            M3[Backup]
        end
    end
```

## 3. Flow Communication

### 3.1 User Authentication Flow

```mermaid
sequenceDiagram
    participant U as User
    participant GW as API Gateway
    participant AUTH as Auth Service (Go)
    participant USER as User Service (Java)
    
    U->>GW: POST /api/v1/auth/send-otp
    GW->>AUTH: gRPC: SendOTP
    AUTH->>AUTH: Generate OTP
    AUTH->>AUTH: Store in Redis (5min)
    AUTH-->>U: OTP Sent
    
    U->>GW: POST /api/v1/auth/verify-otp
    GW->>AUTH: gRPC: VerifyOTP
    AUTH->>AUTH: Validate OTP
    AUTH->>USER: gRPC: Get/Create User
    USER-->>AUTH: User Profile
    AUTH-->>GW: JWT + Refresh Token
    GW-->>U: Access Token
```

### 3.2 AI Chat Flow

```mermaid
sequenceDiagram
    participant U as User
    participant GW as API Gateway
    participant CREDIT as Credit Service (Go)
    participant AI as AI Gateway (Python)
    participant OPENAI as OpenAI API
    
    U->>GW: POST /api/v1/ai/chat
    GW->>CREDIT: gRPC: Check & Deduct Credits
    CREDIT-->>GW: Credits OK
    GW->>AI: HTTP: Chat Request
    AI->>OPENAI: API: GPT-4o
    OPENAI-->>AI: Response Stream
    AI-->>GW: Response Stream
    GW-->>U: SSE Stream
```

### 3.3 Order & Payment Flow

```mermaid
sequenceDiagram
    participant U as User
    participant GW as API Gateway
    participant ORDER as Order Service (Java)
    participant PAY as Payment Service (Java)
    participant PG as Payment Gateway
    
    U->>GW: POST /api/v1/orders
    GW->>ORDER: Create Order
    ORDER-->>GW: Order Created
    
    U->>GW: POST /api/v1/payments/vnpay
    GW->>PAY: Process Payment
    PAY->>PG: VNPay Redirect
    PG-->>U: Payment Form
    
    U->>PG: Complete Payment
    PG->>PAY: Callback
    PAY->>ORDER: Update Order Status
    ORDER-->>PAY: Order Updated
    PAY-->>GW: Payment Success
    GW-->>U: Order Confirmed
```

## 4. Database Schema Overview

### 4.1 Core Tables

| Module | Tables | Description |
|--------|--------|-------------|
| **Company** | 5 | Multi-tenant, regions, cafes, seats, staff |
| **Products** | 12 | Menu, variants, options, modifiers, recipes, combos |
| **Orders** | 7 | Orders, items, status, payments, cart |
| **Users** | 8 | User accounts, profiles, devices, sessions |
| **AI** | 8 | Models, sessions, messages, credits |
| **CRM** | 5 | Customers, loyalty, reviews |
| **Payment** | 6 | Transactions, refunds, payment methods |
| **Inventory** | 8 | Stock, suppliers, purchase orders, alerts |
| **Delivery** | 5 | Zones, partners, tracking |
| **Table Mgmt** | 5 | Sessions, reservations, waitlist |
| **KDS** | 3 | Kitchen display, order tracking |
| **Marketing** | 7 | Campaigns, segments, referrals |
| **HR** | 5 | Schedules, attendance, payroll |
| **Operations** | 5 | Print jobs, audit logs, notifications |
| **Extras** | 12 | Translations, files, subscriptions, events |
| **Analytics** | 4 | Daily summaries, metrics |

**Total: 103 tables**

## 5. Technology Stack Summary

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                           AI CAFÉ PLATFORM - TECH STACK                      │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                            FRONTEND                                       │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │    Web      │  │   Mobile    │  │  BackOffice │  │     KDS     │     │  │
│  │  │  Next.js   │  │ Expo/RN     │  │  Next.js    │  │ React+Vite  │     │  │
│  │  │    14       │  │ Android/iOS │  │   + AntD    │  │             │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                        BACKEND - GO (API Gateway)                         │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │  Go Fiber   │  │    gRPC     │  │    Redis    │  │    JWT      │     │  │
│  │  │    / Echo   │  │   Client    │  │   Client    │  │   Auth      │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                       BACKEND - GO (Core Services)                         │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │  │
│  │  │Auth Service │  │Credit Svc   │  │Session Svc  │                        │  │
│  │  │  • OTP      │  │ • Balance   │  │ • WebSocket │                        │  │
│  │  │  • JWT      │  │ • Deduct    │  │ • Presence  │                        │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                        │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                     BACKEND - JAVA (Spring Boot)                          │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │User Service │  │Payment Svc  │  │Order Service│  │Member Svc   │     │  │
│  │  ├─────────────┤  ├─────────────┤  ├─────────────┤  ├─────────────┤     │  │
│  │  │Loyalty Svc  │  │Notification │  │Admin Svc    │  │             │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                       BACKEND - PYTHON (AI Gateway)                       │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │   FastAPI   │  │   Chat      │  │   Image     │  │   Video     │     │  │
│  │  │             │  │   Handler   │  │   Generator │  │   Generator │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                            DATA LAYER                                     │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │ PostgreSQL  │  │    Redis    │  │    MinIO    │  │   BullMQ    │     │  │
│  │  │    16       │  │     7       │  │    (S3)     │  │   Queue     │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘
```

## 6. Infrastructure

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                           INFRASTRUCTURE                                       │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                         AWS SERVICES                                      │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │    ECS      │  │    RDS      │  │ ElastiCache │  │ CloudFront  │     │  │
│  │  │  Fargate    │  │ PostgreSQL  │  │    Redis    │  │    CDN      │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                         CONTAINERIZATION                                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                       │  │
│  │  │   Docker    │  │Docker Compose│  │    Nginx    │                       │  │
│  │  │             │  │ Local Dev    │  │   Reverse   │                       │  │
│  │  │             │  │             │  │   Proxy     │                       │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                       │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                         MONITORING                                       │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │  │
│  │  │Prometheus  │  │   Grafana    │  │CloudWatch   │  │ OpenTelemetry│     │  │
│  │  │  Metrics   │  │  Dashboard  │  │   Logs      │  │  Tracing    │     │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                         CI/CD                                            │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                       │  │
│  │  │   GitHub    │  │  Terraform  │  │ AWS Secrets │                       │  │
│  │  │   Actions   │  │    IaC      │  │   Manager   │                       │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                       │  │
│  └──────────────────────────────────────────────────────────────────────────┘  │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘
```

## 7. Security Architecture

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                           SECURITY LAYERS                                       │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                │
│  1️⃣  NETWORK SECURITY                                                          │
│      ├── AWS WAF - Web Application Firewall                                   │
│      ├── CloudFront - DDoS Protection, CDN                                     │
│      ├── Security Groups - Network Access Control                              │
│      └── Private Subnets - Services Not Directly Exposed                       │
│                                                                                │
│  2️⃣  API GATEWAY SECURITY (Go)                                                │
│      ├── JWT Validation on Every Request                                        │
│      ├── Rate Limiting (100 req/min per user)                                  │
│      ├── Input Validation & Sanitization                                       │
│      ├── CORS Configuration                                                    │
│      └── Request Logging & Monitoring                                          │
│                                                                                │
│  3️⃣  SERVICE SECURITY                                                          │
│      ├── mTLS Between Services (gRPC)                                          │
│      ├── Service-to-Service Authentication                                    │
│      ├── Role-Based Access Control (RBAC)                                     │
│      └── API Key Validation for Internal Services                              │
│                                                                                │
│  4️⃣  DATA SECURITY                                                             │
│      ├── Encryption at Rest (PostgreSQL, Redis)                                │
│      ├── Encryption in Transit (TLS 1.3)                                       │
│      ├── Secrets Manager for Credentials                                       │
│      └── Row-Level Security in Database                                        │
│                                                                                │
│  5️⃣  APPLICATION SECURITY                                                      │
│      ├── SQL Injection Prevention (Parameterized Queries)                       │
│      ├── XSS Prevention (Input Sanitization)                                  │
│      ├── CSRF Protection                                                       │
│      ├── Secure Headers (HSTS, CSP, etc.)                                      │
│      └── Audit Logging                                                         │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘
```

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Author:** AI Café Platform Team
