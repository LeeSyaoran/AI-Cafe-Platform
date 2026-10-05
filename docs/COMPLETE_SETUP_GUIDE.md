# AI Café Platform - Complete Setup Guide (5 Phần)

## Table of Contents
1. [AI Setup - Tạo tài khoản OpenAI/Anthropic/Gemini](#1-ai-setup)
2. [Docker Setup - Cài đặt Docker Desktop](#2-docker-setup)
3. [Database - Setup PostgreSQL](#3-database)
4. [AI Gateway - Code chi tiết AI chat](#4-ai-gateway)
5. [Full Demo - Chạy toàn bộ hệ thống](#5-full-demo)

---

## 1. AI Setup - Tạo tài khoản AI Providers {#1-ai-setup}

### 1.1 OpenAI (GPT-4)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         OPENAI SETUP - STEP BY STEP                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Truy cập website                                                         │
│  ═══════════════════════                                                         │
│                                                                                      │
│  🌐 Mở trình duyệt: https://platform.openai.com                                 │
│                                                                                      │
│  STEP 2: Đăng ký tài khoản                                                       │
│  ═══════════════════════════                                                         │
│                                                                                      │
│  a) Click "Sign up"                                                                │
│  b) Chọn đăng ký bằng:                                                          │
│     - Email + Password                                                           │
│     - Hoặc "Continue with Google" (nhanh hơn)                                    │
│                                                                                      │
│  STEP 3: Xác minh email                                                          │
│  ═══════════════════                                                               │
│                                                                                      │
│  a) Check email inbox                                                             │
│  b) Click link xác minh                                                          │
│                                                                                      │
│  STEP 4: Xác minh số điện thoại                                                 │
│  ═══════════════════════════════                                                  │
│                                                                                      │
│  a) Nhập số điện thoại Việt Nam: +84xxxxxxxxx                                  │
│  b) Nhận mã OTP qua SMS                                                          │
│  c) Nhập mã để xác minh                                                         │
│                                                                                      │
│  STEP 5: Thêm Credit (Nạp tiền)                                                 │
│  ═════════════════════════════                                                     │
│                                                                                      │
│  a) Truy cập: https://platform.openai.com/account/billing/overview              │
│  b) Click "Add payment method"                                                   │
│  c) Nhập thông tin thẻ (Visa/Mastercard)                                        │
│  d) Chọn số tiền nạp: $10-20 (đủ để dev)                                     │
│                                                                                      │
│  ⚠️ LƯU Ý: Đặt Budget Limit!                                                    │
│  ─────────────────────────────                                                     │
│  a) Truy cập: https://platform.openai.com/account/billing/limits               │
│  b) Set "Monthly limit": $10-20                                                  │
│  c) Prevent phát sinh chi phí không mong muốn                                   │
│                                                                                      │
│  STEP 6: Tạo API Key                                                             │
│  ═══════════════════                                                               │
│                                                                                      │
│  a) Truy cập: https://platform.openai.com/api-keys                              │
│  b) Click "Create new secret key"                                                 │
│  c) Đặt tên: "aicafe-dev"                                                       │
│  d) Click "Create secret key"                                                     │
│  e) ⭐ COPY NGAY VÀ LƯU CẨN THẬN!                                               │
│     (Chỉ hiển thị 1 LẦN DUY NHẤT!)                                             │
│                                                                                      │
│  📝 API Key sẽ có format: sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx                      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Anthropic (Claude)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         ANTHROPIC SETUP - STEP BY STEP                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Truy cập website                                                         │
│  ═══════════════════════                                                         │
│                                                                                      │
│  🌐 Mở trình duyệt: https://console.anthropic.com                              │
│                                                                                      │
│  STEP 2: Đăng ký tài khoản                                                       │
│  ═══════════════════════════                                                         │
│                                                                                      │
│  a) Click "Sign up"                                                                │
│  b) Chọn "Continue with Google" (nhanh nhất)                                      │
│     Hoặc đăng ký bằng email                                                      │
│                                                                                      │
│  STEP 3: Tạo API Key                                                             │
│  ═══════════════════                                                               │
│                                                                                      │
│  a) Truy cập: https://console.anthropic.com/settings/keys                      │
│  b) Click "Create Key"                                                            │
│  c) Đặt tên: "aicafe-dev"                                                        │
│  d) Chọn permissions: "Developer"                                                 │
│  e) Click "Create"                                                                 │
│  f) ⭐ COPY NGAY! (Chỉ hiển thị 1 lần)                                           │
│                                                                                      │
│  STEP 4: Cài đặt Billing (Optional)                                               │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  a) Truy cập: https://console.anthropic.com/settings/billing                   │
│  b) Thêm thẻ để dùng production                                                  │
│                                                                                      │
│  💰 Pricing:                                                                      │
│  ├─ Claude 3 Opus: $0.015/1K input, $0.075/1K output                           │
│  ├─ Claude 3 Sonnet: $0.003/1K input, $0.015/1K output                         │
│  └─ Claude 3 Haiku: $0.00025/1K input, $0.00125/1K output                      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 Google AI (Gemini) - FREE!

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         GOOGLE AI SETUP - STEP BY STEP (FREE!)                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Truy cập website                                                         │
│  ═══════════════════════                                                         │
│                                                                                      │
│  🌐 Mở trình duyệt: https://aistudio.google.com                                │
│                                                                                      │
│  STEP 2: Đăng nhập Google                                                        │
│  ═══════════════════════════                                                         │
│                                                                                      │
│  a) Click "Sign in"                                                                │
│  b) Dùng tài khoản Google của bạn                                                 │
│                                                                                      │
│  STEP 3: Tạo API Key (Miễn phí!)                                                │
│  ═══════════════════════════════════                                               │
│                                                                                      │
│  a) Truy cập: https://aistudio.google.com/app/apikeys                          │
│  b) Click "Create API Key"                                                        │
│  c) Click "Create API key in new project"                                         │
│  d) ⭐ COPY API KEY!                                                              │
│                                                                                      │
│  ✅ Đã xong! Không cần credit card!                                               │
│                                                                                      │
│  💰 FREE TIER (Rất hậu hĩ!):                                                     │
│  ├─ Gemini 1.0 Pro: 60 requests/min, 1M tokens/month                           │
│  ├─ Gemini 1.5 Pro: 60 requests/min, 1M tokens/month                           │
│  └─ Gemini 1.5 Flash: 15 requests/min, 1M tokens/month                          │
│                                                                                      │
│  🎁 Google cho $300 credit khi đăng ký Google Cloud mới!                          │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.4 Hugging Face (Open Source) - FREE!

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         HUGGING FACE SETUP - STEP BY STEP (FREE!)                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Truy cập website                                                         │
│  ═══════════════════════                                                         │
│                                                                                      │
│  🌐 Mở trình duyệt: https://huggingface.co/join                               │
│                                                                                      │
│  STEP 2: Đăng ký (Miễn phí 100%)                                                │
│  ═══════════════════════════════════                                               │
│                                                                                      │
│  a) Click "Sign up"                                                                │
│  b) Nhập email, username, password                                                │
│  c) Verify email                                                                   │
│                                                                                      │
│  STEP 3: Tạo Access Token                                                         │
│  ═══════════════════════════                                                       │
│                                                                                      │
│  a) Truy cập: https://huggingface.co/settings/tokens                             │
│  b) Click "New token"                                                             │
│  c) Name: "aicafe-access"                                                        │
│  d) Token type: "Read" (đủ cho việc sử dụng)                                    │
│  e) Click "Generate token"                                                         │
│  f) ⭐ COPY TOKEN!                                                               │
│                                                                                      │
│  STEP 4: (Optional) Inference API Pro                                              │
│  ═══════════════════════════════════════                                           │
│                                                                                      │
│  a) Truy cập: https://huggingface.co/inference-endpoints                         │
│  b) Có free tier: 30K tokens/month                                               │
│                                                                                      │
│  🆓 Popular Free Models:                                                          │
│  ├─ Mistral 7B Instruct                                                          │
│  ├─ Llama 2 7B Chat                                                              │
│  ├─ Falcon 7B Instruct                                                            │
│  └─ Code Llama 7B                                                                │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.5 Tổng Hợp API Keys

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         SUMMARY - API KEYS                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Sau khi hoàn thành, bạn sẽ có:                                                   │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐  │
│  │ Provider      │ API Key Format           │ Cost        │ Setup Time          │  │
│  │ ────────────┼────────────────────────┼────────────┼─────────────│  │
│  │ OpenAI      │ sk-xxxxxxxxxxxxxxxx    │ $0.002-0.06│ 5 phút      │  │
│  │ Anthropic   │ sk-ant-api03-xxx       │ $0.001-0.075│ 3 phút      │  │
│  │ Google AI   │ AIzaSyxxxxxxxx          │ FREE (1M/mo) │ 2 phút      │  │
│  │ HuggingFace │ hf_xxxxxxxxxxxx        │ FREE        │ 2 phút      │  │
│  └─────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                      │
│  ✅ Recommendation: Bắt đầu với Google AI (FREE!) để test                        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Docker Setup - Cài đặt Docker Desktop {#2-docker-setup}

### 2.1 Kiểm tra yêu cầu hệ thống

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         SYSTEM REQUIREMENTS                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  WINDOWS:                                                                            │
│  ───────                                                                             │
│  ✅ Windows 10/11 Pro, Enterprise, hoặc Education                                  │
│  ✅ 64-bit processor                                                                 │
│  ✅ 4GB RAM minimum (8GB recommended)                                               │
│  ✅ 20GB available disk space                                                       │
│  ✅ WSL 2 enabled                                                                  │
│  ✅ Virtualization enabled in BIOS                                                  │
│                                                                                      │
│  MAC:                                                                                │
│  ───                                                                                 │
│  ✅ macOS 10.15 (Catalina) hoặc mới hơn                                           │
│  ✅ Apple M1/M2/M3 (ARM64) hoặc Intel                                             │
│  ✅ 4GB RAM minimum (8GB recommended)                                               │
│  ✅ 8GB disk space                                                                  │
│                                                                                      │
│  LINUX:                                                                              │
│  ─────                                                                               │
│  ✅ Ubuntu, Debian, Fedora, Raspbian                                                │
│  ✅ 4GB RAM                                                                         │
│  ✅ 20GB disk space                                                                 │
│  ✅ Virtualization (thường auto-enabled)                                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Windows: Cài đặt WSL 2 (BẮT BUỘC)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         WSL 2 SETUP (Windows)                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Mở PowerShell AS ADMIN                                                    │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  a) Right-click Start Menu                                                          │
│  b) Chọn "Terminal (Admin)" hoặc "PowerShell (Admin)"                              │
│                                                                                      │
│  STEP 2: Cài WSL 2                                                                 │
│  ═══════════════════                                                                 │
│                                                                                      │
│  Copy và paste command sau:                                                         │
│                                                                                      │
│  wsl --install                                                                   │
│                                                                                      │
│  ⏳ Đợi download và install hoàn tất (~5-10 phút)                                  │
│                                                                                      │
│  STEP 3: Restart máy                                                              │
│  ═══════════════════                                                                 │
│                                                                                      │
│  a) Restart computer                                                                │
│  b) Sau khi restart, Ubuntu sẽ tự khởi động                                        │
│  c) Tạo username và password cho Ubuntu                                             │
│                                                                                      │
│  STEP 4: Verify WSL 2                                                              │
│  ═══════════════════════════                                                       │
│                                                                                      │
│  Mở PowerShell và gõ:                                                              │
│                                                                                      │
│  wsl -l -v                                                               │
│                                                                                      │
│  Kết quả mong đợi:                                                                 │
│  NAME                   STATE           VERSION                                      │
│  * Ubuntu                 Running         2                                         │
│                                                                                      │
│  ⚠️ Nếu VERSION = 1, chạy: wsl --set-version Ubuntu 2                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Cài đặt Docker Desktop

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DOCKER DESKTOP INSTALLATION                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Download Docker Desktop                                                    │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  🌐 Truy cập: https://www.docker.com/products/docker-desktop/                  │
│  Click "Download for Windows" (hoặc Mac)                                          │
│                                                                                      │
│  STEP 2: Chạy Installer                                                           │
│  ═══════════════════                                                                 │
│                                                                                      │
│  a) Double-click file: Docker Desktop Installer.exe                                │
│  b) ✅ Tick "Use WSL 2 instead of Hyper-V" (Windows)                              │
│  c) Click "Install"                                                                │
│  d) ⏳ Đợi install hoàn tất (~5-10 phút)                                          │
│  e) Click "Close and restart"                                                      │
│                                                                                      │
│  STEP 3: Khởi động Docker Desktop                                                 │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  a) Tìm "Docker Desktop" trong Start Menu                                         │
│  b) Click để khởi động                                                           │
│  c) Đợi icon Docker whale hiện ổn định trong system tray (không có !)            │
│                                                                                      │
│  STEP 4: Verify Installation                                                        │
│  ═════════════════════════════                                                     │
│                                                                                      │
│  Mở PowerShell hoặc Terminal, gõ:                                                 │
│                                                                                      │
│  docker --version                                                                │
│  docker-compose --version                                                        │
│                                                                                      │
│  Kết quả mong đợi:                                                                 │
│  Docker version 24.x.x                                                            │
│  Docker Compose version v2.x.x                                                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.4 Docker Desktop Configuration

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DOCKER DESKTOP SETTINGS                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Mở Docker Desktop Settings                                                │
│  ═══════════════════════════════════                                               │
│                                                                                      │
│  a) Right-click Docker icon trong system tray                                      │
│  b) Chọn "Settings" hoặc "Preferences"                                           │
│                                                                                      │
│  STEP 2: Cấu hình Resources                                                       │
│  ═══════════════════════════                                                       │
│                                                                                      │
│  Settings → Resources                                                               │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐  │
│  │ Memory: 4GB minimum (8GB recommended)                                    │  │
│  │ CPUs: 4 (hoặc half of your cores)                                       │  │
│  │ Swap: 1GB                                                                │  │
│  │ Disk image size: 64GB (or more)                                          │  │
│  │ Disk image location: (default is fine)                                    │  │
│  └─────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                      │
│  STEP 3: Enable Kubernetes (Optional)                                               │
│  ═══════════════════════════════════════                                           │
│                                                                                      │
│  Settings → Kubernetes                                                              │
│  ✅ Enable Kubernetes                                                             │
│  Click "Apply & Restart"                                                           │
│                                                                                      │
│  STEP 4: Setup Docker Hub Login                                                    │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  a) Settings → Account                                                            │
│  b) Sign in with Docker Hub account (hoặc tạo mới)                                │
│  c) Đăng nhập để pull private images                                              │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.5 Test Docker

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DOCKER TEST - HELLO WORLD                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Mở Terminal (PowerShell/Command Prompt/Linux Terminal)                             │
│                                                                                      │
│  Chạy command:                                                                     │
│                                                                                      │
│  docker run -d -p 80:80 --name web-server nginx                                  │
│                                                                                      │
│  Giải thích:                                                                       │
│  ├─ docker run: Chạy container mới                                                │
│  ├─ -d: Detached mode (chạy background)                                           │
│  ├─ -p 80:80: Map port 80 host → port 80 container                               │
│  ├─ --name web-server: Đặt tên container                                          │
│  └─ nginx: Image name                                                              │
│                                                                                      │
│  Kiểm tra:                                                                         │
│                                                                                      │
│  docker ps                                                               │
│  # Thấy container web-server đang chạy                                            │
│                                                                                      │
│  Mở trình duyệt: http://localhost:80                                            │
│  # Thấy trang Welcome nginx                                                       │
│                                                                                      │
│  Dọn dẹp:                                                                         │
│                                                                                      │
│  docker stop web-server                                                          │
│  docker rm web-server                                                            │
│                                                                                      │
│  ✅ Nếu thấy trang nginx → Docker đã hoạt động!                                   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Database - Setup PostgreSQL {#3-database}

### 3.1 Chạy PostgreSQL với Docker

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         POSTGRESQL WITH DOCKER                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Tạo Network cho project                                                   │
│  ═══════════════════════════════════                                               │
│                                                                                      │
│  docker network create aicafe-network                                             │
│                                                                                      │
│  STEP 2: Chạy PostgreSQL Container                                                 │
│  ═══════════════════════════════════                                               │
│                                                                                      │
│  docker run -d \                                                                │
│    --name aicafe-postgres \                                                     │
│    --network aicafe-network \                                                   │
│    -e POSTGRES_DB=aicafe_dev \                                                 │
│    -e POSTGRES_USER=aicafe \                                                    │
│    -e POSTGRES_PASSWORD=devpassword123 \                                         │
│    -p 5432:5432 \                                                               │
│    -v postgres_data:/var/lib/postgresql/data \                                    │
│    postgres:15-alpine                                                            │
│                                                                                      │
│  Giải thích:                                                                       │
│  ├─ postgres:15-alpine: Image nhẹ, nhanh                                         │
│  ├─ POSTGRES_DB: Tên database                                                    │
│  ├─ POSTGRES_USER: Username để login                                              │
│  ├─ POSTGRES_PASSWORD: Password để login                                         │
│  ├─ -p 5432:5432: Expose port để connect từ host                                │
│  └─ -v: Persist data (không mất khi restart)                                     │
│                                                                                      │
│  STEP 3: Verify PostgreSQL đang chạy                                               │
│  ═══════════════════════════════════════                                           │
│                                                                                      │
│  docker ps                                                                   │
│  # Thấy: aicafe-postgres đang running                                           │
│                                                                                      │
│  docker logs aicafe-postgres                                                  │
│  # Xem logs để debug nếu có vấn đề                                              │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Kết nối Database với DBeaver (GUI)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DBEAVER CONNECTION                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Download & Install DBeaver                                                │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  🌐 https://dbeaver.io/download/                                                  │
│  Download Community Edition (FREE)                                                 │
│                                                                                      │
│  STEP 2: Tạo Connection mới                                                       │
│  ═══════════════════════════                                                       │
│                                                                                      │
│  a) Click "New Connection" (biểu tượng +)                                         │
│  b) Search "PostgreSQL"                                                           │
│  c) Click "Next"                                                                  │
│                                                                                      │
│  STEP 3: Điền Connection Details                                                   │
│  ═══════════════════════════════                                                   │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐  │
│  │ Host: localhost                                                            │  │
│  │ Port: 5432                                                               │  │
│  │ Database: aicafe_dev                                                      │  │
│  │ Username: aicafe                                                          │  │
│  │ Password: devpassword123                                                  │  │
│  │                                                                          │  │
│  │ ☐ Save password permanently                                               │  │
│  │ ☐ Show all databases                                                     │  │
│  └─────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                      │
│  STEP 4: Test & Connect                                                            │
│  ═══════════════════════════                                                       │
│                                                                                      │
│  a) Click "Test Connection"                                                       │
│  b) Nếu thành công → "Finish"                                                    │
│  c) Database sẽ xuất hiện trong panel bên trái                                     │
│                                                                                      │
│  ✅ Đã kết nối thành công!                                                        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.3 Redis (Cache)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         REDIS WITH DOCKER                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Chạy Redis container:                                                            │
│                                                                                      │
│  docker run -d \                                                                │
│    --name aicafe-redis \                                                        │
│    --network aicafe-network \                                                   │
│    -p 6379:6379 \                                                               │
│    redis:7-alpine                                                                │
│                                                                                      │
│  Verify:                                                                         │
│  docker ps                                                                   │
│                                                                                      │
│  Test Redis CLI:                                                                 │
│  docker exec -it aicafe-redis redis-cli                                         │
│  > PING                                                                     │
│  PONG                                                                          │
│  > SET test "hello"                                                             │
│  OK                                                                           │
│  > GET test                                                                     │
│  "hello"                                                                       │
│  > EXIT                                                                         │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.4 Kafka (Message Queue)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         KAFKA WITH DOCKER                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Chạy Zookeeper và Kafka:                                                        │
│                                                                                      │
│  docker network create aicafe-network                                            │
│                                                                                      │
│  # Zookeeper                                                                     │
│  docker run -d \                                                                │
│    --name aicafe-zookeeper \                                                    │
│    --network aicafe-network \                                                   │
│    -e ZOOKEEPER_CLIENT_PORT=2181 \                                             │
│    confluentinc/cp-zookeeper:7.5.0                                             │
│                                                                                      │
│  # Kafka                                                                         │
│  docker run -d \                                                                │
│    --name aicafe-kafka \                                                        │
│    --network aicafe-network \                                                   │
│    -p 9092:9092 \                                                               │
│    -e KAFKA_BROKER_ID=1 \                                                       │
│    -e KAFKA_ZOOKEEPER_CONNECT=aicafe-zookeeper:2181 \                           │
│    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \                    │
│    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \                                │
│    confluentinc/cp-kafka:7.5.0                                                  │
│                                                                                      │
│  Verify Kafka:                                                                  │
│  docker exec -it aicafe-kafka kafka-topics --bootstrap-server localhost:9092 --list │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.5 Docker Compose cho Infrastructure

```yaml
# docker-compose.infra.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: aicafe-postgres
    environment:
      POSTGRES_DB: aicafe_dev
      POSTGRES_USER: aicafe
      POSTGRES_PASSWORD: devpassword123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - aicafe-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U aicafe"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: aicafe-redis
    ports:
      - "6379:6379"
    networks:
      - aicafe-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: aicafe-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    networks:
      - aicafe-network

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: aicafe-zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    networks:
      - aicafe-network

volumes:
  postgres_data:

networks:
  aicafe-network:
    driver: bridge
```

**Chạy:**
```bash
docker-compose -f docker-compose.infra.yml up -d
```

---

## 4. AI Gateway - Code chi tiết AI chat {#4-ai-gateway}

### 4.1 Project Structure

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         AI GATEWAY STRUCTURE                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  services/ai-gateway/                                                             │
│  ├── src/                                                                      │
│  │   ├── index.ts              # Entry point                                    │
│  │   ├── app.ts               # Express app setup                                │
│  │   ├── config/              # Configuration                                   │
│  │   │   └── index.ts                                                         │
│  │   ├── routes/              # API routes                                      │
│  │   │   ├── chat.ts                                                          │
│  │   │   ├── models.ts                                                        │
│  │   │   └── health.ts                                                        │
│  │   ├── services/            # Business logic                                  │
│  │   │   ├── ai-providers.ts   # Multi-provider AI service                      │
│  │   │   ├── cache.ts          # Redis cache                                   │
│  │   │   └── cost-tracker.ts   # Usage tracking                                │
│  │   ├── middleware/          # Express middleware                              │
│  │   │   ├── auth.ts                                                          │
│  │   │   ├── rate-limiter.ts                                                  │
│  │   │   └── error-handler.ts                                                 │
│  │   ├── types/               # TypeScript types                                │
│  │   │   └── index.ts                                                         │
│  │   └── utils/               # Utilities                                      │
│  │       └── logger.ts                                                         │
│  ├── tests/                   # Unit tests                                     │
│  ├── package.json                                                             │
│  └── tsconfig.json                                                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 package.json

```json
{
  "name": "ai-gateway",
  "version": "1.0.0",
  "description": "AI Gateway for AI Café Platform",
  "main": "dist/index.js",
  "scripts": {
    "dev": "tsx watch src/index.ts",
    "build": "tsc",
    "start": "node dist/index.js",
    "test": "jest"
  },
  "dependencies": {
    "express": "^4.18.2",
    "openai": "^4.20.0",
    "@anthropic-ai/sdk": "^0.10.0",
    "redis": "^4.6.10",
    "zod": "^3.22.4",
    "winston": "^3.11.0",
    "helmet": "^7.1.0",
    "cors": "^2.8.5",
    "dotenv": "^16.3.1",
    "uuid": "^9.0.1"
  },
  "devDependencies": {
    "@types/express": "^4.17.21",
    "@types/node": "^20.10.0",
    "@types/cors": "^2.8.17",
    "typescript": "^5.3.2",
    "tsx": "^4.6.0",
    "jest": "^29.7.0",
    "@types/jest": "^29.5.11"
  }
}
```

### 4.3 Configuration

```typescript
// src/config/index.ts
import { z } from 'zod';
import * as dotenv from 'dotenv';

dotenv.config();

const configSchema = z.object({
  // Server
  PORT: z.string().default('8086'),
  NODE_ENV: z.enum(['development', 'production']).default('development'),
  
  // AI Providers
  OPENAI_API_KEY: z.string().optional(),
  ANTHROPIC_API_KEY: z.string().optional(),
  GOOGLE_AI_API_KEY: z.string().optional(),
  HUGGINGFACE_ACCESS_TOKEN: z.string().optional(),
  
  // Database
  DATABASE_URL: z.string(),
  
  // Redis
  REDIS_URL: z.string().default('redis://localhost:6379'),
  
  // Kafka
  KAFKA_BROKERS: z.string().default('localhost:9092'),
  
  // JWT
  JWT_SECRET: z.string(),
  
  // Rate Limiting
  RATE_LIMIT_MAX: z.string().default('100'),
  RATE_LIMIT_WINDOW: z.string().default('60000'),
  
  // Cost Limits
  MAX_CREDITS_PER_USER: z.string().default('100000'),
});

export const config = configSchema.parse(process.env);

export const AI_PROVIDERS = {
  openai: {
    name: 'OpenAI',
    models: ['gpt-4', 'gpt-4-turbo-preview', 'gpt-3.5-turbo'],
    baseCostPer1K: {
      'gpt-4': { input: 0.03, output: 0.06 },
      'gpt-4-turbo-preview': { input: 0.01, output: 0.03 },
      'gpt-3.5-turbo': { input: 0.0015, output: 0.002 },
    },
  },
  anthropic: {
    name: 'Anthropic',
    models: ['claude-3-opus', 'claude-3-sonnet', 'claude-3-haiku'],
    baseCostPer1K: {
      'claude-3-opus': { input: 0.015, output: 0.075 },
      'claude-3-sonnet': { input: 0.003, output: 0.015 },
      'claude-3-haiku': { input: 0.00025, output: 0.00125 },
    },
  },
  google: {
    name: 'Google AI',
    models: ['gemini-pro', 'gemini-1.5-pro', 'gemini-1.5-flash'],
    baseCostPer1K: {
      'gemini-pro': { input: 0.00125, output: 0.00375 },
      'gemini-1.5-pro': { input: 0.00125, output: 0.005 },
      'gemini-1.5-flash': { input: 0.000075, output: 0.0003 },
    },
  },
  huggingface: {
    name: 'HuggingFace',
    models: ['mistral-7b-instruct', 'llama-2-7b-chat', 'falcon-7b-instruct'],
    baseCostPer1K: {
      // Free tier available
      'mistral-7b-instruct': { input: 0, output: 0 },
      'llama-2-7b-chat': { input: 0, output: 0 },
      'falcon-7b-instruct': { input: 0, output: 0 },
    },
  },
} as const;
```

### 4.4 AI Providers Service

```typescript
// src/services/ai-providers.ts
import OpenAI from 'openai';
import Anthropic from '@anthropic-ai/sdk';
import { HfInference } from '@huggingface/inference';
import { config, AI_PROVIDERS } from '../config';
import { logger } from '../utils/logger';

export interface ChatMessage {
  role: 'system' | 'user' | 'assistant';
  content: string;
}

export interface AIResponse {
  content: string;
  provider: string;
  model: string;
  tokens: {
    input: number;
    output: number;
    total: number;
  };
  latency: number;
  cost: number;
  cached: boolean;
}

export interface AIRequest {
  model: string;
  messages: ChatMessage[];
  temperature?: number;
  maxTokens?: number;
  userId: string;
}

class AIService {
  private openai: OpenAI | null = null;
  private anthropic: Anthropic | null = null;
  private hf: HfInference | null = null;

  constructor() {
    this.initializeProviders();
  }

  private initializeProviders() {
    // Initialize OpenAI
    if (config.OPENAI_API_KEY) {
      this.openai = new OpenAI({
        apiKey: config.OPENAI_API_KEY,
      });
      logger.info('OpenAI provider initialized');
    }

    // Initialize Anthropic
    if (config.ANTHROPIC_API_KEY) {
      this.anthropic = new Anthropic({
        apiKey: config.ANTHROPIC_API_KEY,
      });
      logger.info('Anthropic provider initialized');
    }

    // Initialize HuggingFace
    if (config.HUGGINGFACE_ACCESS_TOKEN) {
      this.hf = new HfInference(config.HUGGINGFACE_ACCESS_TOKEN);
      logger.info('HuggingFace provider initialized');
    }
  }

  async chat(request: AIRequest): Promise<AIResponse> {
    const startTime = Date.now();
    const { model, messages, userId } = request;
    const provider = this.getProviderForModel(model);

    logger.info(`AI Chat request: ${provider}/${model}`, {
      userId,
      messageCount: messages.length,
    });

    try {
      let response: AIResponse;

      switch (provider) {
        case 'openai':
          response = await this.chatWithOpenAI(model, messages, request.temperature);
          break;
        case 'anthropic':
          response = await this.chatWithAnthropic(model, messages, request.temperature);
          break;
        case 'google':
          response = await this.chatWithGoogle(model, messages, request.temperature);
          break;
        case 'huggingface':
          response = await this.chatWithHuggingFace(model, messages);
          break;
        default:
          throw new Error(`Unknown model: ${model}`);
      }

      response.latency = Date.now() - startTime;
      response.cost = this.calculateCost(provider, model, response.tokens.total);

      logger.info(`AI Chat response: ${provider}/${model}`, {
        userId,
        latency: response.latency,
        cost: response.cost,
        cached: response.cached,
      });

      return response;
    } catch (error) {
      logger.error(`AI Chat error: ${provider}/${model}`, { error, userId });
      throw error;
    }
  }

  private chatWithOpenAI(
    model: string,
    messages: ChatMessage[],
    temperature = 0.7
  ): Promise<AIResponse> {
    return new Promise(async (resolve, reject) => {
      try {
        const response = await this.openai!.chat.completions.create({
          model,
          messages: messages as any[],
          temperature,
          max_tokens: 2000,
        });

        resolve({
          content: response.choices[0].message.content || '',
          provider: 'openai',
          model,
          tokens: {
            input: response.usage?.prompt_tokens || 0,
            output: response.usage?.completion_tokens || 0,
            total: response.usage?.total_tokens || 0,
          },
          latency: 0,
          cost: 0,
          cached: false,
        });
      } catch (error) {
        reject(error);
      }
    });
  }

  private chatWithAnthropic(
    model: string,
    messages: ChatMessage[],
    temperature = 0.7
  ): Promise<AIResponse> {
    return new Promise(async (resolve, reject) => {
      try {
        const systemMessage = messages.find((m) => m.role === 'system');
        const userMessages = messages.filter((m) => m.role !== 'system');

        const response = await this.anthropic!.messages.create({
          model,
          max_tokens: 2000,
          temperature,
          system: systemMessage?.content,
          messages: userMessages as any[],
        });

        const responseText =
          response.content[0].type === 'text' ? response.content[0].text : '';

        resolve({
          content: responseText,
          provider: 'anthropic',
          model,
          tokens: {
            input: response.usage.input_tokens,
            output: response.usage.output_tokens,
            total: response.usage.input_tokens + response.usage.output_tokens,
          },
          latency: 0,
          cost: 0,
          cached: false,
        });
      } catch (error) {
        reject(error);
      }
    });
  }

  private chatWithGoogle(
    model: string,
    messages: ChatMessage[],
    temperature = 0.7
  ): Promise<AIResponse> {
    return new Promise(async (resolve, reject) => {
      try {
        // Google AI implementation using fetch API
        const response = await fetch(
          `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${config.GOOGLE_AI_API_KEY}`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              contents: messages
                .filter((m) => m.role !== 'system')
                .map((m) => ({
                  role: m.role === 'assistant' ? 'model' : 'user',
                  parts: [{ text: m.content }],
                })),
              generationConfig: {
                temperature,
                maxOutputTokens: 2000,
              },
            }),
          }
        );

        const data = await response.json();
        const content = data.candidates?.[0]?.content?.parts?.[0]?.text || '';

        resolve({
          content,
          provider: 'google',
          model,
          tokens: {
            input: data.usageMetadata?.promptTokenCount || 0,
            output: data.usageMetadata?.candidatesTokenCount || 0,
            total: data.usageMetadata?.totalTokenCount || 0,
          },
          latency: 0,
          cost: 0,
          cached: false,
        });
      } catch (error) {
        reject(error);
      }
    });
  }

  private chatWithHuggingFace(
    model: string,
    messages: ChatMessage[]
  ): Promise<AIResponse> {
    return new Promise(async (resolve, reject) => {
      try {
        const conversation = messages
          .filter((m) => m.role !== 'system')
          .map((m) => `${m.role}: ${m.content}`)
          .join('\n');

        const response = await this.hf!.textGeneration({
          model,
          inputs: conversation,
          parameters: {
            max_new_tokens: 500,
            return_full_text: false,
          },
        });

        resolve({
          content: response.generated_text,
          provider: 'huggingface',
          model,
          tokens: {
            input: Math.ceil(conversation.length / 4), // Approximate
            output: Math.ceil(response.generated_text.length / 4),
            total: 0,
          },
          latency: 0,
          cost: 0,
          cached: false,
        });
      } catch (error) {
        reject(error);
      }
    });
  }

  private getProviderForModel(model: string): string {
    for (const [provider, config] of Object.entries(AI_PROVIDERS)) {
      if (config.models.includes(model)) {
        return provider;
      }
    }
    throw new Error(`Unknown model: ${model}`);
  }

  private calculateCost(provider: string, model: string, tokens: number): number {
    const providerConfig = AI_PROVIDERS[provider as keyof typeof AI_PROVIDERS];
    if (!providerConfig) return 0;

    const modelCost = providerConfig.baseCostPer1K[model as keyof typeof providerConfig.baseCostPer1K];
    if (!modelCost) return 0;

    // Average input/output cost
    const costPerToken = (modelCost.input + modelCost.output) / 2 / 1000;
    return tokens * costPerToken;
  }

  async chatWithFallback(messages: ChatMessage[], userId: string): Promise<AIResponse> {
    const providers = [
      { provider: 'openai', models: ['gpt-3.5-turbo', 'gpt-4'] },
      { provider: 'anthropic', models: ['claude-3-sonnet', 'claude-3-haiku'] },
      { provider: 'google', models: ['gemini-pro', 'gemini-1.5-flash'] },
      { provider: 'huggingface', models: ['mistral-7b-instruct'] },
    ];

    for (const { provider, models } of providers) {
      const model = models[0];
      try {
        const response = await this.chat({ model, messages, userId });
        return response;
      } catch (error) {
        logger.warn(`Provider ${provider} failed, trying next...`, { error });
        continue;
      }
    }

    throw new Error('All AI providers failed');
  }

  getAvailableModels() {
    return Object.entries(AI_PROVIDERS).flatMap(([provider, config]) =>
      config.models.map((model) => ({
        id: model,
        name: this.getModelDisplayName(model),
        provider,
        providerName: config.name,
        costPer1K: config.baseCostPer1K[model as keyof typeof config.baseCostPer1K],
      }))
    );
  }

  private getModelDisplayName(model: string): string {
    const names: Record<string, string> = {
      'gpt-4': 'GPT-4',
      'gpt-4-turbo-preview': 'GPT-4 Turbo',
      'gpt-3.5-turbo': 'GPT-3.5 Turbo',
      'claude-3-opus': 'Claude 3 Opus',
      'claude-3-sonnet': 'Claude 3 Sonnet',
      'claude-3-haiku': 'Claude 3 Haiku',
      'gemini-pro': 'Gemini Pro',
      'gemini-1.5-pro': 'Gemini 1.5 Pro',
      'gemini-1.5-flash': 'Gemini 1.5 Flash',
      'mistral-7b-instruct': 'Mistral 7B',
      'llama-2-7b-chat': 'Llama 2 7B',
      'falcon-7b-instruct': 'Falcon 7B',
    };
    return names[model] || model;
  }
}

export const aiService = new AIService();
```

### 4.5 API Routes

```typescript
// src/routes/chat.ts
import { Router, Request, Response, NextFunction } from 'express';
import { aiService } from '../services/ai-providers';
import { authMiddleware } from '../middleware/auth';
import { rateLimiter } from '../middleware/rate-limiter';
import { z } from 'zod';
import { v4 as uuidv4 } from 'uuid';
import { cacheService } from '../services/cache';
import { costTracker } from '../services/cost-tracker';
import { logger } from '../utils/logger';

const router = Router();

// Validation schema
const chatSchema = z.object({
  model: z.string().min(1),
  messages: z
    .array(
      z.object({
        role: z.enum(['system', 'user', 'assistant']),
        content: z.string().min(1),
      })
    )
    .min(1),
  temperature: z.number().min(0).max(2).optional().default(0.7),
  sessionId: z.string().optional(),
  stream: z.boolean().optional().default(false),
});

// POST /api/v1/ai/chat
router.post(
  '/chat',
  authMiddleware,
  rateLimiter({ max: 50, windowMs: 60000 }),
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const userId = req.user!.id;

      // Validate request
      const validation = chatSchema.safeParse(req.body);
      if (!validation.success) {
        res.status(400).json({
          error: 'Invalid request',
          details: validation.error.issues,
        });
        return;
      }

      const { model, messages, temperature, sessionId } = validation.data;

      // Check user credits
      const userCredits = await costTracker.getUserCredits(userId);
      if (userCredits <= 0) {
        res.status(402).json({
          error: 'Insufficient credits',
          message: 'Please purchase more credits to continue using AI services.',
        });
        return;
      }

      // Check cache
      const cacheKey = cacheService.generateCacheKey(model, messages);
      const cachedResponse = await cacheService.get(cacheKey);
      if (cachedResponse) {
        logger.info('Returning cached response', { userId, model });
        res.json({
          ...cachedResponse,
          cached: true,
          remainingCredits: userCredits,
        });
        return;
      }

      // Call AI
      const response = await aiService.chat({
        model,
        messages,
        temperature,
        userId,
      });

      // Calculate cost and deduct credits
      const costInCredits = Math.ceil(response.tokens.total / 10); // 1 credit per 10 tokens
      await costTracker.deductCredits(userId, costInCredits);

      // Track usage
      await costTracker.trackUsage(userId, {
        model,
        provider: response.provider,
        tokens: response.tokens.total,
        cost: response.cost,
        cached: response.cached,
      });

      // Save to chat history (async)
      saveChatHistory(userId, sessionId, model, messages, response).catch((err) =>
        logger.error('Failed to save chat history', { error: err })
      );

      // Cache response (if not streaming)
      if (!req.body.stream) {
        await cacheService.set(cacheKey, response, 3600); // 1 hour
      }

      // Return response
      res.json({
        id: uuidv4(),
        model,
        provider: response.provider,
        content: response.content,
        tokens: response.tokens,
        cost: response.cost,
        creditsUsed: costInCredits,
        remainingCredits: userCredits - costInCredits,
        cached: false,
        created: new Date().toISOString(),
      });
    } catch (error: any) {
      logger.error('Chat error', { error: error.message });
      res.status(500).json({
        error: 'AI service error',
        message: error.message,
      });
    }
  }
);

// GET /api/v1/ai/models
router.get('/models', async (req: Request, res: Response) => {
  const models = aiService.getAvailableModels();
  res.json({
    models: models.map((m) => ({
      id: m.id,
      name: m.name,
      provider: m.provider,
      providerName: m.providerName,
      inputCost: m.costPer1K.input,
      outputCost: m.costPer1K.output,
    })),
  });
});

// GET /api/v1/ai/history
router.get('/history', authMiddleware, async (req: Request, res: Response) => {
  const userId = req.user!.id;
  const { sessionId, limit = 50, offset = 0 } = req.query;

  // Get chat history from database
  // Implementation depends on your database setup
  const history = await getChatHistory(userId, {
    sessionId: sessionId as string,
    limit: Number(limit),
    offset: Number(offset),
  });

  res.json(history);
});

// POST /api/v1/ai/chat/stream
router.post(
  '/chat/stream',
  authMiddleware,
  rateLimiter({ max: 20, windowMs: 60000 }),
  async (req: Request, res: Response, next: NextFunction) => {
    try {
      const userId = req.user!.id;
      const { model, messages, temperature = 0.7 } = req.body;

      // Set headers for SSE
      res.setHeader('Content-Type', 'text/event-stream');
      res.setHeader('Cache-Control', 'no-cache');
      res.setHeader('Connection', 'keep-alive');

      // Implementation for streaming would go here
      // Using Server-Sent Events (SSE) or WebSocket

      res.end();
    } catch (error: any) {
      res.status(500).json({ error: error.message });
    }
  }
);

async function saveChatHistory(
  userId: string,
  sessionId: string | undefined,
  model: string,
  messages: any[],
  response: any
) {
  // Save to database
  // Implementation depends on your database setup
  logger.info('Chat history saved', { userId, sessionId, model });
}

async function getChatHistory(userId: string, options: any) {
  // Get from database
  return { sessions: [], total: 0 };
}

export default router;
```

### 4.6 Express App

```typescript
// src/app.ts
import express, { Express, Request, Response, NextFunction } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import { config } from './config';
import { logger } from './utils/logger';
import chatRoutes from './routes/chat';
import healthRoutes from './routes/health';
import { errorHandler } from './middleware/error-handler';

export function createApp(): Express {
  const app = express();

  // Security middleware
  app.use(helmet());
  app.use(
    cors({
      origin: config.ALLOWED_ORIGINS?.split(',') || ['http://localhost:3000'],
      credentials: true,
    })
  );

  // Body parsing
  app.use(express.json({ limit: '10mb' }));
  app.use(express.urlencoded({ extended: true }));

  // Request logging
  app.use((req: Request, res: Response, next: NextFunction) => {
    const start = Date.now();
    res.on('finish', () => {
      const duration = Date.now() - start;
      logger.info('Request', {
        method: req.method,
        path: req.path,
        status: res.statusCode,
        duration: `${duration}ms`,
      });
    });
    next();
  });

  // Routes
  app.use('/health', healthRoutes);
  app.use('/api/v1/ai', chatRoutes);

  // Error handler
  app.use(errorHandler);

  // 404 handler
  app.use((req: Request, res: Response) => {
    res.status(404).json({ error: 'Not found' });
  });

  return app;
}
```

### 4.7 Entry Point

```typescript
// src/index.ts
import { createApp } from './app';
import { config } from './config';
import { logger } from './utils/logger';
import { cacheService } from './services/cache';

async function main() {
  try {
    // Initialize cache connection
    await cacheService.connect();

    // Create and start app
    const app = createApp();

    const server = app.listen(config.PORT, () => {
      logger.info(`AI Gateway started on port ${config.PORT}`, {
        nodeEnv: config.NODE_ENV,
      });
    });

    // Graceful shutdown
    const shutdown = async () => {
      logger.info('Shutting down...');
      server.close();
      await cacheService.disconnect();
      process.exit(0);
    };

    process.on('SIGTERM', shutdown);
    process.on('SIGINT', shutdown);
  } catch (error) {
    logger.error('Failed to start server', { error });
    process.exit(1);
  }
}

main();
```

### 4.8 Middleware

```typescript
// src/middleware/auth.ts
import { Request, Response, NextFunction } from 'express';
import { config } from '../config';
import { logger } from '../utils/logger';

export interface AuthUser {
  id: string;
  email: string;
  role: string;
}

declare global {
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

export function authMiddleware(
  req: Request,
  res: Response,
  next: NextFunction
) {
  // Get token from header
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({ error: 'Unauthorized - No token provided' });
    return;
  }

  const token = authHeader.substring(7);

  try {
    // In production, verify JWT token
    // For now, decode token (simplified)
    const user = decodeToken(token);

    if (!user) {
      res.status(401).json({ error: 'Unauthorized - Invalid token' });
      return;
    }

    req.user = user;
    next();
  } catch (error) {
    logger.error('Auth error', { error });
    res.status(401).json({ error: 'Unauthorized' });
  }
}

function decodeToken(token: string): AuthUser | null {
  // Simplified token decoding
  // In production, use proper JWT verification
  try {
    // This is a placeholder - implement proper JWT verification
    return {
      id: 'user-123',
      email: 'user@example.com',
      role: 'user',
    };
  } catch {
    return null;
  }
}
```

```typescript
// src/middleware/rate-limiter.ts
import { Request, Response, NextFunction } from 'express';
import { config } from '../config';
import { cacheService } from '../services/cache';
import { logger } from '../utils/logger';

interface RateLimiterOptions {
  max: number;
  windowMs: number;
}

export function rateLimiter(options: RateLimiterOptions) {
  return async (req: Request, res: Response, next: NextFunction) => {
    const userId = req.user?.id || req.ip;
    const key = `ratelimit:${userId}:${req.path}`;

    try {
      const current = await cacheService.increment(key, options.windowMs);

      if (current > options.max) {
        logger.warn('Rate limit exceeded', { userId, key, count: current });
        res.status(429).json({
          error: 'Too many requests',
          message: `Rate limit exceeded. Try again in ${Math.ceil(options.windowMs / 1000)} seconds.`,
          retryAfter: Math.ceil(options.windowMs / 1000),
        });
        return;
      }

      res.setHeader('X-RateLimit-Limit', options.max);
      res.setHeader('X-RateLimit-Remaining', Math.max(0, options.max - current));
      next();
    } catch (error) {
      // If Redis fails, allow request (fail open)
      logger.error('Rate limiter error', { error });
      next();
    }
  };
}
```

---

## 5. Full Demo - Chạy toàn bộ hệ thống {#5-full-demo}

### 5.1 Chuẩn bị Environment

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         FULL DEMO SETUP                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  STEP 1: Clone Project                                                              │
│  ═══════════════════                                                                 │
│                                                                                      │
│  git clone https://github.com/your-org/ai-cafe-platform.git                      │
│  cd ai-cafe-platform                                                               │
│                                                                                      │
│  STEP 2: Tạo .env file                                                             │
│  ═══════════════════                                                                 │
│                                                                                      │
│  cp .env.example .env                                                              │
│                                                                                      │
│  STEP 3: Điền API Keys                                                            │
│  ═══════════════════                                                                 │
│                                                                                      │
│  Mở .env và điền các keys đã tạo ở Phần 1:                                       │
│                                                                                      │
│  OPENAI_API_KEY=sk-your-key-here                                                 │
│  ANTHROPIC_API_KEY=sk-ant-your-key-here                                           │
│  GOOGLE_AI_API_KEY=AIza-your-key-here                                             │
│  HUGGINGFACE_ACCESS_TOKEN=hf-your-key-here                                        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Start Infrastructure

```bash
# Chạy tất cả infrastructure (PostgreSQL, Redis, Kafka)
docker-compose -f docker-compose.infra.yml up -d

# Verify all services are running
docker ps

# Kết quả mong đợi:
# CONTAINER ID   IMAGE                  STATUS
# xxx            postgres:15-alpine    Up (healthy)
# xxx            redis:7-alpine        Up (healthy)
# xxx            cp-kafka:7.5.0       Up
# xxx            cp-zookeeper:7.5.0    Up
```

### 5.3 Start AI Gateway

```bash
# Navigate to AI Gateway
cd services/ai-gateway

# Install dependencies
npm install

# Start in development mode
npm run dev

# Hoặc chạy production
npm run build
npm start

# Output mong đợi:
# AI Gateway started on port 8086
# OpenAI provider initialized
# Anthropic provider initialized
# HuggingFace provider initialized
```

### 5.4 Test AI Gateway

```bash
# Test health endpoint
curl http://localhost:8086/health

# Kết quả:
# {"status":"ok","timestamp":"2024-01-01T00:00:00.000Z"}

# Test models endpoint
curl http://localhost:8086/api/v1/ai/models

# Kết quả:
# {"models":[
#   {"id":"gpt-3.5-turbo","name":"GPT-3.5 Turbo","provider":"openai",...},
#   {"id":"claude-3-sonnet","name":"Claude 3 Sonnet","provider":"anthropic",...},
#   ...
# ]}

# Test chat (với mock token)
curl -X POST http://localhost:8086/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer mock-token" \
  -d '{
    "model": "gpt-3.5-turbo",
    "messages": [
      {"role": "user", "content": "Hello, how are you?"}
    ]
  }'

# Kết quả mong đợi:
# {
#   "id": "uuid",
#   "model": "gpt-3.5-turbo",
#   "provider": "openai",
#   "content": "Hello! I'm doing well, thank you for asking...",
#   "tokens": {"input": 15, "output": 25, "total": 40},
#   "remainingCredits": 9999
# }
```

### 5.5 Start Frontend

```bash
# Mở terminal mới

# Navigate to web app
cd apps/web

# Install dependencies
npm install

# Start development
npm run dev

# Mở trình duyệt: http://localhost:3000
```

### 5.6 Demo Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DEMO FLOW                                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  1. Mở trình duyệt http://localhost:3000                                          │
│                                                                                      │
│  2. Đăng nhập / Đăng ký tài khoản                                                │
│                                                                                      │
│  3. Vào trang AI Chat                                                              │
│                                                                                      │
│  4. Chọn model:                                                                   │
│     ├─ GPT-3.5 Turbo (Nhanh, rẻ)                                                │
│     ├─ GPT-4 (Mạnh, đắt hơn)                                                    │
│     ├─ Claude 3 Sonnet (Cân bằng)                                               │
│     └─ Gemini Pro (Miễn phí!)                                                     │
│                                                                                      │
│  5. Gửi message                                                                  │
│                                                                                      │
│  6. Xem kết quả + credits đã dùng                                                │
│                                                                                      │
│  7. Kiểm tra lịch sử chat                                                        │
│                                                                                      │
│  8. Test fallback: Tắt 1 AI provider, chat vẫn hoạt động                         │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.7 Full Stack Command

```bash
#!/bin/bash
# ================================================
# AI CAFE PLATFORM - FULL STARTUP
# ================================================

echo "🚀 Starting AI Café Platform..."

# 1. Start Infrastructure
echo "📦 Starting infrastructure (Postgres, Redis, Kafka)..."
docker-compose -f docker-compose.infra.yml up -d
echo "✅ Infrastructure started"

# 2. Wait for services
echo "⏳ Waiting for services..."
sleep 10

# 3. Start AI Gateway
echo "🤖 Starting AI Gateway..."
cd services/ai-gateway
npm install 2>/dev/null
npm run dev &
cd ../..

# 4. Start other backend services (if needed)
# cd services/user-service && ./mvnw spring-boot:run &
# cd services/auth-service && go run cmd/server/main.go &

# 5. Start Frontend
echo "🌐 Starting Web App..."
cd apps/web
npm install 2>/dev/null
npm run dev &
cd ../..

echo ""
echo "✅ All services started!"
echo ""
echo "🌐 Access URLs:"
echo "   Web App:     http://localhost:3000"
echo "   AI Gateway:  http://localhost:8086"
echo "   API Docs:   http://localhost:8086/docs"
echo ""
echo "📊 Check status:"
echo "   docker ps"
echo "   curl http://localhost:8086/health"
echo ""
```

### 5.8 Expected Results

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         EXPECTED OUTPUTS                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  DOCKER PS:                                                                       │
│  ──────────                                                                       │
│  aicafe-postgres    postgres:15-alpine    Up (healthy)                           │
│  aicafe-redis       redis:7-alpine        Up (healthy)                           │
│  aicafe-kafka       cp-kafka:7.5.0        Up                                     │
│  aicafe-zookeeper   cp-zookeeper:7.5.0    Up                                     │
│                                                                                      │
│  HEALTH CHECK:                                                                     │
│  ───────────                                                                      │
│  $ curl http://localhost:8086/health                                               │
│  {"status":"ok","timestamp":"2024-01-01T00:00:00.000Z","services":{"postgres":"up","redis":"up","kafka":"up"}} │
│                                                                                      │
│  CHAT API:                                                                        │
│  ────────                                                                         │
│  $ curl -X POST http://localhost:8086/api/v1/ai/chat \                            │
│      -H "Authorization: Bearer $TOKEN" \                                          │
│      -d '{"model":"gemini-pro","messages":[{"role":"user","content":"Hello"}]}'   │
│                                                                                      │
│  {                                                                                 │
│    "id": "550e8400-e29b-41d4-a716-446655440000",                                │
│    "model": "gemini-pro",                                                         │
│    "provider": "google",                                                          │
│    "content": "Hello! How can I help you today?",                                 │
│    "tokens": { "input": 5, "output": 10, "total": 15 },                         │
│    "cost": 0,                                                                     │
│    "remainingCredits": 10000                                                      │
│  }                                                                                 │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Troubleshooting

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         TROUBLESHOOTING                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Issue: Docker not starting                                                        │
│  ──────────────────────────                                                        │
│  Solution: Enable WSL 2 (Windows) or restart Docker Desktop                        │
│                                                                                      │
│  Issue: Port already in use                                                         │
│  ──────────────────────────                                                        │
│  Solution: docker ps → docker stop <container>                                     │
│                                                                                      │
│  Issue: AI API Key not working                                                     │
│  ──────────────────────────────                                                    │
│  Solution: Check key is correct, account has credits, organization allows API       │
│                                                                                      │
│  Issue: Database connection refused                                                 │
│  ────────────────────────────────                                                  │
│  Solution: Wait 10s after docker-compose up, check POSTGRES_PASSWORD              │
│                                                                                      │
│  Issue: CORS error                                                                 │
│  ────────────                                                                      │
│  Solution: Add localhost:3000 to ALLOWED_ORIGINS in .env                          │
│                                                                                      │
│  Issue: Rate limit exceeded                                                        │
│  ─────────────────────                                                              │
│  Solution: Wait 1 minute or increase RATE_LIMIT_MAX in .env                       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Next Steps

| Task | Description | Status |
|------|-------------|--------|
| ✅ AI Setup | Tạo tài khoản OpenAI/Anthropic/Gemini | Done |
| ✅ Docker Setup | Cài đặt Docker Desktop | Done |
| ✅ Database | Setup PostgreSQL với Docker | Done |
| ✅ AI Gateway | Code chi tiết AI chat service | Done |
| ✅ Full Demo | Chạy toàn bộ hệ thống | Done |

**Bây giờ bạn có thể:**
1. Mở http://localhost:3000 để xem Web App
2. Test AI chat với các model khác nhau
3. Thử fallback (tắt 1 provider)
4. Tiếp tục phát triển các features khác!

Chúc bạn thành công! 🎉
