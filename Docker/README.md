# AI Café Platform - Docker Infrastructure & Deployment Guide

Tài liệu hướng dẫn cấu hình và vận hành hệ sinh thái container Docker cho nền tảng AI Café Platform.

---

## 1. Cấu trúc thư mục `Docker/`

```
Docker/
├── nginx/
│   ├── nginx.conf          # Cấu hình lõi Nginx: Worker, Gzip, Connection pool, Rate limit zones
│   └── conf.d/
│       └── api.conf        # Định tuyến Reverse Proxy: / -> Next.js, /v1/ -> Go API Gateway, /ws/ -> WebSocket
├── .env.example            # Bản mẫu cấu hình biến môi trường chuẩn (Database, Redis, JWT, Keys)
└── README.md               # Tài liệu hướng dẫn (Tệp này)
```

---

## 2. Danh sách dịch vụ (Services Architecture)

Toàn bộ dịch vụ được quản lý tập trung tại file gốc [docker-compose.yml](file:///d:/project%20code/AI%20Café%20Platform/docker-compose.yml):

| Service | Port nội bộ | Port public | Công nghệ / Image | Chức năng |
| :--- | :---: | :---: | :--- | :--- |
| **nginx** | 80 | **80** | `nginx:alpine` | Cổng vào Reverse Proxy duy nhất: định tuyến web, API, rate limit, WebSocket |
| **web** | 3001 | **3001** | Node 20 / Next.js 14 | Giao diện người dùng Web Storefront & Trang quản trị Admin Backoffice |
| **api-gateway** | 3000 | **3000** | Go 1.22 / Alpine | Cổng API Gateway xử lý Auth, Routing, Cart, Order và AI Integration |
| **postgres** | 5432 | **5432** | `postgres:15-alpine` | Cơ sở dữ liệu chính (Tự động nạp `Database/*.sql` khi khởi tạo) |
| **redis** | 6379 | **6379** | `redis:7-alpine` | Bộ nhớ đệm phân tán (Cache), phiên người dùng & Rate limit token |
| **pgadmin** | 80 | **5050** | `dpage/pgadmin4` | Giao diện Web trực quan quản lý PostgreSQL |

---

## 3. Hướng dẫn khởi chạy nhanh (Quick Start)

### Bước 1: Chuẩn bị biến môi trường (từ thư mục gốc dự án)
```bash
# Tạo file .env từ file mẫu
cp Docker/.env.example .env
```

### Bước 2: Khởi động toàn bộ cụm dịch vụ
```bash
# Build và chạy ngầm toàn bộ container
docker compose up -d --build
```

### Bước 3: Kiểm tra trạng thái
```bash
# Xem danh sách container đang chạy và trạng thái healthcheck
docker compose ps
```

---

## 4. Các điểm truy cập hệ thống (Endpoints)

* 🌐 **Giao diện Web Khách hàng & Quản trị**: [http://localhost](http://localhost) (thông qua Nginx cổng 80) hoặc [http://localhost:3001](http://localhost:3001)
* ⚡ **API Gateway (REST API v1)**: [http://localhost/v1/health](http://localhost/v1/health) hoặc [http://localhost:3000](http://localhost:3000)
* 🔌 **WebSocket Trợ lý AI**: `ws://localhost/ws/chat`
* 🐘 **Trình quản lý cơ sở dữ liệu pgAdmin**: [http://localhost:5050](http://localhost:5050)
  * Email: `admin@aicafe.vn`
  * Mật khẩu: `admin123`
* 🗄️ **Kết nối trực tiếp PostgreSQL**: `localhost:5432` (User: `aicafe`, Pass: `aicafe_secret_2024`, DB: `aicafe`)

---

## 5. Lệnh quản lý thường dùng (CLI Cheatsheet)

```bash
# Xem log chi tiết theo thời gian thực của toàn bộ dịch vụ
docker compose logs -f

# Xem log riêng của service cụ thể (ví dụ: api-gateway hoặc nginx)
docker compose logs -f api-gateway
docker compose logs -f nginx

# Dừng toàn bộ hệ thống
docker compose down

# Dừng và xóa toàn bộ dữ liệu volume (Reset sạch cơ sở dữ liệu)
docker compose down -v

# Rebuild lại một dịch vụ khi có cập nhật code mới
docker compose build --no-cache api-gateway
docker compose up -d api-gateway
```