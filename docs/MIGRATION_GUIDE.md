# AI Café Platform - Migration Guide: Local → Self-Hosted → Cloud

## 1. Tổng Quan Kiến Trúc Di Chuyển

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         MIGRATION PATH OVERVIEW                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    STAGE 1: LOCAL DEVELOPMENT                            │     │
│  │                         (Current)                                         │     │
│  │                                                                          │     │
│  │    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐           │     │
│  │    │   Your      │     │   Docker    │     │   Docker    │           │     │
│  │    │  Laptop    │────▶│  Desktop    │────▶│   Compose   │           │     │
│  │    │ (Windows/  │     │  +          │     │   (All in   │           │     │
│  │    │  Mac)      │     │  Local DB   │     │   one host) │           │     │
│  │    └─────────────┘     └─────────────┘     └─────────────┘           │     │
│  │                                                                          │     │
│  │    💰 Cost: $0                                                          │     │
│  │    👥 Users: 1-5                                                        │     │
│  │    🎯 Purpose: Development & Testing                                     │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                    │                                                │
│                                    │ MIGRATE                                        │
│                                    ▼                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                   STAGE 2: SELF-HOSTED VPS                              │     │
│  │                         (Next Step)                                      │     │
│  │                                                                          │     │
│  │    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐           │     │
│  │    │   VPS       │     │   Docker    │     │   Docker    │           │     │
│  │    │  Server     │────▶│  Swarm /   │────▶│   Compose   │           │     │
│  │    │ (Ubuntu/   │     │  Single     │     │   (Same     │           │     │
│  │    │  Debian)   │     │  Host       │     │   config!)  │           │     │
│  │    └─────────────┘     └─────────────┘     └─────────────┘           │     │
│  │                                                                          │     │
│  │    💰 Cost: $40-100/month                                              │     │
│  │    👥 Users: 100-1000                                                  │     │
│  │    🎯 Purpose: Production (Small-Medium)                               │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                    │                                                │
│                                    │ MIGRATE                                        │
│                                    ▼                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    STAGE 3: CLOUD (AWS/GCP)                             │     │
│  │                       (Future)                                           │     │
│  │                                                                          │     │
│  │    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐           │     │
│  │    │   Cloud     │     │   EKS/     │     │   K8s      │           │     │
│  │    │  Provider  │────▶│   ECS      │────▶│   (Same    │           │     │
│  │    │ (AWS/GCP)  │     │   (Cloud)  │     │   images!)  │           │     │
│  │    └─────────────┘     └─────────────┘     └─────────────┘           │     │
│  │                                                                          │     │
│  │    💰 Cost: $250-500/month                                            │     │
│  │    👥 Users: Unlimited                                                 │     │
│  │    🎯 Purpose: Production (Enterprise)                                  │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Điều Quan Trọng: Code Không Đổi!

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         THE MAGIC: SAME CODE EVERYWHERE                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│  ┌───────────────────────────────────────────────────────────────────────────┐   │
│  │                                                                          │   │
│  │                         YOUR APPLICATION CODE                             │   │
│  │                         (Docker Image)                                   │   │
│  │                                                                          │   │
│  │   ┌─────────────────────────────────────────────────────────────┐      │   │
│  │   │                                                             │      │   │
│  │   │   services/user-service/                                   │      │   │
│  │   │   services/order-service/                                   │      │   │
│  │   │   services/ai-gateway/                                     │      │   │
│  │   │   apps/web/                                                │      │   │
│  │   │   apps/admin/                                              │      │   │
│  │   │   apps/mobile/                                             │      │   │
│  │   │   apps/pos/                                                │      │   │
│  │   │                                                             │      │   │
│  │   │   ✅ CODE GIỐNG NHAU Ở TẤT CẢ MÔI TRƯỜNG!               │      │   │
│  │   │                                                             │      │   │
│  │   └─────────────────────────────────────────────────────────────┘      │   │
│  │                              │                                           │   │
│  │          ┌──────────────────┼──────────────────┐                      │   │
│  │          │                  │                  │                        │   │
│  │          ▼                  ▼                  ▼                        │   │
│  │   ┌────────────┐    ┌────────────┐    ┌────────────┐               │   │
│  │   │   LOCAL    │    │    VPS     │    │   CLOUD    │               │   │
│  │   │  Laptop    │    │  Server    │    │    AWS     │               │   │
│  │   │            │    │            │    │            │               │   │
│  │   │ • Docker   │    │ • Docker   │    │ • EKS      │               │   │
│  │   │ • Postgres │    │ • Postgres │    │ • RDS      │               │   │
│  │   │ • Redis    │    │ • Redis    │    │ • ElastiCache │            │   │
│  │   │ • Kafka    │    │ • Kafka    │    │ • MSK      │               │   │
│  │   │            │    │            │    │            │               │   │
│  │   │ CHỈ KHÁC:  │    │ CHỈ KHÁC:  │    │ CHỈ KHÁC:   │               │   │
│  │   │ • Env vars │    │ • Env vars │    │ • Env vars  │               │   │
│  │   │ • Domain   │    │ • Domain   │    │ • Domain    │               │   │
│  │   │ • SSL cert │    │ • SSL cert │    │ • ACM cert  │               │   │
│  │   └────────────┘    └────────────┘    └────────────┘               │   │
│  │                                                                          │   │
│  └───────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. So Sánh Cấu Hình Theo Môi Trường

### 3.1 Environment Variables (Điểm Khác Biệt Duy Nhất)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         ENVIRONMENT CONFIGURATION                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  LOCAL (.env.local)                                                                 │
│  ═══════════════════                                                                 │
│  ```bash                                                                            │
│  # Database                                                                         │
│  DATABASE_URL=postgres://aicafe:devpassword@localhost:5432/aicafe_dev              │
│                                                                                      │
│  # Redis                                                                            │
│  REDIS_URL=redis://localhost:6379                                                    │
│                                                                                      │
│  # Kafka                                                                            │
│  KAFKA_BROKERS=localhost:9092                                                      │
│                                                                                      │
│  # Domain                                                                           │
│  API_URL=http://localhost:8000                                                      │
│  WEB_URL=http://localhost:3000                                                     │
│  ```                                                                                │
│                                                                                      │
│  ────────────────────────────────────────────────────────────────────────────────   │
│                                                                                      │
│  VPS/SELF-HOSTED (.env.vps)                                                         │
│  ═════════════════════════════                                                      │
│  ```bash                                                                            │
│  # Database                                                                         │
│  DATABASE_URL=postgres://aicafe:securepass@localhost:5432/aicafe_prod               │
│                                                                                      │
│  # Redis                                                                            │
│  REDIS_URL=redis://localhost:6379                                                   │
│                                                                                      │
│  # Kafka                                                                            │
│  KAFKA_BROKERS=localhost:9092                                                      │
│                                                                                      │
│  # Domain                                                                           │
│  API_URL=https://api.aicafe.vn                                                     │
│  WEB_URL=https://aicafe.vn                                                         │
│  ```                                                                                │
│                                                                                      │
│  ────────────────────────────────────────────────────────────────────────────────   │
│                                                                                      │
│  CLOUD/AWS (.env.cloud)                                                             │
│  ═══════════════════════                                                             │
│  ```bash                                                                            │
│  # Database (RDS)                                                                  │
│  DATABASE_URL=postgres://aicafe:securepass@rds.amazonaws.com:5432/aicafe_prod       │
│                                                                                      │
│  # Redis (ElastiCache)                                                              │
│  REDIS_URL=redis://elasticache.amazonaws.com:6379                                  │
│                                                                                      │
│  # Kafka (MSK)                                                                      │
│  KAFKA_BROKERS=msk.amazonaws.com:9092                                              │
│                                                                                      │
│  # Domain                                                                           │
│  API_URL=https://api.aicafe.vn                                                     │
│  WEB_URL=https://aicafe.vn                                                         │
│  ```                                                                                │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Docker Compose Files (Thay Đổi Nhẹ)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DOCKER COMPOSE COMPARISON                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    LOCAL: docker-compose.yml                              │     │
│  │                    (Full stack in one file)                              │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    VPS: docker-compose.prod.yml                          │     │
│  │                    (Thêm: Nginx, SSL, Healthchecks)                    │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    CLOUD: K8s manifests                                 │     │
│  │                    (Kubernetes deployments)                              │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  KEY DIFFERENCES:                                                                   │
│  ─────────────────                                                                   │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │ Feature               │ Local │ VPS │ Cloud │                         │     │
│  │ ────────────────────┼───────┼─────┼───────┼─────────────────────────│     │
│  │ Full docker-compose  │  ✅   │  ✅ │   ❌  │                         │     │
│  │ Kubernetes manifests │  ❌   │  ❌ │   ✅  │                         │     │
│  │ Nginx reverse proxy │  ❌   │  ✅ │   ✅  │                         │     │
│  │ SSL certificates     │  ❌   │  ✅ │   ✅  │                         │     │
│  │ Health checks        │  ⚠️   │  ✅ │   ✅  │                         │     │
│  │ Resource limits     │  ❌   │  ✅ │   ✅  │                         │     │
│  │ Backup scripts       │  ❌   │  ✅ │   ✅  │                         │     │
│  │ Monitoring           │  ⚠️   │  ✅ │   ✅  │                         │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Chi Tiết Từng Bước Di Chuyển

### 4.1 Từ Local → VPS (Self-Hosted)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         LOCAL TO VPS MIGRATION                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Chuẩn bị VPS Server                                                        │
│  ═══════════════════════════                                                         │
│                                                                                      │
│  1.1. Thuê VPS (DigitalOcean, Linode, Vultr)                                       │
│      ┌─────────────────────────────────────────────────────────────────┐        │
│      │ Recommended: $40-60/month                                     │        │
│      │ • 4 vCPU, 8GB RAM                                           │        │
│      │ • 80GB SSD                                                  │        │
│      │ • Ubuntu 22.04 LTS                                         │        │
│      │ • IPv4 + IPv6                                               │        │
│      └─────────────────────────────────────────────────────────────────┘        │
│                                                                                      │
│  1.2. Cài đặt Docker trên VPS                                                       │
│      ```bash                                                                       │
│      # SSH vào VPS                                                                 │
│      ssh root@your-vps-ip                                                          │
│                                                                                      │
│      # Cài Docker                                                                   │
│      curl -fsSL https://get.docker.com | sh                                        │
│                                                                                      │
│      # Enable Docker                                                                │
│      systemctl enable docker                                                        │
│      ```                                                                           │
│                                                                                      │
│  STEP 2: Clone Code lên VPS                                                         │
│  ════════════════════════                                                           │
│                                                                                      │
│  ```bash                                                                           │
│  # Clone repository                                                                 │
│  git clone https://github.com/your-org/ai-cafe-platform.git                        │
│  cd ai-cafe-platform                                                               │
│                                                                                      │
│  # Copy production env file                                                         │
│  cp .env.example .env                                                              │
│  nano .env  # Chỉnh sửa với domain thật                                           │
│  ```                                                                           │
│                                                                                      │
│  STEP 3: Cấu hình Domain & SSL                                                     │
│  ══════════════════════════════════                                                  │
│                                                                                      │
│  ```bash                                                                           │
│  # Cài Nginx làm reverse proxy                                                     │
│  apt install nginx -y                                                              │
│                                                                                      │
│  # Cài Certbot cho SSL                                                             │
│  apt install certbot python3-certbot-nginx -y                                       │
│                                                                                      │
│  # Xin SSL certificate                                                              │
│  certbot --nginx -d api.aicafe.vn -d aicafe.vn                                    │
│  ```                                                                           │
│                                                                                      │
│  STEP 4: Deploy với Docker Compose                                                  │
│  ═══════════════════════════════════                                                │
│                                                                                      │
│  ```bash                                                                           │
│  # Build và chạy production                                                         │
│  docker-compose -f docker-compose.prod.yml up -d                                   │
│                                                                                      │
│  # Kiểm tra                                                                      │
│  docker-compose ps                                                                │
│  docker-compose logs -f                                                            │
│  ```                                                                           │
│                                                                                      │
│  STEP 5: Setup Monitoring (Tùy chọn)                                               │
│  ══════════════════════════════════                                                 │
│                                                                                      │
│  ```bash                                                                           │
│  # Cài Prometheus + Grafana                                                        │
│  docker run -d --name prometheus                                                  │
│    -p 9090:9090                                                                  │
│    -v /etc/prometheus:/etc/prometheus                                              │
│    prom/prometheus:latest                                                         │
│  ```                                                                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Từ VPS → Cloud (AWS)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         VPS TO CLOUD MIGRATION                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  MIGRATION STRATEGY: "Lift and Shift" (Di chuyển nguyên container)               │
│                                                                                      │
│  ════════════════════════════════════════════════════════════════════════════════   │
│                                                                                      │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    SAME DOCKER IMAGE                                    │     │
│  │                         ↓                                              │     │
│  │              ┌───────────┴───────────┐                                  │     │
│  │              │                       │                                  │     │
│  │              ▼                       ▼                                  │     │
│  │       ┌────────────┐         ┌────────────┐                           │     │
│  │       │    VPS     │         │    AWS     │                           │     │
│  │       │  Docker    │   →    │    EKS     │                           │     │
│  │       │  Compose   │         │ Kubernetes │                           │     │
│  │       └────────────┘         └────────────┘                           │     │
│  │                                                                          │     │
│  │       Image: Tag & Push → ECR → Deploy to EKS                         │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│                                                                                      │
│  STEP 1: Push Docker Images lên ECR                                               │
│  ═════════════════════════════════════════                                          │
│                                                                                      │
│  ```bash                                                                           │
│  # Login to ECR                                                                     │
│  aws ecr get-login-password | docker login --username AWS \                         │
│    --password-stdin $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com                     │
│                                                                                      │
│  # Tag images                                                                       │
│  docker tag aicafe/user-service:latest \                                           │
│    $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/aicafe/user-service:latest           │
│                                                                                      │
│  # Push to ECR                                                                     │
│  docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/aicafe/user-service:latest │
│  ```                                                                           │
│                                                                                      │
│  STEP 2: Tạo EKS Cluster                                                           │
│  ═══════════════════════                                                             │
│                                                                                      │
│  ```bash                                                                           │
│  # Tạo cluster (hoặc dùng Terraform/EKSCTL)                                       │
│  eksctl create cluster \                                                            │
│    --name aicafe-prod \                                                            │
│    --region ap-southeast-1 \                                                       │
│    --nodegroup-name workers \                                                      │
│    --nodes 3 \                                                                    │
│    --nodes-min 2 \                                                                 │
│    --nodes-max 5 \                                                                 │
│    --node-type t3.medium                                                           │
│  ```                                                                           │
│                                                                                      │
│  STEP 3: Deploy lên EKS                                                            │
│  ═══════════════════                                                                │
│                                                                                      │
│  ```bash                                                                           │
│  # Update kubeconfig                                                                │
│  aws eks update-kubeconfig --name aicafe-prod --region ap-southeast-1              │
│                                                                                      │
│  # Deploy Kubernetes manifests                                                      │
│  kubectl apply -f infrastructure/k8s/production/                                    │
│  ```                                                                           │
│                                                                                      │
│  STEP 4: Migration Database                                                          │
│  ═════════════════════                                                             │
│                                                                                      │
│  ```bash                                                                           │
│  # 1. Dump database từ VPS                                                         │
│  pg_dump -h localhost -U aicafe aicafe_prod > backup.sql                          │
│                                                                                      │
│  # 2. Restore lên RDS                                                              │
│  psql -h rds.amazonaws.com -U admin -d aicafe_prod < backup.sql                  │
│  ```                                                                           │
│                                                                                      │
│  STEP 5: Update DNS sau khi verify                                                  │
│  ══════════════════════════════════                                                 │
│                                                                                      │
│  ```bash                                                                           │
│  # Trỏ DNS về AWS ELB                                                              │
│  # api.aicafe.vn → AWS Load Balancer DNS                   │
│  # aicafe.vn → CloudFront distribution                                        │
│  ```                                                                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Code Structure Giữ Nguyên

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         PROJECT STRUCTURE (UNCHANGED!)                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ai-cafe-platform/                                                                 │
│  │                                                                                  │
│  ├── services/                    # ✅ Không đổi                                 │
│  │   ├── user-service/           # Java Spring Boot                              │
│  │   ├── order-service/         # Java Spring Boot                              │
│  │   ├── payment-service/       # Java Spring Boot                              │
│  │   ├── auth-service/          # Go                                           │
│  │   ├── credit-service/        # Go                                           │
│  │   ├── ai-gateway/            # Node.js                                      │
│  │   └── notification-service/  # Go                                           │
│  │                                                                                  │
│  ├── apps/                      # ✅ Không đổi                                 │
│  │   ├── web/                   # Next.js Customer App                          │
│  │   ├── admin/                 # Next.js Admin Dashboard                        │
│  │   ├── mobile/                # React Native / Expo                          │
│  │   └── pos/                   # React Native / Expo                          │
│  │                                                                                  │
│  ├── infrastructure/            # ⚠️ THAY ĐỔI THEO MÔI TRƯỜNG                 │
│  │   ├── docker/                # Dockerfiles (giữ nguyên)                     │
│  │   ├── local/                 # Local development configs                    │
│  │   ├── vps/                   # VPS deployment configs                      │
│  │   └── k8s/                   # Kubernetes manifests (cloud)                 │
│  │                                                                                  │
│  ├── docker-compose.yml         # ⚠️ Local only                                │
│  ├── docker-compose.prod.yml    # ⚠️ VPS/Production                             │
│  │                                                                                  │
│  └── .env                       # ⚠️ THAY ĐỔI MÔI TRƯỜNG                       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Infrastructure Files Theo Môi Trường

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE FILES BY ENVIRONMENT                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  LOCAL (Docker Compose)                                                              │
│  ═══════════════════                                                                 │
│                                                                                      │
│  infrastructure/local/                                                               │
│  ├── docker-compose.yml           # Full stack local                              │
│  ├── nginx.conf                  # Local reverse proxy                           │
│  └── prometheus.yml              # Local monitoring                             │
│                                                                                      │
│  → Chạy: docker-compose up -d                                                     │
│                                                                                      │
│  ────────────────────────────────────────────────────────────────────────────────   │
│                                                                                      │
│  VPS (Docker Compose + Nginx)                                                        │
│  ═════════════════════════════                                                      │
│                                                                                      │
│  infrastructure/vps/                                                                 │
│  ├── docker-compose.prod.yml     # Production compose                             │
│  ├── nginx.conf                 # Nginx với SSL                                  │
│  ├── ssl/                       # SSL certificates                              │
│  ├── backup.sh                  # Auto backup script                            │
│  └── monitoring/                # Prometheus + Grafana                          │
│                                                                                      │
│  → Chạy: docker-compose -f docker-compose.prod.yml up -d                          │
│                                                                                      │
│  ────────────────────────────────────────────────────────────────────────────────   │
│                                                                                      │
│  CLOUD (Kubernetes)                                                                 │
│  ═══════════════════                                                                 │
│                                                                                      │
│  infrastructure/k8s/                                                                  │
│  ├── production/                  # Production K8s manifests                      │
│  │   ├── namespace.yaml          # Namespace                                    │
│  │   ├── configmap.yaml          # ConfigMaps                                   │
│  │   ├── secret.yaml             # Secrets (encrypted)                          │
│  │   ├── deployment-*.yaml       # Deployments per service                       │
│  │   ├── service-*.yaml          # Services per service                         │
│  │   ├── ingress.yaml           # Ingress (ALB)                                 │
│  │   ├── hpa-*.yaml            # Horizontal Pod Autoscaler                     │
│  │   └── pdb.yaml              # Pod Disruption Budget                         │
│  ├── staging/                    # Staging manifests                            │
│  └── helm/                       # Helm charts (optional)                       │
│                                                                                      │
│  → Chạy: kubectl apply -f infrastructure/k8s/production/                          │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Migration Checklist

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         MIGRATION CHECKLIST                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  LOCAL → VPS                                                                        │
│  ───────────────                                                                    │
│  ☐ 1. Thuê VPS server                                                              │
│  ☐ 2. Cài đặt Ubuntu 22.04 LTS                                                   │
│  ☐ 3. Cài Docker & Docker Compose                                                  │
│  ☐ 4. Setup firewall (UFW)                                                          │
│  ☐ 5. Clone code lên VPS                                                           │
│  ☐ 6. Cấu hình .env với domain mới                                                │
│  ☐ 7. Cài đặt Nginx                                                               │
│  ☐ 8. Xin SSL certificate (Let's Encrypt)                                          │
│  ☐ 9. Cấu hình Nginx reverse proxy                                                │
│  ☐ 10. Deploy với docker-compose.prod.yml                                          │
│  ☐ 11. Test tất cả endpoints                                                       │
│  ☐ 12. Setup backup database                                                       │
│  ☐ 13. Cập nhật DNS                                                               │
│  ☐ 14. Verify SSL & domain                                                         │
│                                                                                      │
│  VPS → CLOUD                                                                        │
│  ────────────                                                                      │
│  ☐ 1. Tạo AWS/GCP account                                                         │
│  ☐ 2. Setup EKS/GKE cluster                                                        │
│  ☐ 3. Tạo RDS PostgreSQL                                                           │
│  ☐ 4. Tạo ElastiCache Redis                                                       │
│  ☐ 5. Setup MSK Kafka (hoặc dùng Confluent Cloud)                                  │
│  ☐ 6. Push Docker images lên ECR/GCR                                              │
│  ☐ 7. Deploy Kubernetes manifests                                                  │
│  ☐ 8. Setup Ingress + ACM Certificate                                              │
│  ☐ 9. Dump database từ VPS                                                        │
│  ☐ 10. Restore database lên RDS                                                   │
│  ☐ 11. Test trên staging (subdomain)                                              │
│  ☐ 12. Update DNS sang Cloud                                                       │
│  ☐ 13. Verify production                                                          │
│  ☐ 14. Setup CloudWatch monitoring                                                │
│  ☐ 15. Setup auto-scaling                                                         │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Rollback Plan

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           ROLLBACK STRATEGY                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  VPS → LOCAL (Emergency Rollback)                                                  │
│  ═════════════════════════════                                                      │
│                                                                                      │
│  Nếu migration thất bại:                                                          │
│  1. Giữ VPS cũ đang chạy (không shutdown)                                         │
│  2. Revert DNS về IP VPS cũ                                                       │
│  3. Database rollback nếu cần                                                     │
│  4. Debug và fix issues                                                           │
│  5. Thử lại khi đã sẵn sàng                                                      │
│                                                                                      │
│  CLOUD → VPS (Disaster Recovery)                                                   │
│  ═════════════════════════════                                                      │
│                                                                                      │
│  Nếu cloud gặp vấn đề:                                                           │
│  1. Giữ VPS as hot standby (luôn sync)                                           │
│  2. DNS failover về VPS                                                           │
│  3. Scale up VPS tạm thời                                                         │
│  4. Investigate cloud issue                                                        │
│  5. Khắc phục và migrate back                                                     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Tóm Tắt

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              KEY TAKEAWAYS                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ✅ CODE KHÔNG ĐỔI                                                                │
│  ├─ Docker images giống nhau ở mọi môi trường                                   │
│  ├─ Chỉ thay đổi environment variables                                           │
│  └─ Chỉ thay đổi infrastructure configuration                                    │
│                                                                                      │
│  🔄 MIGRATION ĐƠN GIẢN                                                             │
│  ├─ Local → VPS: Docker Compose đã support                                       │
│  ├─ VPS → Cloud: Docker Image push → Kubernetes deploy                           │
│  └─ Không cần rewrite code                                                        │
│                                                                                      │
│  💰 BẮT ĐẦU RẺ                                                                     │
│  ├─ Local: $0 (Dev machine đã có)                                               │
│  ├─ VPS: $40-60/month                                                            │
│  └─ Cloud: $250-500/month (khi cần scale)                                       │
│                                                                                      │
│  📈 SCALE DỄ DÀNG                                                                 │
│  ├─ VPS: Thêm RAM/CPU hoặc upgrade plan                                          │
│  └─ Cloud: Auto-scaling với Kubernetes                                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Next Steps

Bạn muốn tôi hướng dẫn chi tiết bước nào?

| Bước | Mô tả | Thời gian |
|------|--------|-----------|
| 1 | **Setup Local Development** | 1-2 giờ |
| 2 | **Setup VPS Self-Hosted** | 2-4 giờ |
| 3 | **Setup Cloud (AWS)** | 1-2 ngày |
| 4 | **Migration Local → VPS** | 2-4 giờ |
| 5 | **Migration VPS → Cloud** | 1-2 ngày |
