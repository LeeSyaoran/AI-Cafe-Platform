# 🚀 AI Café Platform - Hướng Dẫn Chạy Dự Án

## Yêu Cầu Hệ Thống

- **Docker** & **Docker Compose** (v2.0+)
- **Postman** (để test API)
- **Git** (để clone project)

---

## Bước 1: Clone & Cài Đặt

```bash
cd "d:/project code/AI Café Platform"
```

---

## Bước 2: Chạy Infrastructure (Database & Redis)

```bash
docker compose -f Docker/docker-compose.infra.yml up -d
```

Kiểm tra:
```bash
docker ps
```

**Kết quả mong đợi:**
| Container | Port | Status |
|----------|------|--------|
| aicafe-postgres | 5432 | Up |
| aicafe-redis | 6379 | Up |
| aicafe-pgadmin | 5050 | Up |

---

## Bước 3: Chạy Services

### Option A: Chạy với Docker (Khuyến nghị)

```bash
docker compose -f Docker/docker-compose.yml up -d
```

### Option B: Chạy Local (Dev)

Nếu muốn debug, chạy từng service local:

```bash
# Terminal 1: Go API Gateway
cd BackEnd/go/api-gateway
go run main.go

# Terminal 2: Java User Service
cd BackEnd/java/user-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Bước 4: Kiểm Tra Services

```bash
docker compose -f Docker/docker-compose.yml ps
```

**Services endpoints:**
| Service | URL | Port |
|---------|-----|------|
| API Gateway | http://localhost:3000 | 3000 |
| User Service | http://localhost:3004 | 3004 |
| Payment Service | http://localhost:3005 | 3005 |
| Order Service | http://localhost:3007 | 3007 |
| Member Service | http://localhost:3006 | 3006 |
| AI Gateway | http://localhost:4000 | 4000 |
| Admin Service | http://localhost:3009 | 3009 |
| Notification | http://localhost:3008 | 3008 |
| PostgreSQL | localhost:5432 | 5432 |
| Redis | localhost:6379 | 6379 |

---

## Bước 5: Test API với Postman

### 1. Import Collection (Tùy chọn)

Tạo collection mới: `AI Café Platform`

### 2. Test Health Check

```
GET http://localhost:3000/health
```

**Response:**
```json
{
  "status": "ok",
  "services": {
    "user": "up",
    "payment": "up",
    "order": "up"
  }
}
```

### 3. Test Đăng Ký/Gửi OTP

```
POST http://localhost:3000/v1/auth/send-otp
Content-Type: application/json

{
  "phone": "0909123456"
}
```

### 4. Test Đăng Nhập OTP

```
POST http://localhost:3000/v1/auth/verify-otp
Content-Type: application/json

{
  "phone": "0909123456",
  "code": "123456"
}
```

**Response thành công:**
```json
{
  "success": true,
  "data": {
    "tokens": {
      "access_token": "eyJhbGci...",
      "refresh_token": "dGhpcy...",
      "expires_in": 900
    },
    "user": {
      "id": "uuid",
      "phone": "0909123456",
      "role": "customer"
    }
  }
}
```

### 5. Test API Có Auth

```
GET http://localhost:3000/v1/users/me
Authorization: Bearer <access_token>
```

### 6. Test Lấy Menu

```
GET http://localhost:3000/v1/menu
```

### 7. Test Tạo Order

```
POST http://localhost:3000/v1/orders
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "cafe_id": "00000000-0000-0000-0000-000000000001",
  "order_type": "takeaway",
  "items": [
    {
      "product_id": "00000000-0000-0000-0000-000000000001",
      "quantity": 2
    }
  ]
}
```

---

## Cấu Hình Postman Environment

Tạo environment `AI Café Local`:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | http://localhost:3000 | http://localhost:3000 |
| `access_token` | (empty) | (sẽ set sau login) |
| `user_id` | (empty) | (sẽ set sau login) |

---

## Demo Users (Từ Seed Data)

| Email | Phone | Password | Role |
|-------|-------|----------|------|
| admin@aicafe.vn | 0909000001 | (cần set OTP) | admin |
| staff@aicafe.vn | 0909000002 | (cần set OTP) | staff |
| customer@example.com | 0909000003 | (cần set OTP) | customer |

---

## Troubleshooting

### Lỗi: Container không start được

```bash
# Xem logs
docker compose -f Docker/docker-compose.yml logs postgres
docker compose -f Docker/docker-compose.yml logs api-gateway

# Restart
docker compose -f Docker/docker-compose.yml restart
```

### Lỗi: Database connection failed

Đợi postgres healthy:
```bash
docker compose -f Docker/docker-compose.yml up -d postgres
docker compose -f Docker/docker-compose.yml ps postgres
```

### Xem logs service cụ thể

```bash
docker logs aicafe-api-gateway -f
docker logs aicafe-user-service -f
```

---

## Các Lệnh Hữu Ích

```bash
# Stop all
docker compose -f Docker/docker-compose.yml down

# Rebuild & restart
docker compose -f Docker/docker-compose.yml build --no-cache
docker compose -f Docker/docker-compose.yml up -d

# Xem resource usage
docker stats

# Cleanup volumes (XÓA DATA!)
docker compose -f Docker/docker-compose.yml down -v
```

---

## API Documentation

Xem chi tiết tại: `BackEnd/docs/API_REFERENCE.md`

### Base URL
- Local: `http://localhost:3000`
- API Prefix: `/v1/`

### Endpoints chính

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/auth/send-otp` | Gửi OTP |
| POST | `/v1/auth/verify-otp` | Xác thực OTP |
| POST | `/v1/auth/refresh` | Refresh token |
| GET | `/v1/users/me` | Thông tin user |
| PUT | `/v1/users/me` | Cập nhật user |
| GET | `/v1/menu` | Lấy menu |
| GET | `/v1/products` | Lấy sản phẩm |
| POST | `/v1/orders` | Tạo order |
| GET | `/v1/orders` | Lấy danh sách order |
| GET | `/v1/orders/:id` | Chi tiết order |
| POST | `/v1/payments` | Thanh toán |
| GET | `/v1/wallet` | Ví tiền |
| GET | `/v1/credits` | Credits |
