# Database

Schema, migrations, và scripts quản lý PostgreSQL cho AI Café.

## Cấu trúc

```
Database/
├── migrations/
│   ├── V1__init_schema.sql   # Full schema (103 tables, 35+ modules)
│   └── V2__seed_data.sql     # Demo data: companies, cafes, products, users
├── docs/
│   ├── DATABASE_SCHEMA.md
│   ├── ENHANCED_DATABASE_DESIGN.md
│   ├── DATABASE_DESIGN.md
│   └── ADDITIONAL_TABLES.md
└── scripts/
    ├── backup.sh
    └── restore.sh
```

## Tables overview

| Module | Tables | Mô tả |
|--------|--------|-------|
| Company | 5 | Multi-tenant, regions, cafes, seats, staff |
| Products | 12 | Menu, variants, options, modifiers, recipes, combos |
| Inventory | 8 | Stock, suppliers, purchase orders |
| Orders | 7 | Orders, items, status, payments, cart |
| AI | 8 | Models, sessions, messages, credits |
| CRM | 5 | Customers, loyalty, reviews |
| Delivery | 5 | Zones, partners, tracking |
| Table Mgmt | 5 | Sessions, reservations, waitlist |
| KDS | 3 | Kitchen display |
| Financial | 6 | Cash drawers, expenses, commissions |
| Gifts | 5 | Gift cards, vouchers |
| HR | 5 | Schedules, attendance, payroll |
| Marketing | 7 | Campaigns, segments, referrals |
| Support | 5 | Tickets, complaints |
| Operations | 5 | Print jobs, audit logs |
| Analytics | 4 | Daily summaries, metrics |
| Extras | 12 | Translations, files, subscriptions, events |

## Run migrations

```bash
# Apply schema
psql -h localhost -U aicafe -d aicafe -f migrations/V1__init_schema.sql

# Apply seed data (optional)
psql -h localhost -U aicafe -d aicafe -f migrations/V2__seed_data.sql
```

## Local connection

```
Host: localhost
Port: 5432
DB: aicafe
User: aicafe
Password: aicafe_secret_password_2024
```