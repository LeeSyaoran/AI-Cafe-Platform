# AI Café Platform - Docker Setup

## Cấu trúc
```
AI Café Platform/
├── BackEnd/          # Backend services (Java, Go, Python)
├── Database/         # SQL scripts
├── Docker/           # Docker configs ← BẠN ĐANG Ở ĐÂY
├── FrontEnd/
└── docs/
```

## Quick Start

### Chạy tất cả (từ project root)
```bash
docker compose -f Docker/docker-compose.yml up -d
```

### Hoặc từng phần
```bash
# Infrastructure only
docker compose -f Docker/docker-compose.infra.yml up -d

# Services only (cần infrastructure chạy trước)
docker compose -f Docker/docker-compose.services.yml up -d
```

## Services

| Service | Port | Type |
|---------|------|------|
| postgres | 5432 | Database |
| redis | 6379 | Cache |
| pgadmin | 5050 | DB UI |
| api-gateway | 3000 | Go |
| user-service | 3004 | Java |
| payment-service | 3005 | Java |
| member-service | 3006 | Java |
| order-service | 3007 | Java |
| notification-service | 3008 | Java |
| admin-service | 3009 | Java |
| ai-gateway | 4000 | Python |
| nginx | 80/443 | Proxy (prod) |

## Commands

```bash
# Xem status
docker compose -f Docker/docker-compose.yml ps

# Xem logs
docker compose -f Docker/docker-compose.yml logs -f

# Stop
docker compose -f Docker/docker-compose.yml down

# Rebuild
docker compose -f Docker/docker-compose.yml build --no-cache
```

## Cấu hình

Environment variables ở `Docker/.env`:
```env
JWT_SECRET=your-secret-here
VNPAY_TMN_CODE=xxx
MOMO_PARTNER_CODE=xxx
OPENAI_API_KEY=xxx
ANTHROPIC_API_KEY=xxx
```

## Production với Nginx
```bash
docker compose -f Docker/docker-compose.yml --profile production up -d
```