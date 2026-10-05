# AI Café Platform - Enterprise F&B System

## 🎯 Project Overview

Comprehensive enterprise Food & Beverage platform with AI integration, multi-tenant architecture, and modern microservices design.

---

## 📦 Project Structure (NEW - 3-folder layout)

```
AI Café Platform/
├── BackEnd/                # Backend services + API docs + Docker
│   ├── services/           # Microservices (api-gateway, ai, user, order)
│   ├── packages/           # Shared backend packages
│   ├── docker/             # Docker Compose
│   └── docs/               # API docs, design docs
│
├── Database/               # Schema, migrations, seed
│   ├── migrations/         # V1__init_schema.sql, V2__seed_data.sql
│   ├── docs/               # DATABASE_SCHEMA, ENHANCED_DATABASE_DESIGN
│   └── README.md
│
├── FrontEnd/               # All client apps
│   ├── Web/                # Customer web (Next.js)
│   ├── Mobile/             # React Native (Android + iOS)
│   ├── BackOffice/         # Admin dashboard
│   ├── KDS/                # Kitchen Display System
│   └── docs/               # Frontend design docs
│
├── docs/                   # General cross-cutting docs (PRD, ROADMAP, etc.)
└── .github/                 # CI/CD workflows
```

---

## 🗄️ Database Schema (103 Tables)

### Modules

| Module | Tables | Description |
|--------|--------|-------------|
| Company | 5 | Multi-tenant, regions, cafes, seats, staff |
| Products | 12 | Menu, variants, options, modifiers, recipes, combos |
| Inventory | 8 | Stock, suppliers, purchase orders, alerts |
| Orders | 7 | Orders, items, status, payments, cart |
| AI | 8 | Models, sessions, messages, credits |
| CRM | 5 | Customers, loyalty, reviews |
| Delivery | 5 | Zones, partners, tracking |
| Table Mgmt | 5 | Sessions, reservations, waitlist |
| KDS | 3 | Kitchen display, order tracking |
| Financial | 6 | Cash drawers, expenses, commissions |
| Gifts | 5 | Gift cards, vouchers |
| HR | 5 | Schedules, attendance, payroll |
| Marketing | 7 | Campaigns, segments, referrals |
| Support | 5 | Tickets, complaints, suggestions |
| Operations | 5 | Print jobs, audit logs, notifications |
| Extras | 12 | Translations, files, subscriptions, events |
| Analytics | 4 | Daily summaries, metrics |

Chi tiết: [Database/docs/DATABASE_SCHEMA.md](Database/docs/DATABASE_SCHEMA.md)

---

## 🚀 Quick Start

```bash
# 1. Start database
cd Database
psql -U aicafe -d aicafe -f migrations/V1__init_schema.sql
psql -U aicafe -d aicafe -f migrations/V2__seed_data.sql

# 2. Start backend
cd ../BackEnd/docker
docker-compose up -d

# 3. Start frontend
cd ../../FrontEnd/Web
npm install && npm run dev
```

---

## 🔌 API Documentation

### Base URL
```
Production: https://api.aicafe.vn/v1
Staging: https://api-staging.aicafe.vn/v1
Local: http://localhost:3000/v1
```

### Authentication
```http
Authorization: Bearer <access_token>
X-Company-ID: <company_uuid>
```

### Key Endpoints
- Auth: `/auth/register`, `/auth/login`, `/auth/refresh`
- Users: `/users/me`, `/users/me/notifications`
- Cafes: `/cafes`, `/cafes/:id/menu`
- Products: `/products`, `/products/:id`
- Orders: `/orders`, `/orders/:id/status`
- Cart: `/cart`, `/cart/items`, `/cart/checkout`
- AI Chat: `/ai/sessions`, `/ai/sessions/:id/messages`
- Credits: `/credits`, `/credits/purchase`

Chi tiết: [BackEnd/docs/api/REST_API.md](BackEnd/docs/api/REST_API.md)

---

## 🏗️ Tech Stack

### Backend (BackEnd/)
- **Runtime:** Node.js 20+ (NestJS)
- **Database:** PostgreSQL 16 with UUID extension
- **Cache:** Redis 7
- **ORM:** TypeORM
- **Auth:** JWT + Refresh Tokens
- **AI:** OpenAI, Anthropic, Google AI

### Frontend (FrontEnd/)
- **Web:** Next.js 14 (App Router)
- **Mobile:** React Native + Expo (Android + iOS)
- **BackOffice:** Next.js + Ant Design
- **KDS:** React + Vite
- **State:** Zustand + React Query
- **UI:** TailwindCSS + Radix UI

### Infrastructure
- **Container:** Docker + Docker Compose
- **Proxy:** Nginx
- **Monitoring:** Prometheus + Grafana
- **Storage:** MinIO (S3-compatible)

---

## 🔧 Development

### Backend
```bash
cd BackEnd/services/api-gateway
npm install
npm run start:dev
```

### Frontend Web
```bash
cd FrontEnd/Web
npm install
npm run dev
```

### Mobile (Android + iOS)
```bash
cd FrontEnd/Mobile
npm install
npm run android   # Android
npm run ios       # iOS
```

---

## 🔒 Security

- JWT with refresh tokens
- Row-level security (RLS) for multi-tenancy
- Input validation with class-validator
- Rate limiting on all endpoints
- SQL injection prevention
- XSS protection
- CORS configuration

---

## 📄 License

Proprietary - AI Café Vietnam © 2024