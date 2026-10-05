# AI Café Platform - Setup Guide: Keys & Development Environment

## 1. Tổng Quan Các Tài Khoản Cần Thiết

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         REQUIRED ACCOUNTS & API KEYS                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    AI/LLM PROVIDERS                                        │     │
│  │  ─────────────────────────────────────────────────────────────────────│     │
│  │                                                                          │     │
│  │   🤖 OpenAI (GPT-4, GPT-3.5)                                           │     │
│  │   🤖 Anthropic (Claude)                                                │     │
│  │   🤖 Google AI (Gemini)                                               │     │
│  │   🤖 Hugging Face (Open source models)                                │     │
│  │                                                                          │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    PAYMENT PROVIDERS                                      │     │
│  │  ─────────────────────────────────────────────────────────────────────│     │
│  │                                                                          │     │
│  │   💳 Stripe (International payments)                                   │     │
│  │   💳 VNPay (Vietnam payments)                                          │     │
│  │                                                                          │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    INFRASTRUCTURE                                        │     │
│  │  ─────────────────────────────────────────────────────────────────────│     │
│  │                                                                          │     │
│  │   🖥️ AWS / GCP / DigitalOcean (Cloud hosting)                        │     │
│  │   🐳 Docker Hub (Container registry)                                  │     │
│  │   📦 GitHub (Code repository)                                          │     │
│  │                                                                          │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    COMMUNICATION & NOTIFICATIONS                         │     │
│  │  ─────────────────────────────────────────────────────────────────────│     │
│  │                                                                          │     │
│  │   📱 Twilio (SMS OTP)                                                 │     │
│  │   📧 SendGrid (Email)                                                 │     │
│  │   🔔 Firebase (Push notifications)                                    │     │
│  │                                                                          │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────┐     │
│  │                    MONITORING & ANALYTICS                               │     │
│  │  ─────────────────────────────────────────────────────────────────────│     │
│  │                                                                          │     │
│  │   📊 Sentry (Error tracking)                                          │     │
│  │   📊 Google Analytics (Web analytics)                                  │     │
│  │   📊 Mixpanel (User analytics)                                        │     │
│  │                                                                          │     │
│  └─────────────────────────────────────────────────────────────────────────┘     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Hướng Dẫn Tạo Tài Khoản & Lấy API Keys

### 2.1 OpenAI (GPT-4, GPT-3.5)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         OPENAI SETUP                                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://platform.openai.com                                              │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://platform.openai.com/signup                                           │
│     → Dùng email hoặc đăng nhập Google/Microsoft                                │
│                                                                                      │
│  2. Xác minh số điện thoại                                                        │
│     → Required để sử dụng API                                                    │
│                                                                                      │
│  3. Nạp tiền vào tài khoản (Credits)                                              │
│     → https://platform.openai.com/account/billing/overview                         │
│     → Minimum: $5-$10 để bắt đầu                                                │
│     → Giá: GPT-4 = $0.03/1K tokens, GPT-3.5 = $0.002/1K tokens               │
│                                                                                      │
│  4. Tạo API Key                                                                  │
│     → https://platform.openai.com/api-keys                                          │
│     → Click "Create new secret key"                                               │
│     → Copy và LƯU GIỮ CẨN THẬN (không hiển thị lại)                             │
│                                                                                      │
│  5. Cài đặt giới hạn chi phí (Budget)                                             │
│     → https://platform.openai.com/account/billing/limits                           │
│     → Set monthly limit để tránh phát sinh chi phí                                │
│                                                                                      │
│  Environment Variable:                                                               │
│  ═══════════════════                                                                │
│  OPENAI_API_KEY=sk-...                                                             │
│                                                                                      │
│  Pricing Reference:                                                                │
│  ────────────────                                                                  │
│  GPT-4-8K:     $0.03/1K input tokens, $0.06/1K output tokens                    │
│  GPT-4-32K:    $0.06/1K input tokens, $0.12/1K output tokens                    │
│  GPT-3.5-Turbo: $0.0015/1K input tokens, $0.002/1K output tokens               │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Anthropic (Claude)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         ANTHROPIC SETUP                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://console.anthropic.com                                            │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://console.anthropic.com/signup                                         │
│     → Dùng email hoặc đăng nhập Google                                          │
│                                                                                      │
│  2. Tạo API Key                                                                  │
│     → https://console.anthropic.com/settings/keys                                  │
│     → Click "Create Key"                                                          │
│     → Đặt tên và chọn permissions                                                 │
│     → Copy key ngay (chỉ hiển thị 1 lần!)                                       │
│                                                                                      │
│  3. Cài đặt Billing                                                               │
│     → https://console.anthropic.com/settings/billing                               │
│     → Hiện tại chỉ support credit card                                           │
│                                                                                      │
│  Environment Variable:                                                               │
│  ═══════════════════                                                                │
│  ANTHROPIC_API_KEY=sk-ant-...                                                     │
│                                                                                      │
│  Pricing Reference:                                                                │
│  ────────────────                                                                  │
│  Claude 3 Opus:  $0.015/1K input tokens, $0.075/1K output tokens                │
│  Claude 3 Sonnet: $0.003/1K input tokens, $0.015/1K output tokens               │
│  Claude 3 Haiku:  $0.00025/1K input, $0.00125/1K output tokens                 │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Google AI (Gemini)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         GOOGLE AI SETUP (GEMINI)                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://aistudio.google.com                                              │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản Google                                                      │
│     → Dùng Gmail hiện có là được                                                  │
│                                                                                      │
│  2. Truy cập Google AI Studio                                                     │
│     → https://aistudio.google.com                                                 │
│                                                                                      │
│  3. Tạo API Key                                                                  │
│     → https://aistudio.google.com/app/apikeys                                      │
│     → Click "Create API Key"                                                       │
│     → Copy key                                                                  │
│                                                                                      │
│  4. Enable Gemini API (nếu cần)                                                   │
│     → https://console.cloud.google.com/apis/library/generativelanguage.googleapis.com│
│     → Tạo project mới hoặc chọn project có sẵn                                   │
│     → Enable API                                                                 │
│                                                                                      │
│  Environment Variable:                                                               │
│  ═══════════════════                                                                │
│  GOOGLE_AI_API_KEY=AIza...                                                         │
│                                                                                      │
│  Pricing Reference (Free Tier):                                                   │
│  ────────────────────────                                                           │
│  Gemini 1.0 Pro: 60 requests/minute, 1M tokens/month (FREE!)                     │
│  Gemini 1.5 Pro: 60 requests/minute, 1M tokens/month (FREE!)                     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.4 Hugging Face (Open Source Models)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         HUGGING FACE SETUP                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://huggingface.co                                                  │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://huggingface.co/join                                                  │
│     → Miễn phí!                                                                   │
│                                                                                      │
│  2. Tạo Access Token                                                               │
│     → https://huggingface.co/settings/tokens                                       │
│     → Click "New token"                                                            │
│     → Name:随便取名                                                                │
│     → Type: Read (hoặc Write nếu cần push models)                                 │
│     → Copy token                                                                  │
│                                                                                      │
│  3. (Optional) Subscribe Inference API                                              │
│     → https://huggingface.co/inference-api                                         │
│     → Có free tier: 30K tokens/month                                              │
│                                                                                      │
│  Environment Variable:                                                               │
│  ═══════════════════                                                                │
│  HUGGINGFACE_ACCESS_TOKEN=hf_...                                                   │
│                                                                                      │
│  Popular Open Source Models:                                                        │
│  ─────────────────────────                                                          │
│  Llama 2/3 (Meta)         - Code Llama, Llama Chat                              │
│  Mistral 7B               - High quality, fast inference                         │
│  Falcon                   - TII's flagship model                                  │
│  Vicuna                    - Chatbot fine-tune                                    │
│  StarCoder                - Code generation                                      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.5 Stripe (Payments)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         STRIPE SETUP                                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://dashboard.stripe.com                                            │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://dashboard.stripe.com/register                                        │
│     → Dùng email thật (sẽ verify)                                                 │
│                                                                                      │
│  2. Xác minh email & Activate account                                              │
│     → Check email inbox                                                            │
│     → Complete business information                                               │
│                                                                                      │
│  3. Lấy API Keys                                                                  │
│     → https://dashboard.stripe.com/apikeys                                          │
│     → Public key (pk_test_...) - Dùng frontend                                   │
│     → Secret key (sk_test_...) - Dùng backend (BẢO MẬT!)                        │
│                                                                                      │
│  4. Enable Test Mode                                                               │
│     → Toggle "Test mode" để test không mất tiền                                  │
│     → Dùng test cards: 4242 4242 4242 4242                                       │
│                                                                                      │
│  5. Setup Webhook (cho production)                                                 │
│     → https://dashboard.stripe.com/webhooks                                        │
│     → Add endpoint: https://api.yourdomain.com/webhooks/stripe                    │
│     → Subscribe events: payment_intent.succeeded, payment_intent.failed            │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  STRIPE_PUBLIC_KEY=pk_test_...                                                     │
│  STRIPE_SECRET_KEY=sk_test_...                                                     │
│  STRIPE_WEBHOOK_SECRET=whsec_...                                                   │
│                                                                                      │
│  Pricing:                                                                            │
│  ───────                                                                             │
│  Vietnam: 3.4% + 15,000 VND per successful transaction                            │
│  International: 2.9% + 30¢ per card transaction                                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.6 VNPay (Vietnam Payment)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         VNPAY SETUP                                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://vnpay.vn                                                        │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Liên hệ VNPay để đăng ký                                                     │
│     → Email: hotro@vnpay.vn                                                      │
│     → Hotline: 1900 55 55 77                                                      │
│     → Cần: Đăng ký kinh doanh, Website, Logo                                    │
│                                                                                      │
│  2. Nhận credentials từ VNPay                                                     │
│     → Terminal ID (TmnCode)                                                       │
│     → Hash Secret (SecureKey)                                                     │
│     → API URL (sandbox/production)                                                │
│                                                                                      │
│  3. Setup Sandbox (Testing)                                                        │
│     → Sandbox: https://sandbox.vnpayment.vn/apis                                   │
│     → Production: https://pos.vnpay.vn (cần contract thật)                        │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  VNPAY_TMN_CODE=YOUR_TERMINAL_ID                                                  │
│  VNPAY_HASH_SECRET=YOUR_HASH_SECRET                                               │
│  VNPAY_URL=https://sandbox.vnpayment.vn/                                   │
│  VNPAY_CALLBACK_URL=https://api.yourdomain.com/webhooks/vnpay                     │
│                                                                                      │
│  Test Card (VNPay Sandbox):                                                        │
│  ────────────────────────                                                           │
│  Card Number: 9704198526191432198                                                 │
│  Card Name: NGUYEN VAN A                                                          │
│  Date: 07/15   (MM/YY)                                                          │
│  OTP: 123456                                                                   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.7 Twilio (SMS OTP)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         TWILIO SETUP                                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://www.twilio.com/console                                          │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://www.twilio.com/try-twilio                                           │
│     → Free trial: $15.50 credits                                                  │
│                                                                                      │
│  2. Lấy Credentials                                                                │
│     → https://console.twilio.com/account/api_keys                                  │
│     → Account SID: AC...                                                          │
│     → Auth Token: (hiển thị 1 lần)                                               │
│                                                                                      │
│  3. Tạo SMS Service                                                               │
│     → https://console.twilio.com/messaging/services                               │
│     → Create Messaging Service                                                    │
│     → Lấy Messaging Service SID                                                   │
│                                                                                      │
│  4. Đăng ký Số điện thoại (Sender)                                               │
│     → https://console.twilio.com/phone-numbers/registered                          │
│     → Mua số hoặc dùng số có sẵn                                                │
│     → Vietnam: Cần đăng ký với nhà mạng (Viettel, Mobifone, Vinaphone)          │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  TWILIO_ACCOUNT_SID=AC...                                                         │
│  TWILIO_AUTH_TOKEN=...                                                            │
│  TWILIO_PHONE_NUMBER=+84...                                                       │
│  TWILIO_MESSAGING_SERVICE_SID=MG...                                               │
│                                                                                      │
│  Pricing (Vietnam):                                                                │
│  ────────────────────                                                               │
│  SMS quốc tế đến VN: $0.0289 - $0.0459/SMS                                     │
│  Verify API (OTP): ~$0.05/SMS                                                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.8 SendGrid (Email)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         SENDGRID SETUP                                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://app.sendgrid.com                                                 │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản (Free Tier: 100 emails/day)                                │
│     → https://signup.sendgrid.com                                                 │
│     → Verify email                                                                 │
│                                                                                      │
│  2. Tạo API Key                                                                  │
│     → https://app.sendgrid.com/settings/api_keys                                  │
│     → Create API Key → Full Access hoặc Restricted                                │
│     → Copy key ngay (chỉ hiển thị 1 lần)                                         │
│                                                                                      │
│  3. Verify Sender (Required!)                                                      │
│     → https://app.sendgrid.com/settings/sender_auth/senders                       │
│     → Create New Sender                                                          │
│     → Verify email đó                                                             │
│                                                                                      │
│  4. Setup Domain Authentication (Production)                                       │
│     → https://app.sendgrid.com/settings/sender_auth/domain_authentication          │
│     → Thêm DNS records (MX, TXT, CNAME)                                          │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  SENDGRID_API_KEY=SG....                                                          │
│  SENDGRID_FROM_EMAIL=noreply@aicafe.vn                                            │
│  SENDGRID_FROM_NAME=AICafe Platform                                               │
│                                                                                      │
│  Free Tier:                                                                          │
│  ───────────                                                                           │
│  100 emails/day, 5,000 emails/month                                              │
│  Production: Từ $14.95/month cho 50K emails                                       │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.9 Firebase (Push Notifications)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         FIREBASE SETUP                                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://console.firebase.google.com                                      │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Tạo Firebase Project                                                          │
│     → https://console.firebase.google.com                                        │
│     → Add project → Đặt tên project                                              │
│     → Enable Google Analytics (recommended)                                       │
│                                                                                      │
│  2. Register Apps                                                                  │
│     → Add app: Android / iOS / Web                                                │
│     → Download google-services.json (Android)                                      │
│     → Download GoogleService-Info.plist (iOS)                                     │
│                                                                                      │
│  3. Lấy Firebase Config                                                           │
│     → Project Settings → Your apps → Config                                       │
│     → Web: apiKey, authDomain, projectId, messagingSenderId, appId              │
│                                                                                      │
│  4. Enable Firebase Cloud Messaging (FCM)                                           │
│     → Messaging in Firebase Console                                               │
│     → Create first campaign hoặc use API                                          │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  FIREBASE_API_KEY=...                                                             │
│  FIREBASE_AUTH_DOMAIN=aicafe-xxx.firebaseapp.com                                  │
│  FIREBASE_PROJECT_ID=aicafe-xxx                                                   │
│  FIREBASE_MESSAGING_SENDER_ID=...                                                 │
│  FIREBASE_APP_ID=...                                                              │
│                                                                                      │
│  Pricing (Free Tier):                                                              │
│  ─────────────────────                                                             │
│  Cloud Messaging: Free (unlimited)                                                │
│  Notifications: Free                                                              │
│  Storage: 5GB                                                                      │
│  Firestore: 1GB                                                                   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.10 Sentry (Error Tracking)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         SENTRY SETUP                                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Website: https://sentry.io                                                        │
│                                                                                      │
│  Steps:                                                                              │
│  ══════                                                                             │
│                                                                                      │
│  1. Đăng ký tài khoản                                                              │
│     → https://sentry.io/signup/                                                   │
│     → Free tier: 5 projects, 5K events/month                                      │
│                                                                                      │
│  2. Tạo Organization & Project                                                     │
│     → Create Organization                                                         │
│     → Create Project: Choose platform (Node, Python, React, etc.)                 │
│                                                                                      │
│  3. Lấy DSN (Data Source Name)                                                     │
│     → Project Settings → Client Keys (DSN)                                        │
│     → https://xxx@sentry.io/xxx                                                   │
│                                                                                      │
│  4. Install SDK                                                                   │
│     → Backend: npm install @sentry/node                                           │
│     → Frontend: npm install @sentry/react                                         │
│                                                                                      │
│  Environment Variables:                                                            │
│  ═════════════════════                                                              │
│  SENTRY_DSN=https://xxx@sentry.io/xxx                                             │
│  SENTRY_ORG=your-org                                                             │
│  SENTRY_PROJECT=your-project                                                     │
│  SENTRY_AUTH_TOKEN=... (for CI/CD)                                                │
│                                                                                      │
│  Pricing:                                                                            │
│  ───────                                                                             │
│  Free: 5 projects, 5K errors/month, 30 days retention                             │
│  Developer: $8/month - 10 projects, 50K events                                   │
│  Team: $80/month - unlimited projects, 500K events                               │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Setup Môi Trường Development

### 3.1 Cài Đặt Công Cụ Cần Thiết

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DEVELOPMENT TOOLS                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  REQUIRED TOOLS                                                                    │
│  ═══════════════                                                                    │
│                                                                                      │
│  1. Git                                                                             │
│     ────                                                                             │
│     Windows: https://git-scm.com/download/win                                      │
│     Mac: brew install git                                                          │
│                                                                                      │
│  2. Node.js (v18+)                                                                  │
│     ──────────────                                                                  │
│     Windows: https://nodejs.org/en/download/                                       │
│     Mac: brew install node                                                         │
│     Linux: curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -     │
│                                                                                      │
│     Verify: node --version && npm --version                                       │
│                                                                                      │
│  3. Docker Desktop                                                                  │
│     ─────────────────                                                                  │
│     Windows: https://www.docker.com/products/docker-desktop/                      │
│     Mac: https://www.docker.com/products/docker-desktop/                          │
│                                                                                      │
│     Requirements:                                                                  │
│     • Windows: WSL 2, 4GB RAM, 20GB disk                                         │
│     • Mac: macOS 10.15+, 4GB RAM                                                 │
│                                                                                      │
│  4. Java 17+ (cho Spring Boot services)                                           │
│     ────────────────────────────────────                                           │
│     Windows: https://adoptium.net/                                                │
│     Mac: brew install openjdk@17                                                   │
│     Linux: sudo apt install openjdk-17-jdk                                        │
│                                                                                      │
│  5. Go 1.21+ (cho Go services)                                                   │
│     ──────────────────────────                                                     │
│     Windows: https://go.dev/dl/                                                   │
│     Mac: brew install go                                                           │
│     Linux: wget https://go.dev/dl/go1.21.linux-amd64.tar.gz && \                 │
│            sudo tar -C /usr/local -xzf go1.21.linux-amd64.tar.gz                  │
│                                                                                      │
│  6. VS Code (Recommended IDE)                                                     │
│     ────────────────────────                                                        │
│     https://code.visualstudio.com/                                                │
│                                                                                      │
│     Extensions:                                                                    │
│     • Docker                                                                   │
│     • Kubernetes                                                               │
│     • ESLint                                                                 │
│     • Prettier                                                                │
│     • GitLens                                                                │
│     • Thunder Client (API testing)                                             │
│     • Remote - Containers                                                      │
│                                                                                      │
│  7. DBeaver / TablePlus (Database GUI)                                           │
│     ─────────────────────────────────────                                         │
│     DBeaver: https://dbeaver.io/                                                │
│     TablePlus: https://tableplus.com/                                            │
│                                                                                      │
│  8. Postman / Insomnia (API Testing)                                             │
│     ────────────────────────────────────                                          │
│     Postman: https://www.postman.com/downloads/                                  │
│     Insomnia: https://insomnia.rest/download                                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Clone Project & Setup

```bash
# 1. Clone repository
git clone https://github.com/your-org/ai-cafe-platform.git
cd ai-cafe-platform

# 2. Copy environment file
cp .env.example .env

# 3. Mở .env và điền các API keys đã lấy ở trên
```

### 3.3 Sample .env File

```bash
# ================================================
# AI CAFE PLATFORM - DEVELOPMENT ENV
# ================================================

# ================================================
# DATABASE (Local Docker)
# ================================================
DATABASE_URL=postgres://aicafe:devpassword@localhost:5432/aicafe_dev
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_NAME=aicafe_dev
DATABASE_USER=aicafe
DATABASE_PASSWORD=devpassword

# ================================================
# REDIS
# ================================================
REDIS_URL=redis://localhost:6379

# ================================================
# KAFKA
# ================================================
KAFKA_BROKERS=localhost:9092
KAFKA_TOPIC_ORDERS=orders-events
KAFKA_TOPIC_PAYMENTS=payment-events
KAFKA_TOPIC_NOTIFICATIONS=notification-events

# ================================================
# AI PROVIDERS
# ================================================
# OpenAI (GPT-4)
OPENAI_API_KEY=sk-...

# Anthropic (Claude)
ANTHROPIC_API_KEY=sk-ant-...

# Google AI (Gemini)
GOOGLE_AI_API_KEY=AIza...

# Hugging Face
HUGGINGFACE_ACCESS_TOKEN=hf_...

# ================================================
# PAYMENT
# ================================================
# Stripe
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# VNPay
VNPAY_TMN_CODE=YOUR_TMN_CODE
VNPAY_HASH_SECRET=YOUR_HASH_SECRET
VNPAY_URL=https://sandbox.vnpayment.vn/apis

# ================================================
# SMS & EMAIL
# ================================================
# Twilio
TWILIO_ACCOUNT_SID=AC...
TWILIO_AUTH_TOKEN=...
TWILIO_PHONE_NUMBER=+84...
TWILIO_MESSAGING_SERVICE_SID=MG...

# SendGrid
SENDGRID_API_KEY=SG...
SENDGRID_FROM_EMAIL=noreply@aicafe.vn

# ================================================
# FIREBASE
# ================================================
FIREBASE_API_KEY=...
FIREBASE_AUTH_DOMAIN=aicafe-xxx.firebaseapp.com
FIREBASE_PROJECT_ID=aicafe-xxx
FIREBASE_MESSAGING_SENDER_ID=...
FIREBASE_APP_ID=...

# ================================================
# MONITORING
# ================================================
SENTRY_DSN=https://xxx@sentry.io/xxx

# ================================================
# APP CONFIG
# ================================================
NODE_ENV=development
API_PORT=8000
WEB_PORT=3000
ADMIN_PORT=3001

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-in-production
JWT_EXPIRES_IN=7d

# CORS
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001

# ================================================
# CLOUD (Optional - for production)
# ================================================
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_REGION=ap-southeast-1
S3_BUCKET=aicafe-assets
```

---

## 4. Setup Backend (BE) Services

### 4.1 User Service (Java Spring Boot)

```bash
# Navigate to service
cd services/user-service

# Build
./mvnw clean package -DskipTests

# Run locally
./mvnw spring-boot:run

# Or with Docker
docker build -t aicafe/user-service .
docker run -p 8081:8080 --env-file ../../.env aicafe/user-service
```

### 4.2 Auth Service (Go)

```bash
# Navigate to service
cd services/auth-service

# Install dependencies
go mod download

# Run
go run cmd/server/main.go

# Build
go build -o auth-service ./cmd/server
./auth-service
```

### 4.3 AI Gateway (Node.js)

```bash
# Navigate to service
cd services/ai-gateway

# Install dependencies
npm install

# Run development
npm run dev

# Run production
npm run build
npm start
```

### 4.4 Other Services

```bash
# Order Service (Java)
cd services/order-service
./mvnw spring-boot:run

# Payment Service (Java)
cd services/payment-service
./mvnw spring-boot:run

# Credit Service (Go)
cd services/credit-service
go run cmd/server/main.go

# Notification Service (Go)
cd services/notification-service
go run cmd/server/main.go
```

---

## 5. Setup Frontend (FE) Apps

### 5.1 Web App (Customer - Next.js)

```bash
# Navigate
cd apps/web

# Install dependencies
npm install

# Run development
npm run dev
# → http://localhost:3000

# Build for production
npm run build

# Start production
npm start
```

### 5.2 Admin Dashboard (Next.js)

```bash
# Navigate
cd apps/admin

# Install dependencies
npm install

# Run development
npm run dev
# → http://localhost:3001

# Build for production
npm run build
npm start
```

### 5.3 POS App (React Native)

```bash
# Navigate
cd apps/pos

# Install dependencies
npm install

# iOS
npx pod install
npx react-native run-ios

# Android
npx react-native run-android
```

### 5.4 Mobile App (React Native)

```bash
# Navigate
cd apps/mobile

# Install dependencies
npm install

# iOS
npx pod install
npx react-native run-ios

# Android
npx react-native run-android
```

---

## 6. Setup Database (PostgreSQL)

### 6.1 Start PostgreSQL with Docker

```bash
# Run PostgreSQL container
docker run -d \
  --name aicafe-postgres \
  -e POSTGRES_DB=aicafe_dev \
  -e POSTGRES_USER=aicafe \
  -e POSTGRES_PASSWORD=devpassword \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15-alpine

# Check logs
docker logs -f aicafe-postgres

# Connect with DBeaver/TablePlus
# Host: localhost
# Port: 5432
# Database: aicafe_dev
# User: aicafe
# Password: devpassword
```

### 6.2 Run Database Migrations

```bash
# User Service (Spring Boot JPA)
cd services/user-service
./mvnw flyway:migrate

# Auth Service (Go - using golang-migrate)
cd services/auth-service
migrate -path ./db/migrations -database $DATABASE_URL up

# AI Gateway (Node.js - using Knex)
cd services/ai-gateway
npm run db:migrate
```

---

## 7. Setup AI Chat API

### 7.1 AI Gateway Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         AI GATEWAY ARCHITECTURE                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│    Client Request                                                                  │
│         │                                                                          │
│         ▼                                                                          │
│  ┌──────────────┐                                                                │
│  │   AI Gateway │  (services/ai-gateway)                                        │
│  │   (Node.js)  │                                                                │
│  └──────┬───────┘                                                                │
│         │                                                                          │
│         ├─────────────────┬─────────────────┬─────────────────┐                   │
│         │                 │                 │                 │                    │
│         ▼                 ▼                 ▼                 ▼                    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │
│  │   OpenAI     │ │  Anthropic  │ │  Google AI   │ │   Hugging   │        │
│  │  (GPT-4)    │ │ (Claude 3)  │ │  (Gemini)   │ │    Face     │        │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘        │
│                                                                                      │
│                                                                                      │
│  Features:                                                                          │
│  ├─ Load balancing across providers                                               │
│  ├─ Fallback机制 (provider này fail → dùng provider khác)                        │
│  ├─ Token counting & rate limiting                                                │
│  ├─ Caching với Redis                                                            │
│  ├─ Cost tracking per user                                                       │
│  └─ Chat history management                                                      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 AI Gateway Code Example

```javascript
// services/ai-gateway/src/services/ai-providers.ts

import OpenAI from 'openai';
import Anthropic from '@anthropic-ai/sdk';
import { HfInference } from '@huggingface/inference';

class AIService {
  private openai: OpenAI;
  private anthropic: Anthropic;
  private hf: HfInference;

  constructor() {
    this.openai = new OpenAI({
      apiKey: process.env.OPENAI_API_KEY,
    });
    
    this.anthropic = new Anthropic({
      apiKey: process.env.ANTHROPIC_API_KEY,
    });
    
    this.hf = new HfInference(process.env.HUGGINGFACE_ACCESS_TOKEN);
  }

  async chat(model: string, messages: any[], userId: string) {
    const startTime = Date.now();
    
    try {
      let response;
      
      switch (model) {
        case 'gpt-4':
        case 'gpt-3.5-turbo':
          response = await this.openai.chat.completions.create({
            model,
            messages,
            temperature: 0.7,
            max_tokens: 2000,
          });
          return {
            content: response.choices[0].message.content,
            provider: 'openai',
            tokens: response.usage.total_tokens,
            latency: Date.now() - startTime,
          };

        case 'claude-3-opus':
        case 'claude-3-sonnet':
          const claudeModel = model.replace('claude-3-', 'claude-3-');
          response = await this.anthropic.messages.create({
            model: claudeModel,
            max_tokens: 2000,
            messages,
          });
          return {
            content: response.content[0].type === 'text' ? response.content[0].text : '',
            provider: 'anthropic',
            tokens: response.usage.input_tokens + response.usage.output_tokens,
            latency: Date.now() - startTime,
          };

        case 'gemini-pro':
          // Google AI implementation
          response = await this.callGemini(model, messages);
          return response;

        case 'llama-2':
        case 'mistral-7b':
          response = await this.hf.chatCompletion({
            model: model,
            messages,
          });
          return {
            content: response.choices[0].message.content,
            provider: 'huggingface',
            tokens: response.usage?.total_tokens || 0,
            latency: Date.now() - startTime,
          };

        default:
          throw new Error(`Unknown model: ${model}`);
      }
    } catch (error) {
      // Log error to Sentry
      console.error('AI Provider Error:', error);
      throw error;
    }
  }

  // Fallback: try next provider if one fails
  async chatWithFallback(messages: any[]) {
    const providers = ['openai', 'anthropic', 'huggingface'];
    
    for (const provider of providers) {
      try {
        const result = await this.chatWithProvider(provider, messages);
        return result;
      } catch (error) {
        console.warn(`Provider ${provider} failed, trying next...`);
        continue;
      }
    }
    
    throw new Error('All AI providers failed');
  }
}

export const aiService = new AIService();
```

### 7.3 API Endpoint

```typescript
// services/ai-gateway/src/routes/chat.ts

import { Router } from 'express';
import { aiService } from '../services/ai-providers';
import { authMiddleware } from '../middleware/auth';
import { rateLimiter } from '../middleware/rateLimiter';

const router = Router();

// POST /api/v1/ai/chat
router.post(
  '/chat',
  authMiddleware,
  rateLimiter({ max: 50, windowMs: 60000 }), // 50 requests/minute
  async (req, res) => {
    try {
      const { model, messages, sessionId } = req.body;
      const userId = req.user.id;

      // Validate model
      const allowedModels = [
        'gpt-4',
        'gpt-3.5-turbo',
        'claude-3-opus',
        'claude-3-sonnet',
        'gemini-pro',
        'llama-2',
        'mistral-7b',
      ];

      if (!allowedModels.includes(model)) {
        return res.status(400).json({ error: 'Invalid model' });
      }

      // Check user credits
      const userCredits = await checkUserCredits(userId);
      if (userCredits <= 0) {
        return res.status(402).json({ error: 'Insufficient credits' });
      }

      // Call AI
      const response = await aiService.chat(model, messages, userId);

      // Deduct credits (simplified)
      await deductCredits(userId, response.tokens);

      // Save to chat history
      await saveChatHistory(userId, sessionId, model, messages, response);

      res.json({
        content: response.content,
        model,
        provider: response.provider,
        tokens: response.tokens,
        remainingCredits: userCredits - Math.ceil(response.tokens / 1000),
      });
    } catch (error) {
      console.error('Chat error:', error);
      res.status(500).json({ error: 'AI service error' });
    }
  }
);

// GET /api/v1/ai/models
router.get('/models', async (req, res) => {
  res.json({
    models: [
      { id: 'gpt-4', name: 'GPT-4', provider: 'OpenAI', pricePer1K: 0.06 },
      { id: 'gpt-3.5-turbo', name: 'GPT-3.5 Turbo', provider: 'OpenAI', pricePer1K: 0.002 },
      { id: 'claude-3-opus', name: 'Claude 3 Opus', provider: 'Anthropic', pricePer1K: 0.075 },
      { id: 'claude-3-sonnet', name: 'Claude 3 Sonnet', provider: 'Anthropic', pricePer1K: 0.015 },
      { id: 'gemini-pro', name: 'Gemini Pro', provider: 'Google', pricePer1K: 0.001 },
      { id: 'llama-2', name: 'Llama 2', provider: 'HuggingFace', pricePer1K: 0 }, // Free
    ],
  });
});

export default router;
```

---

## 8. Quick Start Command Reference

```bash
#!/bin/bash
# ================================================
# AI CAFE PLATFORM - QUICK START
# ================================================

# 1. Start Infrastructure
docker-compose up -d postgres redis kafka

# 2. Wait for services
sleep 10

# 3. Run migrations
cd services/user-service && ./mvnw flyway:migrate && cd ../..
cd services/auth-service && migrate -path ./db/migrations up && cd ../..

# 4. Start Backend Services
cd services/user-service && ./mvnw spring-boot:run &
cd services/auth-service && go run cmd/server/main.go &
cd services/order-service && ./mvnw spring-boot:run &
cd services/ai-gateway && npm run dev &

# 5. Start Frontend Apps
cd apps/web && npm run dev &
cd apps/admin && npm run dev &

# 6. Verify all services
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8084/health             # Auth Service
curl http://localhost:8086/health             # AI Gateway
curl http://localhost:3000                    # Web App
curl http://localhost:3001                    # Admin App

echo "✅ All services started!"
echo "🌐 Web App: http://localhost:3000"
echo "👨‍💼 Admin: http://localhost:3001"
echo "🔌 API: http://localhost:8000"
```

---

## 9. Troubleshooting Common Issues

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         COMMON ISSUES & SOLUTIONS                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Issue: Docker Desktop not starting (Windows)                                        │
│  ─────────────────────────────────────────                                         │
│  Solution: Enable WSL 2 in Windows Features, update BIOS virtualization          │
│                                                                                      │
│  Issue: Port already in use                                                         │
│  ────────────────────────                                                           │
│  Solution: docker ps → docker stop <container_id>                                  │
│                                                                                      │
│  Issue: API Key not working (OpenAI)                                                │
│  ─────────────────────────────────────────                                          │
│  Solution: Check if you have credits, verify key is correct, check organization   │
│                                                                                      │
│  Issue: Database connection refused                                                 │
│  ───────────────────────────────                                                    │
│  Solution: docker-compose up -d postgres → wait 10s → retry                       │
│                                                                                      │
│  Issue: CORS error in browser                                                       │
│  ───────────────────────────                                                        │
│  Solution: Add localhost:3000 to ALLOWED_ORIGINS in backend config                 │
│                                                                                      │
│  Issue: React Native build failed                                                   │
│  ─────────────────────────────                                                       │
│  Solution: npx pod install (iOS), clean build (Android)                          │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Next Steps

| Step | Task | Status |
|------|------|--------|
| 1 | Tạo tài khoản & lấy API keys | ⬜ |
| 2 | Cài đặt development tools | ⬜ |
| 3 | Clone project & setup .env | ⬜ |
| 4 | Start infrastructure (Docker) | ⬜ |
| 5 | Run database migrations | ⬜ |
| 6 | Start backend services | ⬜ |
| 7 | Start frontend apps | ⬜ |
| 8 | Test AI chat feature | ⬜ |

---

Bạn đã có tài khoản nào trong số trên chưa? Tôi có thể giúp bạn setup chi tiết từng phần!
